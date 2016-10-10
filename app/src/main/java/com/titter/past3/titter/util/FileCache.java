package com.titter.past3.titter.util;

import android.content.Context;

import java.io.File;

/**
 * Created by SMILECS on 8/10/16.
 */
public class FileCache {

    private File cacheDir;

    public FileCache(Context context) {
        // Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            cacheDir = new File(
                    android.os.Environment.getExternalStorageDirectory(),
                    "LazyList");
        }
        else {
            cacheDir = new File(context.getCacheDir(), "LazyList");
        }
        if (!cacheDir.exists()){
            cacheDir.mkdirs();
        }
    }

    public File getFile(String url, String type) {
        // I identify images by hashcode. Not a perfect solution, good for the
        // demo.
        String filename = String.valueOf(url.hashCode()) + "." +type;
        // Another possible solution (thanks to grantland)
        // String filename = URLEncoder.encode(url);
        File f = new File(cacheDir, filename);
        return f;

    }

    public String directory(){
        return cacheDir.getAbsolutePath();
    }

    public void clear() {
        File[] files = cacheDir.listFiles();
        if (files == null)
            return;
        for (File f : files)
            f.delete();
    }

}
