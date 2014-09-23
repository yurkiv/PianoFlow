package com.yurkiv.pianoflow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.androidhive.musicplayer.R;
import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Request;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnCompletionListener{

	//private ImageButton btnPlay;
	//private ImageButton btnNext;
	//private ImageButton btnPrevious;
	//private ImageButton btnPlaylist;
	//private ImageButton btnRepeat;
	//private ImageButton btnShuffle;
	private ImageButton playPauseButton;
	//private TextView songTitleLabel;
	// Media Player
	private  MediaPlayer mp;
	private SongsManager songManager;
	private int currentSongIndex = 0; 
	private boolean isShuffle = true;
	private boolean isRepeat = false;
	private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		
		// All player buttons
		//btnPlay = (ImageButton) findViewById(R.id.btnPlay);
		//btnNext = (ImageButton) findViewById(R.id.btnNext);
		//btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
		//btnPlaylist = (ImageButton) findViewById(R.id.btnPlaylist);
		//btnRepeat = (ImageButton) findViewById(R.id.btnRepeat);
		//btnShuffle = (ImageButton) findViewById(R.id.btnShuffle);
		//songTitleLabel = (TextView) findViewById(R.id.songTitle);
		playPauseButton = (ImageButton) findViewById(R.id.playpauseButton);
		// Mediaplayer
		mp = new MediaPlayer();
		songManager = new SongsManager();		
		
		// Listeners		
		mp.setOnCompletionListener(this); // Important
		
		// Getting all songs list
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				songsList = songManager.getPlayList();
			}
		}).start();
		
		
		// By default play first song
		//playSong(0);
				
		/**
		 * Play button click event
		 * plays a song and changes button to pause image
		 * pauses a song and changes button to play image
		 * */
		playPauseButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// check for already playing
				if(mp.isPlaying()){
					if(mp!=null){
						mp.pause();
						// Changing button image to play button
						playPauseButton.setImageResource(R.drawable.play);
					}
				}else{
					// Resume song
					if(mp!=null){
						mp.start();
						// Changing button image to pause button
						playPauseButton.setImageResource(R.drawable.pause);
					}
				}				
			}
		});
		
		/**
		 * Next button click event
		 * Plays next song by taking currentSongIndex + 1
		 * */
//		btnNext.setOnClickListener(new View.OnClickListener() {			
//			@Override
//			public void onClick(View arg0) {
//				// check if next song is there or not
//				if(currentSongIndex < (songsList.size() - 1)){
//					playSong(currentSongIndex + 1);
//					currentSongIndex = currentSongIndex + 1;
//				}else{
//					// play first song
//					playSong(0);
//					currentSongIndex = 0;
//				}				
//			}
//		});
		
		/**
		 * Back button click event
		 * Plays previous song by currentSongIndex - 1
		 * */
//		btnPrevious.setOnClickListener(new View.OnClickListener() {			
//			@Override
//			public void onClick(View arg0) {
//				if(currentSongIndex > 0){
//					playSong(currentSongIndex - 1);
//					currentSongIndex = currentSongIndex - 1;
//				}else{
//					// play last song
//					playSong(songsList.size() - 1);
//					currentSongIndex = songsList.size() - 1;
//				}				
//			}
//		});
		
		/**
		 * Button Click event for Repeat button
		 * Enables repeat flag to true
		 * */
//		btnRepeat.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				if(isRepeat){
//					isRepeat = false;
//					Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
//					btnRepeat.setImageResource(R.drawable.btn_repeat);
//				}else{
//					// make repeat to true
//					isRepeat = true;
//					Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
//					// make shuffle to false
//					isShuffle = false;
//					btnRepeat.setImageResource(R.drawable.btn_repeat_focused);
//					btnShuffle.setImageResource(R.drawable.btn_shuffle);
//				}	
//			}
//		});
		
		/**
		 * Button Click event for Shuffle button
		 * Enables shuffle flag to true
		 * */
