package tk.pianoflow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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
	private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	public SongsManager(){		
	}
	
	/**
	 * Function to read all mp3 files from soundcloud
	 * and store the details in ArrayList
	 * */
	public ArrayList<HashMap<String, String>> getPlayList(){		
		String id = "e39f7d1a41a60e11c6772a54c834f5c5";
		String secret = "8f1e702b3333f2ca4c8dde67164c14ed";
		ApiWrapper wrapper = new ApiWrapper(id, secret, null, null);
		try {
			// Only needed for user-specific actions;
			// wrapper.login("login", "pass");
			// HttpResponse resp = wrapper.get(Request.to("/me"));
			// Get a playlist
			HttpResponse playlistResp = wrapper.get(Request
					.to("/users/60828088/playlists/11631582"));
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
					
					//Log.d("SongsManager", trackId+" : "+trackTitle+" : "+trackId);
				}	
			}
			Log.d("SongsManager", "Playlist downloaded!");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}			
		// return songs list array
		return songsList;
	}
}
