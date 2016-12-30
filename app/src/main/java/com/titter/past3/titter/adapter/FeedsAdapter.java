package com.titter.past3.titter.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.titter.past3.titter.MainActivity;
import com.titter.past3.titter.R;
import com.titter.past3.titter.model.feedModel;
import com.titter.past3.titter.util.FileCache;
import com.titter.past3.titter.util.VideoPlayer;
import com.titter.past3.titter.util.VideoPlayerController;
import com.titter.past3.titter.util.volleySingleton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by SMILECS on 4/21/16.
 */
public class FeedsAdapter extends RealmRecyclerViewAdapter<feedModel, FeedsAdapter.ViewHolder> {
    Context context;
    static final int IMAGE = 1;
    static final int VIDEO = 2;
    ArrayList<feedModel> model;
    FileCache fileCache;
    Realm realm;

    public VideoPlayerController videoPlayerController;

    public FeedsAdapter(Context context, MainActivity activity, OrderedRealmCollection<feedModel> model)
    {   super(activity, model, true);
        //this.model = model;
        this.realm = Realm.getDefaultInstance();
        fileCache = new FileCache(context);
        this.context = context;
        videoPlayerController = new VideoPlayerController(context, realm);


    }
   /* public VideoPlayerController vidObject(){
        return videoPlayerController;
    }*/

    public static class ViewHolder extends RecyclerView.ViewHolder{
       public ImageView img;
        public VideoPlayer vid;
        public TextView txt;
        public RelativeLayout layout, videoRel, ImageRel;
        public ProgressBar progressBar;
        public CardView vidCard;
        public ViewHolder(View v, int Type){
            super(v);
            Log.d("test1", String.valueOf(Type));
            switch (Type){
                case VIDEO:
                  layout = (RelativeLayout) v.findViewById(R.id.layout);
                    vidCard = (CardView) v.findViewById(R.id.vidCard);
                    progressBar = (ProgressBar) v.findViewById(R.id.progressBar4);
                  vid = (VideoPlayer) v.findViewById(R.id.video);
                    ImageRel = (RelativeLayout) v.findViewById(R.id.imageRel);
                    videoRel = (RelativeLayout) v.findViewById(R.id.videoView);
                    img = (ImageView) v.findViewById(R.id.imageView);
                    vidCard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("Adapter", "clicked");
                            vid.changePlayState();
                        }
                    });

                    break;
                default:
                    img = (ImageView) v.findViewById(R.id.imageView);
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
        final feedModel mod = getData().get(position);

        Typeface robot = Typeface.createFromAsset(context.getAssets(),
                "fonts/Roboto-Medium.ttf"); //use this.getAssets if you are calling from an Activity
        try{
            if(mod.getViewType().equals("video")){
                Type = VIDEO;
            }
        }
       catch (Exception ne){
            ne.printStackTrace();
        }

        switch (Type){
            case VIDEO:
                //Uri videoUri = Uri.parse(mod.getURL());
                videoPlayerController.loadVideo(mod, holder.vid, holder.progressBar, position, holder.ImageRel, holder.videoRel);
                //holder.vid.setVideoURI(videoUri);
                //holder.vid.seekTo(holder.vid.getCurrentPosition() + 1000);
                holder.txt.setTypeface(robot);
                holder.txt.setText(mod.getTag());
                break;
            default:
                ImageLoader imageLoader = volleySingleton.getsInstance().getImageLoader();
                //holder.img.setImageUrl(mod.getURL(), imageLoader);
                if(!mod.getAvailable().equals("true")){
                    imageLoader.get(mod.getURL(), new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                            try{
                                final File file = fileCache.getFile(mod.getURL(), "jpg");
                               // Log.d("BitmapCount", String.valueOf(bm.getByteCount()));
                                FileOutputStream outStream = new FileOutputStream(file);
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                imageContainer.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byte[] byteArray = stream.toByteArray();
                                outStream.write(byteArray, 0, byteArray.length);
                                Log.d("FeedsAdapter", "UnAvailable");
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        mod.setURL(file.getAbsolutePath());
                                        mod.setAvailable("true");
                                        realm.copyToRealmOrUpdate(mod);
                                    }
                                });
                                //holder.img.setAdjustViewBounds(true);
                                holder.img.getLayoutParams().height = imageContainer.getBitmap().getHeight();
                                if(imageContainer.getBitmap().getHeight() < 80){
                                    holder.img.getLayoutParams().height = 320;
                                }
                                holder.img.setImageDrawable(Drawable.createFromPath(mod.getURL()));
                            }catch (Exception e){
                                e.printStackTrace();
                                holder.img.setImageBitmap(imageContainer.getBitmap());
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            volleyError.printStackTrace();
                        }
                    });
                }else {
                    holder.img.setImageDrawable(Drawable.createFromPath(mod.getURL()));
                    Log.d("FeedsAdapter", "ImageAvailable");
                }

                //Log.d("nulltest", mod.getURL());
                holder.txt.setTypeface(robot);
                holder.txt.setText(mod.getTag());
                break;
        }


    }


    @Override
    public int getItemCount() {
        try{
            if(getData().isEmpty()){
                return 0;
            }
        }catch (NullPointerException npe){
            npe.getMessage();
            return 0;
        }
        return getData().size();
    }


    @Override
    public int getItemViewType(int position) {
        feedModel mod = getData().get(position);
        try{
            if(mod.getViewType().equals("video")){
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
