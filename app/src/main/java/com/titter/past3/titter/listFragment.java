package com.titter.past3.titter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.titter.past3.titter.adapter.ListAdapter;
import com.titter.past3.titter.model.feedModel;

import java.util.ArrayList;

/**
 * Created by SMILECS on 8/16/16.
 */
public class listFragment extends ListFragment {

    public ListView list;
    ArrayList<feedModel> model;
    ListAdapter listAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.others, container, false);
        list = (ListView) v.findViewById(android.R.id.list);
        model =  new ArrayList<>();
        feedModel mode = new feedModel();
        feedModel mode2 = new feedModel();
        mode.setTag("About Us");
        mode2.setTag("FAQ");
        model.add(mode);
        model.add(mode2);
        listAdapter = new ListAdapter(getContext(), model);
        list.setAdapter(listAdapter);
        return v;
    }
}
