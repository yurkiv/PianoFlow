package com.yurkiv.pianoflow.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by yurkiv on 16.05.2015.
 */
public class Playlist {
    @SerializedName("title")
    private String title;

    @SerializedName("tracks")
    private ArrayList<Track> tracks;

    public String getTitle() {
        return title;
    }

    public ArrayList<Track> getTracks() {
        return tracks;
    }
}
