package com.titter.past3.titter.util;

import android.content.Context;



/**
 * Created by SMILECS on 4/19/16.
 */
public class Application extends android.app.Application{
    private static Application sInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;
    }

    public static Application getsInstance(){
        return sInstance;
    }
    public static Context getAppContext(){
        return sInstance.getApplicationContext();
    }


}
