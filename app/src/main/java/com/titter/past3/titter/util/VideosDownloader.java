package com.titter.past3.titter.util;

import android.app.DownloadManager;
import android.content.Context;
import android.util.Log;

import com.titter.past3.titter.model.feedModel;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import io.realm.Realm;

/**
 * Created by SMILECS on 8/12/16.
 */
public class VideosDownloader {
    private static String TAG = "VideosDownloader";
    Realm realm;
    Context context;
    FileCache fileCache;
    IVideoDownloadListener iVideoDownloadListener;
    DownloadManager downloadManager;


    public VideosDownloader(Context context, Realm realm){
        this.context = context;
        fileCache = new FileCache(context);
        iVideoDownloadListener = (IVideoDownloadListener) context;
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        this.realm = realm;

    }

    public String downloadVideo(final feedModel video, final int position) throws MalformedURLException
    {


        String type = "jpg";
        if(video.getViewType().equals("video")){
            type = "mp4";
        }
        final File file = fileCache.getFile(video.getURL(), type);

                final URL url = new URL(video.getURL());
                Thread tm = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try
                        {
                            Log.d(TAG, "started22");


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
                            outStream.flush();
                            outStream.close();
                            inStream.close();
                            iVideoDownloadListener.onVideoDownloaded(position, file.getAbsolutePath());


                    }
                        catch (Exception e) {
                            e.printStackTrace();
                            //video.setAvailable("false");
                        }


                    }
                });
                tm.start();

                //clean up

              //  video.setAvailable("true");



            Log.d(TAG, file.getAbsolutePath());
            /*realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    video.setURL(file.getAbsolutePath());
                    realm.copyToRealmOrUpdate(video);
                }
            });*/
            // iVideoDownloadListener.onVideoDownloaded(position);
            //db.addFeed(video);
        return "ok";
    }


    /*public static void downloadfromUrl(String url, String dir, String fileName, DownloadManager downloadManager){
        Uri downloadUri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        request.setDestinationInExternalPublicDir(dir,fileName);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        downloadManager.enqueue(request);
    }*/

    public void setOnVideoDownloadListener(IVideoDownloadListener iVideoDownloadListener) {
        this.iVideoDownloadListener = iVideoDownloadListener;
    }
}
