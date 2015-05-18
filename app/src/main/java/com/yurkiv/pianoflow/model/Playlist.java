package com.yurkiv.pianoflow.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by yurkiv on 16.05.2015.
 */
public class Playlist {
    @SerializedName("title")
    private String title;

    @SerializedName("tracks")
    private List<Track> tracks;

    public String getTitle() {
        return title;
    }

    public List<Track> getTracks() {
        return tracks;
    }
}
