package com.yurkiv.pianoflow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Request;

import android.util.Log;


public class SongsManager {

    private static final String TAG="SongsManager";
    private static final String CLIENT_ID="9c4981270863f59719aa8e62f7f4ccdd";
    private static final String CLIENT_SECRET="354924134b97c9221a97cd249ffb7275";

	/**
	 * Function to read all mp3 files from soundcloud
	 * and store the details in ArrayList
	 * */
	public static ArrayList<HashMap<String, String>> getPlayList(){
        ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
		String[] playlistArray={"/users/112076272/playlists/49640862",
								"/users/112076272/playlists/56088270"};		
		String playlistUrl=playlistArray[new Random().nextInt(playlistArray.length)];
		
		ApiWrapper wrapper = new ApiWrapper(CLIENT_ID, CLIENT_SECRET, null, null);
		try {
			// Only needed for user-specific actions;
			// wrapper.login("login", "pass");
			// HttpResponse resp = wrapper.get(Request.to("/me"));
			// Get a playlist			
			HttpResponse playlistResp = wrapper.get(Request.to(playlistUrl));
			// Track JSON response OK?			
			if (playlistResp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				JSONObject playlistJSON = new JSONObject(
						EntityUtils.toString(playlistResp.getEntity()));
				Log.d("SongsManager", playlistJSON.get("title") + ":");
				JSONArray array = playlistJSON.getJSONArray("tracks");
				
				for (int i = 0; i < array.length(); i++) {
					String trackId = array.getJSONObject(i).getString("id");
					String trackTitle = array.getJSONObject(i).getString("title");					
					HashMap<String, String> song = new HashMap<String, String>();
					song.put("songTitle", trackTitle);
					song.put("songPath", trackId);					
					// Adding each song to SongList
					songsList.add(song);
					
					//Log.d("SongsManager", trackId+" : "+trackTitle);
				}	
			}
			Log.d(TAG, "Playlist downloaded!");
		} catch (IOException e) {
			Log.e(TAG, "IOException", e);
		} catch (ParseException e) {
            Log.e(TAG, "ParseException", e);
		} catch (JSONException e) {
            Log.e(TAG, "JSONException", e);
		}			
		// return songs list array
		return songsList;
	}

    public static String getTrackStreamUrl(String trackId) throws IOException, JSONException {
        String streamurl = "";
        ApiWrapper wrapper = new ApiWrapper(CLIENT_ID, CLIENT_SECRET, null, null);
        HttpResponse trackResp = wrapper.get(Request.to("/tracks/" + trackId));
        if (trackResp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            JSONObject trackJSON = new JSONObject(EntityUtils.toString(trackResp.getEntity()));
            // If track is streamable, fetch the stream URL
            // (mp3-https) and start the MediaPlayer
            if (trackJSON.getBoolean("streamable")) {
                HttpResponse streamResp = wrapper.get(Request.to("/tracks/" + trackId + "/stream"));
                JSONObject streamJSON = new JSONObject(EntityUtils.toString(streamResp.getEntity()));
                streamurl = streamJSON.getString("location");
            }
        }
        if(streamurl.length()>4){
            streamurl=removeCharAt(streamurl, 4);
        }
        return streamurl;
    }

    public static String removeCharAt(String s, int pos) {
        return s.substring(0, pos) + s.substring(pos + 1);
    }
}
