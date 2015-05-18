package com.yurkiv.pianoflow.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yurkiv on 16.05.2015.
 */
public class Track {
    @SerializedName("title")
    private String title;

    @SerializedName("stream_url")
    private String streamURL;

    @SerializedName("id")
    private int id;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStreamURL() {
        return streamURL;
    }
}
