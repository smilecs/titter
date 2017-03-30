package com.titter.past3.titter.util;

import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;

/**
 * Created by SMILECS on 4/19/16.
 */
public class Application extends android.app.Application{
    private static Application sInstance;
    private HttpProxyCacheServer proxy;

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;
    }

    public static HttpProxyCacheServer getProxy(Context context) {
        Application app = (Application) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer(sInstance);
    }
    public static Application getsInstance(){
        return sInstance;
    }
    public static Context getAppContext(){
        return sInstance.getApplicationContext();
    }
}
