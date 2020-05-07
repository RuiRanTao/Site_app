package com.example.bottom.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.bottom.Constants;


public class DatabaseHelper_user extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";


    public DatabaseHelper_user(@Nullable Context context) {
        super(context, Constants.DATABASE_NAME, null, 1);
//    SQLiteDatabase db = this.getWritableDatabase();
        Log.d(TAG,"数据库");

    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据库时回调
        db.execSQL("create table if not exists " + Constants.USER_TABLE_NAME + "(ID integer,floor integer,place varchar,num integer,val integer)");
        Log.d(TAG,"创建数据库");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //升级数据库时回调
        db.execSQL("DROP TABLE IF EXISTS " + Constants.USER_TABLE_NAME);
        onCreate(db);
        Log.d(TAG,"升级数据库");
    }
}
