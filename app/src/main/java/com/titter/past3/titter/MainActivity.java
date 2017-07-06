package com.titter.past3.titter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.titter.past3.titter.util.EndlessRecyclerViewScrollListener;
import com.titter.past3.titter.util.IVideoDownloadListener;
import com.titter.past3.titter.util.TitterService;
import com.titter.past3.titter.util.Utils;
import com.titter.past3.titter.util.VideosDownloader;
import com.titter.past3.titter.util.volleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements IVideoDownloadListener, NavigationView.OnNavigationItemSelectedListener{
    private RecyclerView mRecyclerView;
    private FeedsAdapter mAdapter;
    private LinearLayoutManager mlayoutManager;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<feedModel> model;
    String TAG = "TAG";
    ArrayList<feedModel> videos;
    VideosDownloader videosDownloader;
    volleySingleton volley;
    RequestQueue requestQueue;
    ProgressBar progressBar;
    DrawerLayout drawer;
    Context context;
    Button refresh;
    Boolean next = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
        volley = volleySingleton.getsInstance();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.cardList);
        progressBar= (ProgressBar) findViewById(R.id.progressBar);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
        //swipeRefreshLayout.setColorSchemeColors();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                Refresh("1");
            }
        });
        refresh = (Button) findViewById(R.id.button);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Refresh("1");
            }
        });
        mlayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mlayoutManager);
        model = new ArrayList<>();
        videos = new ArrayList<>();
        mAdapter = new FeedsAdapter(this, model);
        videosDownloader = new VideosDownloader(context);
        videosDownloader.setOnVideoDownloadListener(this);

//            mAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mlayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d(TAG, String.valueOf(page));
                if(next){
                    Refresh(String.valueOf(page));
                }

            }
        });
        drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView nav = (NavigationView) findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(this);
        Intent i = new Intent(this, TitterService.class);
        startService(i);
        requestQueue = volley.getmRequestQueue();
        Refresh("1");
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
    public void onVideoDownloaded(feedModel video) {
        Log.d("MainActivity", "downloaded");
    mAdapter.videoPlayerController.handlePlayBack(video);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.other) {
            Intent i = new Intent(this, faq.class);
            startActivity(i);
        }

        if(id == R.id.about) {
            Intent i = new Intent(this, About.class);
            startActivity(i);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void Refresh(String page){
        refresh.setVisibility(View.GONE);
        //progressBar.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(true);
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, Utils.URL+"?p="+page, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                swipeRefreshLayout.setRefreshing(false);
                JSONArray array;
                try {
                    Log.d("MainActivity", jsonObject.getString("Data"));
                    array = jsonObject.getJSONArray("Data");
                    try {
                        next = jsonObject.getJSONObject("Page").getBoolean("Next");
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    for (int i = 0; i < array.length(); i++) {
                        feedModel mode = new feedModel();
                        try {
                            // Log.d("MainActivity", json.toString());
                            mode.setTag(array.getJSONObject(i).getString("Description"));
                            mode.setViewType(array.getJSONObject(i).getString("Type"));
                            mode.setURL(array.getJSONObject(i).getString("File"));
                            mode.setIndex(String.valueOf(i));
                            if (mode.getViewType().equals("Video")) {
                                videos.add(mode);
                            }

                        } catch (Throwable je) {
                            je.printStackTrace();


                        }
                        model.add(mode);

                    }
                    progressBar.setVisibility(View.GONE);
                    mAdapter.notifyDataSetChanged();
                    //videosDownloader.startVideosDownloading(videos);
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
                                if (model != null && model.size() > 0) {
                                    if (findFirstCompletelyVisibleItemPosition >= 0) {
                                        feed = model.get(findFirstCompletelyVisibleItemPosition);
                                        Log.d(TAG, feed.getTag());
                                        if (feed.getViewType().equals("Video")) {
                                            mAdapter.videoPlayerController.setcurrentPositionOfItemToPlay(findFirstCompletelyVisibleItemPosition);
                                            mAdapter.videoPlayerController.handlePlayBack(feed);
                                        }

                                    } else {
                                        feed = model.get(firstVisiblePosition);
                                        Log.d(TAG, feed.getTag());
                                        if (feed.getViewType().equals("Video")) {
                                            mAdapter.videoPlayerController.setcurrentPositionOfItemToPlay(findFirstCompletelyVisibleItemPosition);
                                            mAdapter.videoPlayerController.handlePlayBack(feed);
                                        }
                                    }
                                }

                            }
                        }
                    });
                }
                catch (Exception je){
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
}
