package com.titter.past3.titter.util;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.titter.past3.titter.model.feedModel;

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
    public void loadVideo(feedModel video, VideoPlayer videoPlayer, ProgressBar progressBar, int position){
        videos.put(video.getIndex(), videoPlayer);
        videosSpinner.put(video.getIndex(), progressBar);
        this.position = position;
        handlePlayBack(video);
    }

    public void handlePlayBack(feedModel video){
        if(isVideoDownloaded(video)){
            Log.d(TAG, "downloaded");
            if(isVideoVisible(video)){
                Log.d(TAG, "visible");
                playVideo(video);
            }
        }
    }

 /*   public void handlePlayBack(feedModel video){
            if(isVideoVisible(video)){
                Log.d(TAG, "visible");
                playVideo(video);
            }
    }*/

    private void playVideo(final feedModel video){
        if(currentPlayingVideo != video){
            Log.d(TAG, "currentplayin no");
            if(videos.containsKey(video.getIndex())){
                Log.d(TAG, "contains yes");
                final VideoPlayer videoPlayer2 = videos.get(video.getIndex());
                //String localPath = fileCache.getFile(video.getURL()).getAbsolutePath();
                String localPath = video.getURL();
                if(!videoPlayer2.isLoaded){
                    Log.d(TAG, localPath);
                    videoPlayer2.loadVideo(localPath, video);
                    videoPlayer2.setOnVideoPreparedListner(new IVideoPreparedListener() {
                        @Override
                        public void onVideoPrepared(feedModel vid) {
                            Log.d(TAG, "contains yes2");
                            if(vid.getIndex().equals(video.getIndex())){
                                Log.d(TAG, "contains yes2");
                                if(currentPlayingVideo != null && currentPlayingVideo != video){
                                    Log.d(TAG, "contains yes3");
                                    VideoPlayer videoPlayer1 = videos.get(currentPlayingVideo.getIndex());
                                    videoPlayer1.pausePlay();
                                }
                                downloader.downloadVideo(video, position);
                                videoPlayer2.mp.start();
                                currentPlayingVideo = video;
                            }
                        }
                    });
                }
                else {
                    if(currentPlayingVideo != null){
                        Log.d(TAG, "contains yes2");
                        VideoPlayer videoPlayer1 = videos.get(currentPlayingVideo.getIndex());
                        videoPlayer1.pausePlay();
                    }
                    boolean isStarted = videoPlayer2.startPlay();
                    downloader.downloadVideo(video, position);
                    currentPlayingVideo = video;
                }
            }
        }
        else{
            //String localPath = fileCache.getFile(video.getURL()).getAbsolutePath();
            String localPath = video.getURL();
            final VideoPlayer videoPlayer2 = videos.get(video.getIndex());
            // videoPlayer2.mp.reset();
            videoPlayer2.loadVideo(localPath, video);
            videoPlayer2.setOnVideoPreparedListner(new IVideoPreparedListener() {
                @Override
                public void onVideoPrepared(feedModel vid) {
                    Log.d(TAG, "contains yes2");
                    if(vid.getIndex().equals(video.getIndex())){
                        Log.d(TAG, "contains yes2");
                        if(currentPlayingVideo != null && currentPlayingVideo!= video){
                            Log.d(TAG, "contains yes3");
                            VideoPlayer videoPlayer1 = videos.get(currentPlayingVideo.getIndex());
                            videoPlayer1.pausePlay();
                        }
                        downloader.downloadVideo(video, position);
                        videoPlayer2.mp.start();
                        currentPlayingVideo = video;
                    }
                }
            });
            Log.d(TAG, "already playing video" + video.getURL());
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
