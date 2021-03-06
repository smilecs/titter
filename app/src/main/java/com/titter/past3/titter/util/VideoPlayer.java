package com.titter.past3.titter.util;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import com.titter.past3.titter.model.feedModel;

/**
 * Created by SMILECS on 8/10/16.
 */
public class VideoPlayer extends TextureView implements TextureView.SurfaceTextureListener {
    String TAG, url;
    feedModel video;
    MediaPlayer mp;
    Surface surface;
    SurfaceTexture s;
    boolean isFirstListItem;
    boolean isLoaded;
    boolean isMpPrepared;
    IVideoPreparedListener iVideoPreparedListener;
    public VideoPlayer(Context context){
        super(context);
    }

    public VideoPlayer(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public void loadVideo(String localPath, feedModel video){
        this.url = localPath;
        this.video = video;
        isLoaded = true;
        if (this.isAvailable()){
            prepareVideo(getSurfaceTexture());
        }
        setSurfaceTextureListener(this);
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "SurfaceTextureAvailable");
        isMpPrepared = false;
        prepareVideo(surface);

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if(mp!=null)
        {
            mp.stop();
            mp.reset();
            mp.release();
            mp = null;
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    public void prepareVideo(SurfaceTexture t){
        this.surface = new Surface(t);
        mp = new MediaPlayer();
        mp.setSurface(this.surface);
        try{
            mp.setDataSource(url);
            mp.prepareAsync();
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    isMpPrepared = true;
                    mp.setLooping(true);
                    iVideoPreparedListener.onVideoPrepared(video);
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
    }

    public boolean startPlay(){
        if(mp!=null){
            if(!mp.isPlaying()){
                mp.start();
                return true;
            }
        }
        return false;
    }

    public void pausePlay()
    {
        if(mp!=null)
            mp.pause();
    }

    public void stopPlay()
    {
        if(mp!=null)
            mp.stop();
    }

    public void changePlayState()
    {
        if(mp!=null)
        {
            if(mp.isPlaying())
                mp.pause();
            else
                mp.start();
        }

    }
    public void setOnVideoPreparedListner(IVideoPreparedListener iVideoPreparedListener){
        this.iVideoPreparedListener = iVideoPreparedListener;
    }
}
