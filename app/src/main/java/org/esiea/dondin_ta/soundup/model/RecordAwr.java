package org.esiea.dondin_ta.soundup.model;

import android.media.MediaPlayer;
import android.media.MediaRecorder;

import org.esiea.dondin_ta.soundup.util.Global;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class RecordAwr implements BaseRecord{

    public RecordAwr(){
        list = new ArrayList<String>();
        isPause = false;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getName() {
        if(name == null || "".equals(name.trim())){
            name = Global.getTime() + ".amr";
//            name = Global.getTime() + ".3gp";
        }
        return name;
    }

    public void setName(String name) {
        if(name == null || "".equals(name.trim())){
            this.name = Global.getTime() + ".amr";
        }else{
            this.name = name;
        }
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public File getRecordFile() {
        return recordFile;
    }

    public void setRecordFile(File recordFile) {
        this.recordFile = recordFile;
    }

    private int _id;
    private String name;
    private Date createTime;
    private long length;
    private int type = Global.TYPE_AWR;
    private File recordFile;

    ///////////////////////////////////

    private int second;

    private int minute;
    private Timer timer;


    private File tempFile;


    private ArrayList<String> list;


    private boolean isPause;

    private MediaPlayer mPlayer = null;
    private MediaRecorder mRecorder = null;

    public String getRecordTime(){
        return minute + ":" + second;
    }

    public void startRecord(){
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                second++;
                if (second >= 60) {
                    second = 0;
                    minute++;
                }
            }
        };
        timer.schedule(timerTask, 1000, 1000);


        File rootLocation = new File(Global.PATH);
        if (!rootLocation.exists()) {
            rootLocation.mkdirs();
        }
        mRecorder = new MediaRecorder();

        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
        tempFile = new File(Global.PATH + Global.getTime() + ".amr");
        mRecorder.setOutputFile(tempFile.getAbsolutePath());


        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (Exception e) {
            System.out.println("prepare() or start() failed");
        }
        isPause = false;
    }

    public void stopRecord(){
        //If there is no pause, that is recording, stop recording this paragraph, and add list
        if(!isPause){
            timer.cancel();
            if(mRecorder != null){
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
            }
            list.add(tempFile.getName());
            isPause = false;
        }
        getInputCollection();
        setLength(recordFile.length());
        setCreateTime(new Date());
    }

    /**
     *Pause
     */
    public void onPause() {
        isPause = true;
        timer.cancel();
        if(mRecorder != null){


                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;

        }
        list.add(tempFile.getName());
    }

    /**
     * Merge temporary files
     */
    public void getInputCollection() {

        recordFile = new File(Global.PATH + getName());
        FileOutputStream fileOutputStream = null;

        if (!recordFile.exists()) {
            try {
                recordFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            fileOutputStream = new FileOutputStream(recordFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < list.size(); i++) {
            File file = new File(Global.PATH + (String) list.get(i));
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] myByte = new byte[fileInputStream.available()];
                int length = myByte.length;
                if (i == 0) {
                    while (fileInputStream.read(myByte) != -1) {
                        fileOutputStream.write(myByte, 0, length);
                    }
                }
                else {
                    while (fileInputStream.read(myByte) != -1) {
                        fileOutputStream.write(myByte, 6, length - 6);
                    }
                }
                fileOutputStream.flush();
                fileInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        deleteListRecord();
    }


    private void deleteListRecord() {
        for (int i = 0; i < list.size(); i++) {
            File file = new File(Global.PATH + (String) list.get(i));
            if (file.exists()) {
                file.delete();
            }
        }
        list = null;
    }

}
