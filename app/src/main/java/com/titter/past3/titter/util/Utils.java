package com.titter.past3.titter.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.titter.past3.titter.R;

/**
 * Created by SMILECS on 8/12/16.
 */
public class Utils {

    //public static String URL = "http://192.168.43.126:8080/get";
    public static String URL = "http://titter.herokuapp.com/get";
    //public static String URL = "http://titter.past3.com.ng/api/posts";

    public static String nreadPreferences(Context context, String name){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.save), context.MODE_PRIVATE);
        return sharedPreferences.getString(name, "false");
    }

    public static void savePreferences(Context context, String name){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.save), context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(name, "true");
        editor.commit();
    }
}
