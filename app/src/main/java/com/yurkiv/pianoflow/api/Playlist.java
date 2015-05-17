package com.yurkiv.pianoflow.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by yurkiv on 16.05.2015.
 */
public class Playlist {
    @SerializedName("title")
    private String mTitle;

    @SerializedName("tracks")
    private List<Track> tracks;

    public String getmTitle() {
        return mTitle;
    }

    public List<Track> getTracks() {
        return tracks;
    }
}
