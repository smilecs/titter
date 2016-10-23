package com.titter.past3.titter;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.titter.past3.titter.adapter.FeedsAdapter;
import com.titter.past3.titter.model.feedModel;
import com.titter.past3.titter.util.DbUtility;
import com.titter.past3.titter.util.FileCache;
import com.titter.past3.titter.util.IVideoDownloadListener;
import com.titter.past3.titter.util.TitterService;
import com.titter.past3.titter.util.Utils;
import com.titter.past3.titter.util.VideosDownloader;
import com.titter.past3.titter.util.volleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity implements IVideoDownloadListener, NavigationView.OnNavigationItemSelectedListener {
    private RecyclerView mRecyclerView;
    private FeedsAdapter mAdapter;
    private RecyclerView.LayoutManager mlayoutManager;
    ArrayList<feedModel> model;
    String TAG = "TAG";
    ArrayList<feedModel> videos;
    VideosDownloader videosDownloader;
    volleySingleton volley;
    RequestQueue requestQueue;
    Realm realm;
    ProgressBar progressBar;
    DbUtility dbUtility;
    FileCache file;
    DrawerLayout drawer;
    Context context;
    Button refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        file = new FileCache(this);
        dbUtility = new DbUtility(this);
        volley = volleySingleton.getsInstance();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.cardList);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        refresh = (Button) findViewById(R.id.button);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Refresh();
            }
        });
        mlayoutManager = new LinearLayoutManager(this);
        mAdapter = new FeedsAdapter(context, this, realm.where(feedModel.class).findAllAsync());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mlayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Log.d(TAG, "idle");
                    LinearLayoutManager layoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                    int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                    int findFirstCompletelyVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                    feedModel feed;

                    if (mAdapter.getData() != null && mAdapter.getData().size() > 0) {
                        if (findFirstCompletelyVisibleItemPosition >= 0) {
                            feed = mAdapter.getData().get(findFirstCompletelyVisibleItemPosition);
                            Log.d(TAG, "top");
                            Log.d(TAG, feed.getTag());
                            if (feed.getViewType().equals("video")) {
                                mAdapter.videoPlayerController.setcurrentPositionOfItemToPlay(findFirstCompletelyVisibleItemPosition);
                                mAdapter.videoPlayerController.handlePlayBack(feed);
                            }

                        } else {
                            feed = mAdapter.getData().get(firstVisiblePosition);
                            Log.d(TAG, "Bottom");
                            Log.d(TAG, feed.getTag());
                            if (feed.getViewType().equals("video")) {
                                mAdapter.videoPlayerController.setcurrentPositionOfItemToPlay(findFirstCompletelyVisibleItemPosition);
                                mAdapter.videoPlayerController.handlePlayBack(feed);
                            }
                        }
                    }

                }
            }
        });
        model = new ArrayList<>();
        videos = new ArrayList<>();

        videosDownloader = new VideosDownloader(context, realm);
        videosDownloader.setOnVideoDownloadListener(this);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView nav = (NavigationView) findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(this);
        Intent i = new Intent(this, TitterService.class);
        startService(i);
        requestQueue = volley.getmRequestQueue();
        // Refresh();
        Log.d("tttt", "tttt");
        NetworkStatus();
        //Reload();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }


        if (id == R.id.other) {
            Intent i = new Intent(this, others.class);
            startActivity(i);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onVideoDownloaded(int position) {
        Log.d("MainActivity", "downloaded");
        mAdapter.notifyItemChanged(position);
        //mAdapter.videoPlayerController.handlePlayBack(video);
       // new LoadData().execute();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.other) {
            Intent i = new Intent(this, others.class);
            startActivity(i);
        }

        if (id == R.id.about) {
            Intent i = new Intent(this, About.class);
            startActivity(i);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void Refresh() {
        refresh.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, Utils.URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("MainActivity", "download");
                JSONArray array;
                //realm = Realm.getDefaultInstance();
               // ArrayList<feedModel> tmp = new ArrayList<>();
                try {
                    progressBar.setVisibility(View.GONE);
                    array = jsonObject.getJSONArray("Data");
                    model.clear();
                    Log.d("MainActivity", array.toString());
                    for (int i = 0; i < array.length(); i++) {
                        final feedModel mode = new feedModel();
                        try {

                            mode.setId(array.getJSONObject(i).getString("File"));
                            mode.setTag(array.getJSONObject(i).getString("Description"));
                            mode.setViewType(array.getJSONObject(i).getString("Type"));
                            mode.setURL(array.getJSONObject(i).getString("File"));
                            mode.setIndex(String.valueOf(i));
                        } catch (Throwable je) {
                            je.printStackTrace();
                        }
                        model.add(mode);
                    }
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.copyToRealm(model);
                        }
                    });
                   // Reload();
                    progressBar.setVisibility(View.GONE);

                    /*if (array.length() > 0) {
                        videosDownloader.startVideosDownloading(tmp);
                    }*/

                } catch (Exception je) {
                    je.printStackTrace();
                }
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                refresh.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

        requestQueue.add(req);
    }


    public void Reload(){
        if(realm.where(feedModel.class).findAllAsync().size() < 1){
           // Refresh();
        }else {
            Log.d("Mainactivity", "reload");
            mAdapter = new FeedsAdapter(context, this, realm.where(feedModel.class).findAllAsync());
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);

        }
    }

    public void NetworkStatus() {
        Log.d("MainActivity", "Internet started");
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            //boolean isMOBILE = activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
            // message = "Titter Service connected";
            Refresh();
        }else{
            Reload();
        }
    }
}