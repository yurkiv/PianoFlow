package com.yurkiv.pianoflow.api;

import com.yurkiv.pianoflow.model.Playlist;
import com.yurkiv.pianoflow.model.Track;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by yurkiv on 16.05.2015.
 */
public interface SoundCloudService {

    static final String CLIENT_ID = "9c4981270863f59719aa8e62f7f4ccdd";

    @GET("/tracks?client_id=" + CLIENT_ID)
    public void searchSongs(@Query("q") String query, Callback<List<Track>> cb);

    @GET("/tracks?client_id=" + CLIENT_ID)
    public void getRecentSongs(@Query("created_at[from]") String date, Callback<List<Track>> cb);

    @GET("/playlists/{id}?client_id=" + CLIENT_ID)
    public void getPlaylist(@Path("id") int id, Callback<Playlist> cb);


    public void songsAfter(@Query("created_at[to]") String date, Callback<List<Track>> cb);
    public void bpmFrom(@Query("bpm[from]") String date, Callback<List<Track>> cb);
}