//		btnShuffle.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				if(isShuffle){
//					isShuffle = false;
//					Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
//					btnShuffle.setImageResource(R.drawable.btn_shuffle);
//				}else{
//					// make repeat to true
//					isShuffle= true;
//					Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
//					// make shuffle to false
//					isRepeat = false;
//					btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);
//					btnRepeat.setImageResource(R.drawable.btn_repeat);
//				}	
//			}
//		});
		
		/**
		 * Button Click event for Play list click event
		 * Launches list activity which displays list of songs
		 * */
//		btnPlaylist.setOnClickListener(new View.OnClickListener() {			
//			@Override
//			public void onClick(View arg0) {
//				Intent i = new Intent(getApplicationContext(), PlayListActivity.class);
//				startActivityForResult(i, 100);			
//			}
//		});		
	}
	
	/**
	 * Receiving song index from playlist view
	 * and play the song
	 * */
	@Override
    protected void onActivityResult(int requestCode,
                                     int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 100){
         	 currentSongIndex = data.getExtras().getInt("songIndex");
         	 // play selected song
             playSong(currentSongIndex);
        } 
    }
	
	/**
	 * Function to play a song
	 * @param songIndex - index of song
	 * */
	public void  playSong(int songIndex){
		// Play song
		try {
        	mp.reset();			
			mp.setAudioStreamType(AudioManager.STREAM_MUSIC);			
			String trackId=songsList.get(songIndex).get("songPath");
			String streamurl = "";
			try{
				String id = "e39f7d1a41a60e11c6772a54c834f5c5";
				String secret = "8f1e702b3333f2ca4c8dde67164c14ed";
				ApiWrapper wrapper = new ApiWrapper(id, secret, null, null);
				HttpResponse trackResp = wrapper.get(Request.to("/tracks/"
						+ trackId));
				if (trackResp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					JSONObject trackJSON = new JSONObject(
							EntityUtils.toString(trackResp.getEntity()));
					// If track is streamable, fetch the stream URL
					// (mp3-https) and start the MediaPlayer
					if (trackJSON.getBoolean("streamable")) {
						HttpResponse streamResp = wrapper.get(Request
								.to("/tracks/" + trackId + "/stream"));
						JSONObject streamJSON = new JSONObject(
								EntityUtils.toString(streamResp.getEntity()));
						streamurl = streamJSON.getString("location");
					}
				}					
				if(streamurl.length()>4){
					streamurl=removeCharAt(streamurl, 4);
				}
			} catch(JSONException e){
				Toast.makeText(getApplicationContext(), "Can't connect to server!", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}			
			try {
				mp.setDataSource(streamurl);
			} catch (IllegalArgumentException e) {				
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				mp.prepare();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			mp.start();
			
			notification();
			// Displaying Song title
			String songTitle = songsList.get(songIndex).get("songTitle");
        	//songTitleLabel.setText(songTitle);
			
        	// Changing Button Image to pause image
			playPauseButton.setImageResource(R.drawable.pause);					
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * On Song Playing completed
	 * if repeat is ON play same song again
	 * if shuffle is ON play random song
	 * */
	@Override
	public void onCompletion(MediaPlayer arg0) {
		
		// check for repeat is ON or OFF
		if(isRepeat){
			// repeat is on play same song again
			playSong(currentSongIndex);
		} else if(isShuffle){
			// shuffle is on - play a random song
			Random rand = new Random();
			currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
			playSong(currentSongIndex);
		} else{
			// no repeat or shuffle ON - play next song
			if(currentSongIndex < (songsList.size() - 1)){
				playSong(currentSongIndex + 1);
				currentSongIndex = currentSongIndex + 1;
			}else{
				// play first song
				playSong(0);
				currentSongIndex = 0;
			}
		}
	}
	
	@Override
	 public void onDestroy(){
	 super.onDestroy();
	    mp.release();
	 }
	
	public static String removeCharAt(String s, int pos) {
		return s.substring(0, pos) + s.substring(pos + 1);
	}
	
	private void notification(){
		
	}
}