package org.esiea.dondin_ta.soundup.model;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import org.esiea.dondin_ta.soundup.util.Global;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class RecordWav implements BaseRecord {

    private boolean isPause;
    private boolean isStop;
    private int _id;
    private String name;
    private Date createTime;
    private long length;
    private int type = Global.TYPE_WAV;
    private File recordFile;


    private int second;

    private int minute;
    private Timer timer;

    Thread delay = new Thread();
//    long delayTime = 200;


    private static final String tempAudioName = Global.PATH + "alpha.raw";

    //
    private int audioSource = MediaRecorder.AudioSource.MIC;

    private static int sampleRateInHz = 44100;

    private static int channelConfig = AudioFormat.CHANNEL_IN_STEREO;

    private static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

    private int bufferSizeInBytes = 0;

    private AudioRecord audioRecord;

    public RecordWav(){
        isPause = false;
        isStop = true;
    }

    @Override
    public String getRecordTime() {
        return minute + ":" + second;
    }

    @Override
    public void startRecord() {
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

        isPause = false;
        isStop = false;

        if(audioRecord == null){

            bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz,
                    channelConfig, audioFormat);

            audioRecord = new AudioRecord(audioSource, sampleRateInHz,
                    channelConfig, audioFormat, bufferSizeInBytes);
            audioRecord.startRecording();

            new Thread(new AudioRecordThread()).start();
        }
    }

    @Override
    public void stopRecord() {
        timer.cancel();
        isStop = true;
        close();
        setCreateTime(new Date());

//        setLength(recordFile.length());
    }

    @Override
    public void onPause() {
        timer.cancel();
//        try {
//            delay.sleep(delayTime);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        isPause = true;
    }

    @Override
    public String getName() {
        if(name == null || "".equals(name.trim())){
            name = Global.getTime() + ".wav";
        }
        return name;
    }

    @Override
    public void setName(String name) {
        if(name == null || "".equals(name.trim())){
            this.name = Global.getTime() + ".wav";
        }else{
            this.name = name;
        }
    }

    @Override
    public void setRecordFile(File recordFile) {
        this.recordFile = recordFile;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public Date getCreateTime() {
        if(createTime == null){
            createTime = new Date();
        }
        return createTime;
    }

    @Override
    public long getLength() {
        return length;
    }

    @Override
    public void setLength(long length) {
        this.length = length;
    }

    class AudioRecordThread implements Runnable {
        @Override
        public void run() {
            //
            writeDateTOFile();
            if(recordFile == null){
                recordFile = new File(Global.PATH + getName());
            }
            //
            copyWaveFile(tempAudioName, recordFile.getAbsolutePath());
        }
    }


    private void writeDateTOFile() {

        byte[] audiodata = new byte[bufferSizeInBytes];
        FileOutputStream fos = null;
        int readsize = 0;
        try {
            File file = new File(tempAudioName);
            if (file.exists()) {
                file.delete();
            }
            fos = new FileOutputStream(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (isStop == false) {
            readsize = audioRecord.read(audiodata, 0, bufferSizeInBytes);
            if (AudioRecord.ERROR_INVALID_OPERATION != readsize) {
                if(isPause == false) {
                    try {
                        fos.write(audiodata);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        try {

            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void copyWaveFile(String inFilename, String outFilename) {
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = sampleRateInHz;
        int channels = 2;
        long byteRate = 16 * sampleRateInHz * channels / 8;
        byte[] data = new byte[bufferSizeInBytes];
        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;
            writeWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate);
            while (in.read(data) != -1) {
                out.write(data);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void writeWaveFileHeader(FileOutputStream out, long totalAudioLen,
                                     long totalDataLen, long longSampleRate, int channels, long byteRate)
            throws IOException {
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = 16; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.write(header, 0, 44);
    }

    private void close() {
        if (audioRecord != null) {
            System.out.println("stopRecord");

            isStop = true;
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
    }
}
