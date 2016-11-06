package com.titter.past3.titter.model;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by SMILECS on 4/19/16.
 */
public class feedModel extends RealmObject implements Serializable{
    @PrimaryKey
            String id;
    String ViewType;
    String URL;
    String WebUrl;
    String Tag;
    String index;
    String available = "";
    String playing;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getPlaying() {
        return playing;
    }

    public void setPlaying(String playing) {
        this.playing = playing;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getTag() {
        return Tag;
    }

    public void setTag(String tag) {
        Tag = tag;
    }

    public String getViewType() {
        return ViewType;
    }

    public void setViewType(String viewType) {
        ViewType = viewType;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getWebUrl() {
        return WebUrl;
    }

    public void setWebUrl(String webUrl) {
        WebUrl = webUrl;
    }

    @Override
    public String toString() {
        return Tag;
    }
}
