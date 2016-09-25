package com.titter.past3.titter.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by SMILECS on 9/14/16.
 */
public class Dbhelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    public static final String DATABASE_NAME =  "titter";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DataContract.Data.TABLE_NAME;

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DataContract.Data.TABLE_NAME + " (" +
                    DataContract.Data._ID + " INTEGER PRIMARY KEY," +
                    DataContract.Data.AVAILABLE+ TEXT_TYPE + COMMA_SEP +
                    DataContract.Data.TYPE+ TEXT_TYPE + COMMA_SEP +
                    DataContract.Data.URL+ TEXT_TYPE + COMMA_SEP +
                    DataContract.Data.WEBURL+ TEXT_TYPE + COMMA_SEP +
                    DataContract.Data.TAG + TEXT_TYPE + " )";

    public Dbhelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
