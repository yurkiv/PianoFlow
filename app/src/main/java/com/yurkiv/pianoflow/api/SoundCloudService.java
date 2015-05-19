package com.yurkiv.pianoflow.api;

import com.yurkiv.pianoflow.model.Playlist;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by yurkiv on 16.05.2015.
 */
public interface SoundCloudService {

    static final String CLIENT_ID = "9c4981270863f59719aa8e62f7f4ccdd";

    @GET("/playlists/{id}?client_id=" + CLIENT_ID)
    public void getPlaylist(@Path("id") int id, Callback<Playlist> cb);
}
