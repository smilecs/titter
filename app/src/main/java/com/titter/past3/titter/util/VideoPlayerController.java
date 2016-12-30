package com.titter.past3.titter.util;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.titter.past3.titter.model.feedModel;

import java.net.MalformedURLException;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import io.realm.Realm;

/**
 * Created by SMILECS on 8/10/16.
 */
public class VideoPlayerController {
    private static String TAG = "VideoPlayerController";
    Context context;
    FileCache fileCache;
    int currentPositionOfItemToPlay = 0;
    feedModel currentPlayingVideo;
    VideosDownloader downloader;
    RelativeLayout img, vids;
    int position;
    Realm realm;
    private Map<String, VideoPlayer> videos = Collections.synchronizedMap(new WeakHashMap<String, VideoPlayer>());
    private Map<String, ProgressBar> videosSpinner = Collections.synchronizedMap(new WeakHashMap<String, ProgressBar>());

    public VideoPlayerController(Context context, Realm realm) {
        this.context = context;
        downloader = new VideosDownloader(context, realm);
        fileCache = new FileCache(context);
        this.realm = realm;
    }
    public void loadVideo(feedModel video, VideoPlayer videoPlayer, ProgressBar progressBar, int position, RelativeLayout img, RelativeLayout vid){
        videos.put(video.getIndex(), videoPlayer);
        this.img = img;
        this.vids = vid;
        videosSpinner.put(video.getIndex(), progressBar);
        this.position = position;
        //handlePlayBack(video);
    }

    public void handlePlayBack(feedModel video){
        playVideo(video);

    }
 /*   public void handlePlayBack(feedModel video){
            if(isVideoVisible(video)){
                Log.d(TAG, "visible");
                playVideo(video);
            }
    }*/

    private void playVideo(final feedModel video){
            Log.d(TAG, "currentplayin no");
        Log.d(TAG, video.getURL());
            if(videos.containsKey(video.getIndex())){
                Log.d(TAG, "contains yes");
                final VideoPlayer videoPlayer2 = videos.get(video.getIndex());
                //String localPath = fileCache.getFile(video.getURL()).getAbsolutePath();
                String localPath = video.getURL();
                    Log.d(TAG, localPath);

                    videoPlayer2.loadVideo(localPath, video, img);
                    videoPlayer2.setOnVideoPreparedListner(new IVideoPreparedListener() {
                        @Override
                        public void onVideoPrepared(feedModel vid) {
                            Log.d(TAG, "contains yes2");
                            Log.d(TAG, vid.getIndex());
                            if(vid.getIndex().equals(video.getIndex())){
                                Log.d(TAG, "equals");
                                if(currentPlayingVideo != null && currentPlayingVideo != video){
                                    Log.d(TAG, "contains yes3");
                                    VideoPlayer videoPlayer1 = videos.get(currentPlayingVideo.getIndex());
                                    videoPlayer1.pausePlay();
                                }
                                if(!video.getAvailable().equals("true")){
                                    Log.d(TAG, "Downloading");
                                    try{
                                        downloader.downloadVideo(video, position);
                                    }
                                    catch (MalformedURLException u){
                                        u.printStackTrace();
                                    }

                                }
                                currentPlayingVideo = video;
                                img.setVisibility(View.GONE);
                                vids.setVisibility(View.VISIBLE);
                                videoPlayer2.startPlay();
                            }
                        }
                    });

            }


    }

    public void StopPlayback(feedModel video){
        if(video.getViewType().equals("video")){
            img.setVisibility(View.VISIBLE);
            videos.get(video.getIndex()).stopPlay();
        }
    }


    private boolean isVideoDownloaded(feedModel video) {
        String isVideoDownloaded = video.getAvailable();

//        Log.d(TAG, isVideoDownloaded);
        boolean isVideoAvailable = Boolean.valueOf(isVideoDownloaded);
        if(isVideoAvailable)
        {
            Log.d(TAG, "available");
            hideProgressSpinner(video);
            return true;
        }
        Log.d(TAG, "notdownloaded");
        showProgressSpinner(video);
        return false;
    }

    private void showProgressSpinner(feedModel video) {
        ProgressBar progressBar = videosSpinner.get(video.getIndex());
        if(progressBar!=null)
            progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressSpinner(feedModel video) {

        ProgressBar progressBar = videosSpinner.get(video.getIndex());
        if(progressBar!=null && progressBar.isShown())
        {
            progressBar.setVisibility(View.GONE);
            Log.i(TAG, "ProgressSpinner Hided Index: " + video.getIndex());
        }
    }

    public void setcurrentPositionOfItemToPlay(int mCurrentPositionOfItemToPlay) {
        currentPositionOfItemToPlay = mCurrentPositionOfItemToPlay;
    }

    private boolean isVideoVisible(feedModel video) {

        //To check if the video is visible in the listview or it is currently at a playable position
        //we need the position of this video in listview and current scroll position of the listview
        int positionOfVideo = Integer.valueOf(video.getIndex());
        Log.d(TAG, String.valueOf(currentPositionOfItemToPlay));
        if(currentPositionOfItemToPlay == positionOfVideo) {
            Log.d(TAG, "postion");
            return true;
        }
        return false;
    }

}
