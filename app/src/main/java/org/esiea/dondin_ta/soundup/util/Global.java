package org.esiea.dondin_ta.soundup.util;

import android.os.Environment;

import java.util.Calendar;

/**
 * Created by huangshihe on 2015/7/16.
 */
public class Global {

    /**
     *
     */
    public static final String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/alpha/records/";
//    public static final String PATH = "/";

    public static final int TYPE_AWR = 1;

    public static final int TYPE_WAV = 0;


    public static String getTime(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int days = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minutes = cal.get(Calendar.MINUTE);
        int seconds = cal.get(Calendar.SECOND);
        int MI = cal.get(Calendar.MILLISECOND);
        return "" + year + month + days + hour + minutes + seconds + MI;
    }
}
