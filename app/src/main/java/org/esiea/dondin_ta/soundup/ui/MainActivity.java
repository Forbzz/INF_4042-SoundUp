package org.esiea.dondin_ta.soundup.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.esiea.dondin_ta.soundup.R;
import org.esiea.dondin_ta.soundup.dao.RecordDao;
import org.esiea.dondin_ta.soundup.util.Global;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    private Button startRecordBtn;
    private Button stopRecordBtn;
    private Button showListBtn;
    private Button settingsBtn;
    private Button shareBtn;
    private Timer timer = new Timer();

    private TextView showTimeTV;

    private RecordDao recordDao;
    private org.esiea.dondin_ta.soundup.model.BaseRecord record;

    SharedPreferences settings;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            if(record != null){
                showTimeTV.setText(record.getRecordTime());
            }
        }
    };

    private ActionBar bar;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bar = getActionBar();
      //  bar.hide();

        showTimeTV = (TextView) findViewById(R.id.showTimeTV);
        showTimeTV.setText("00:00");


        if (!Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
            Toast.makeText(MainActivity.this,R.string.nosd, Toast.LENGTH_LONG).show();
        }else{
            File rootLocation = new File(Global.PATH);
            if (!rootLocation.exists()) {
                rootLocation.mkdirs();
            }
        }

        settings = getSharedPreferences("org.esiea.dondin_ta.soundup_preferences", Context.MODE_PRIVATE);
        recordDao = new RecordDao(this);

        startRecordBtn = (Button) findViewById(R.id.startRecordBtn);
        startRecordBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if(record == null){
                            switch (settings.getString("example_list",""+Global.TYPE_WAV)){
                                case ""+Global.TYPE_AWR:
                                    record = new org.esiea.dondin_ta.soundup.model.RecordAwr();
                                    break;
                                case ""+Global.TYPE_WAV:
                                    record = new org.esiea.dondin_ta.soundup.model.RecordWav();
                                    break;
                                default:
                                    record = new org.esiea.dondin_ta.soundup.model.RecordWav();
                            }
                        }
                        record.startRecord();
                        setTimerTask();

                        stopRecordBtn.setEnabled(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        record.onPause();
                        break;
                }
                return false;
            }
        });

        stopRecordBtn = (Button) findViewById(R.id.stopRecordBtn);
        stopRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record.stopRecord();
                showTimeTV.setText(record.getRecordTime());
                //save
                if (recordDao != null && recordDao.addRecord(record)) {
                    Toast.makeText(MainActivity.this, R.string.registered, Toast.LENGTH_LONG).show();
                }
                else if(recordDao == null){
                    Toast.makeText(MainActivity.this, R.string.unregistered, Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(MainActivity.this,  R.string.unregistered, Toast.LENGTH_LONG).show();
                }
                record = null;
                showTimeTV.setText("00:00");
                stopRecordBtn.setEnabled(false);
                startActivity(new Intent(MainActivity.this, RecordListActivity.class));
            }
        });
        stopRecordBtn.setEnabled(false);

        showListBtn = (Button) findViewById(R.id.showListBtn);
        showListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeTV.setText("00:00");
                startActivity(new Intent(MainActivity.this, RecordListActivity.class));
            }
        });

        settingsBtn = (Button) findViewById(R.id.settingsBtn);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this,SettingsActivity.class));
            }
        });

        shareBtn = (Button) findViewById(R.id.shareBtn);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,InstructionActivity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(record != null){
            record.stopRecord();
            //save
            if(recordDao.addRecord(record)){
                Toast.makeText(MainActivity.this, R.string.registered, Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(MainActivity.this, R.string.unregistered, Toast.LENGTH_LONG).show();
            }
            record = null;
        }
        recordDao.close();
        recordDao = null;
    }

    private void setTimerTask(){
        timer.schedule(new TimerTask(){
            @Override
            public void run(){
                Message message = new Message();
                handler.sendMessage(message);
            }
        }, 0, 1000);
    }

}