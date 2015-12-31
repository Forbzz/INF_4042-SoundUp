package org.esiea.dondin_ta.soundup.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class Db extends SQLiteOpenHelper{

    public Db(Context context) {
//    public Db(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
//        super(context, name, factory, version);
        super(context, "alphaDb", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE user(" +
                "_id INTEGER NOT NULL PRIMARY KEY," +
//                "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "username varchar(32) DEFAULT 'visitor'," +
                "password varchar(32)) ");

        db.execSQL("CREATE TABLE record(" +
                "_id INTEGER NOT NULL PRIMARY KEY," +
//                "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "name varchar(32)," +
                "createTime char(10)," +
                "length INTEGER" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
