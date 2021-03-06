package com.titter.past3.titter.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.titter.past3.titter.R;
import com.titter.past3.titter.model.feedModel;
import com.titter.past3.titter.util.VideoPlayer;
import com.titter.past3.titter.util.VideoPlayerController;
import com.titter.past3.titter.util.volleySingleton;

import java.util.ArrayList;

/**
 * Created by SMILECS on 4/21/16.
 */
public class FeedsAdapter extends RecyclerView.Adapter<FeedsAdapter.ViewHolder>{
    Context context;
    static final int IMAGE = 1;
    static final int VIDEO = 2;
    ArrayList<feedModel> model;
    public VideoPlayerController videoPlayerController;

    public FeedsAdapter(Context context, ArrayList<feedModel> model)
    {
        this.model = model;
        this.context = context;
        videoPlayerController = new VideoPlayerController(context);


    }
   /* public VideoPlayerController vidObject(){
        return videoPlayerController;
    }*/

    public static class ViewHolder extends RecyclerView.ViewHolder{
       public NetworkImageView img;
        public VideoPlayer vid;
        public TextView txt;
        public RelativeLayout layout;
        public ProgressBar progressBar;
        public ViewHolder(View v, int Type){
            super(v);
            Log.d("test1", String.valueOf(Type));
            switch (Type){
                case VIDEO:
                  layout = (RelativeLayout) v.findViewById(R.id.layout);
                    progressBar = (ProgressBar) v.findViewById(R.id.progressBar4);
                  vid = (VideoPlayer) v.findViewById(R.id.video);

                    vid.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //vid.start();
                            Log.d("Adapter", "clicked");
                            vid.changePlayState();
                        }
                    });
                    break;
                default:
                    img = (NetworkImageView) v.findViewById(R.id.imageView);
                    progressBar = (ProgressBar) v.findViewById(R.id.loader);
                    break;
            }
            txt = (TextView) v.findViewById(R.id.title);



        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FeedsAdapter.ViewHolder vh;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType){
            case VIDEO:
                View v = inflater.inflate(R.layout.video, parent, false);
                vh = new ViewHolder(v, viewType);
                break;
            default:
                View v1 = inflater.inflate(R.layout.image, parent, false);
                vh = new ViewHolder(v1, viewType);
                break;
             }
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        int Type = IMAGE;
        feedModel mod = model.get(position);
        Typeface robot = Typeface.createFromAsset(context.getAssets(),
                "fonts/Roboto-Medium.ttf"); //use this.getAssets if you are calling from an Activity
        try{
            if(mod.getViewType().equals("Video")){
                Type = VIDEO;
            }
        }
       catch (Exception ne){
            ne.printStackTrace();
        }
        Log.d("Adapter", mod.getURL() + "  " + mod.getViewType());

        switch (Type){
            case VIDEO:
                //Uri videoUri = Uri.parse(mod.getURL());
                videoPlayerController.loadVideo(mod, holder.vid,holder.progressBar);
              //  holder.vid.setVideoURI(videoUri);
                //holder.vid.seekTo(holder.vid.getCurrentPosition() + 1000);
                holder.txt.setTypeface(robot);
                holder.txt.setText(mod.getTag());
                break;
            case IMAGE:
                ImageLoader imageLoader = volleySingleton.getsInstance().getImageLoader();
                //ImageRequest ir = new ImageRequest()
                imageLoader.get(mod.getURL(), new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                        holder.progressBar.setVisibility(View.GONE);
                        holder.img.setImageBitmap(imageContainer.getBitmap());
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });
                try{
                    Log.d("nulltest", mod.getViewType());
                    holder.img.setImageUrl(mod.getURL(), imageLoader);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

                holder.txt.setTypeface(robot);
                holder.txt.setText(mod.getTag());
                break;
        }


    }


    @Override
    public int getItemCount() {

        return model.size();
    }


    @Override
    public int getItemViewType(int position) {
        feedModel mod = model.get(position);
        try{
            if(mod.getViewType().equals("Video")){
                return 2;
            }
            return 1;
        }catch (NullPointerException ex){
            ex.printStackTrace();
            return 1;
        }catch (NumberFormatException nfe){
            nfe.printStackTrace();
            return 1;
        }
        //Log.d("type", model.get(position))

    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
    }
}
