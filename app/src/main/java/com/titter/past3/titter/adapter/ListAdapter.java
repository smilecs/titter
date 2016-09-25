package com.titter.past3.titter.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.titter.past3.titter.R;
import com.titter.past3.titter.model.feedModel;

import java.util.ArrayList;

/**
 * Created by SMILECS on 8/16/16.
 */
public class ListAdapter extends ArrayAdapter<feedModel> {

    public ListAdapter(Context context, ArrayList<feedModel> model){
        super(context, R.layout.others, R.id.list_item, model);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        feedModel model = getItem(position);
        try{
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
            Typeface robot = Typeface.createFromAsset(getContext().getAssets(),
                    "fonts/Roboto-Medium.ttf");
            TextView header = (TextView) convertView.findViewById(R.id.list_item);
            header.setTypeface(robot);
            header.setText(model.getTag());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return convertView;
    }
}
