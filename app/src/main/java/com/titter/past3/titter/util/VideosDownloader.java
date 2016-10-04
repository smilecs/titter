package com.titter.past3.titter.util;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.titter.past3.titter.model.feedModel;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by SMILECS on 8/12/16.
 */
public class VideosDownloader {
    private static String TAG = "VideosDownloader";
    Context context;
    FileCache fileCache;
    IVideoDownloadListener iVideoDownloadListener;
    DownloadManager downloadManager;
    DbUtility db;

    public VideosDownloader(Context context){
        this.context = context;
        fileCache = new FileCache(context);
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        db = new DbUtility(context);

    }
    public void startVideosDownloading(final ArrayList<feedModel> videosList){
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                Log.d(TAG, "started");
                for(int i=0; i<videosList.size(); i++){
                    Log.d(TAG, "started33");
                    final feedModel video = videosList.get(i);
                    String url = video.getURL();
                    video.setAvailable("true");
                    //downloadfromUrl(url, fileCache.directory(), url, downloadManager);
                    String downloadPath = downloadVideo(url, video);
                   /* Activity activity = (Activity) context;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utils.savePreferences(context, video.getURL());
                            iVideoDownloadListener.onVideoDownloaded(video);
                            }
                        });*/
                    //db.addFeed(video);

                }
                Activity activity = (Activity) context;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Utils.savePreferences(context, video.getURL());
                        iVideoDownloadListener.onVideoDownloaded();
                    }
                });

            }
        });
        thread.start();
    }

    private String downloadVideo(String urlStr, feedModel video)
    {
        URL url = null;
        File file = null;
        try
        {   Log.d(TAG, "started22");
            file = fileCache.getFile(urlStr);
            url = new URL(urlStr);
            long startTime = System.currentTimeMillis();
            URLConnection ucon = null;
            ucon = url.openConnection();
            InputStream is = ucon.getInputStream();
            Log.d(TAG, "started22");
            BufferedInputStream inStream = new BufferedInputStream(is, 1024 * 5);
            FileOutputStream outStream = new FileOutputStream(file);
            byte[] buff = new byte[5 * 1024];

            //Read bytes (and store them) until there is nothing more to read(-1)
            int len;
            while ((len = inStream.read(buff)) != -1) {
                outStream.write(buff, 0, len);
            }

            //clean up
            outStream.flush();
            outStream.close();
            inStream.close();

        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, file.getAbsolutePath());
        db.addFeed(video);
        return file.getAbsolutePath();
    }


    public static void downloadfromUrl(String url, String dir, String fileName, DownloadManager downloadManager){
        Uri downloadUri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        request.setDestinationInExternalPublicDir(dir,fileName);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        downloadManager.enqueue(request);
    }

    public void setOnVideoDownloadListener(IVideoDownloadListener iVideoDownloadListener) {
        this.iVideoDownloadListener = iVideoDownloadListener;
    }
}
