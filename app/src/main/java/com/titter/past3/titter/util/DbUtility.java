package com.titter.past3.titter.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.titter.past3.titter.model.feedModel;

import java.util.ArrayList;

/**
 * Created by SMILECS on 9/14/16.
 */
public class DbUtility {
    String  selection = DataContract.Data.TYPE + "=?";
    String [] projection = null;
    Dbhelper helper;
    String TAG = "DbUtility";
    SQLiteDatabase db;
    SQLiteDatabase Rdb;
    public DbUtility(Context c){
        helper = new Dbhelper(c);
        db = helper.getWritableDatabase();
        Rdb = helper.getReadableDatabase();
    }
    public void addFeed(feedModel model){
        ContentValues values = new ContentValues();
        values.put(DataContract.Data.AVAILABLE, model.getAvailable());
        values.put(DataContract.Data.TAG, model.getTag());
        values.put(DataContract.Data.URL, model.getURL());
        values.put(DataContract.Data.WEBURL, model.getWebUrl());
        values.put(DataContract.Data.TYPE, model.getViewType());
        values.put(DataContract.Data.TYPE2, "2");
        //byte[] im = model.getImage().getBytes();
        db.insert(DataContract.Data.TABLE_NAME, null, values);

    }

    public ArrayList<feedModel> readData(){
        Log.d("Dbutility", "here");
        String [] selectionAgs = {};
        ArrayList<feedModel> list = new ArrayList<>();
        Cursor cursor = Rdb.query(DataContract.Data.TABLE_NAME, projection, null, selectionAgs, null, null,  null);
        if(cursor != null  && cursor.moveToFirst()) {
            do {
                feedModel model = new feedModel();
                try {
                    model.setAvailable(cursor.getString(cursor.getColumnIndexOrThrow(DataContract.Data.AVAILABLE)));
                    model.setTag(cursor.getString(cursor.getColumnIndexOrThrow(DataContract.Data.TAG)));
                    model.setViewType(cursor.getString(cursor.getColumnIndexOrThrow(DataContract.Data.TYPE)));
                    model.setURL(cursor.getString(cursor.getColumnIndexOrThrow(DataContract.Data.URL)));
                    model.setWebUrl(cursor.getString(cursor.getColumnIndexOrThrow(DataContract.Data.WEBURL)));
                    list.add(model);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public int  Delete(){
        if(!readData().isEmpty()){
            db.delete(DataContract.Data.TABLE_NAME, DataContract.Data.TYPE2 + "= ?", new String[] {"2"});
            return 1;
        }
        return 0;

    }
}
