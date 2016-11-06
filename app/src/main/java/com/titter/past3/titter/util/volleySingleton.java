package com.titter.past3.titter.util;

/**
 * Created by smile on 1/12/16.
 */

import android.graphics.Bitmap;
import android.util.LruCache;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by smilecs on 6/3/15.
 */
public class volleySingleton {
 private static volleySingleton sInstance = null;
    private RequestQueue mRequestQueue;
    private ImageLoader imageLoader;
    private volleySingleton(){
        mRequestQueue = Volley.newRequestQueue(Application.getAppContext());
        imageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(20);


            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });

    }



    public static volleySingleton getsInstance(){
        if(sInstance == null){
            sInstance = new volleySingleton();
        }
        return sInstance;
    }
    public RequestQueue getmRequestQueue(){
        return mRequestQueue;
    }
    public ImageLoader getImageLoader() {
        return imageLoader;
    }

}
