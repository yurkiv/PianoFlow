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

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Request;

public class MainActivity extends Activity implements OnCompletionListener{

	
	private ImageButton playPauseButton;
	private ImageButton shareButton;
	private Button aboutButton;
	// Media Player
	private  MediaPlayer mp;
	private SongsManager songManager;
	private int currentSongIndex = 0; 
	private boolean isShuffle = true;
	private boolean isRepeat = false;
	private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	private NotificationManager manager;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		
		// All player buttons		
		playPauseButton = (ImageButton) findViewById(R.id.playpauseButton);
		shareButton = (ImageButton) findViewById(R.id.shareButton);
		aboutButton=(Button) findViewById(R.id.aboutButton);
		
		// Mediaplayer
		mp = new MediaPlayer();
		songManager = new SongsManager();		
		
		// Listeners		
		mp.setOnCompletionListener(this); // Important
		
		manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		
		// Getting all songs list
		loadPlaylist();
		
		
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
				if(songsList.isEmpty()){
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.download_error),Toast.LENGTH_LONG).show();
					return;
				}
				if(mp.isPlaying()){
					if(mp!=null){
						mp.pause();
						notification(true);
						// Changing button image to play button
						playPauseButton.setImageResource(R.drawable.play);
					}
				}else{
					// Resume song
					if(mp!=null){
						mp.start();
						notification(false);
						// Changing button image to pause button
						playPauseButton.setImageResource(R.drawable.pause);
					}
				}				
			}
		});
		
		shareButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				shareApp();				
			}
		});
		
		aboutButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intentAbout = new Intent(getApplicationContext(), AboutActivity.class);
				startActivity(intentAbout);	
			}
		});
	}
				
	/**
	 * Function to play a song
	 * @param songIndex - index of song
	 * */
	public void  playSong(int songIndex){
		// Play song
		if(songsList.isEmpty()){
			Toast.makeText(this, getResources().getString(R.string.download_error),Toast.LENGTH_LONG).show();
			return;
		}
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
		if(songsList.isEmpty()){
			Toast.makeText(this, getResources().getString(R.string.download_error),Toast.LENGTH_LONG).show();
			return;
		}
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
	    manager.cancel(1);
	 }
	
	
	private void shareApp(){
		String text="http://pianoflow.tk - PianoFlow - Music to Quiet Your World. " +
				"Simply moving, simply beautiful...";		
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, text);
		sendIntent.setType("text/plain");	
		try {
			startActivity(Intent.createChooser(sendIntent, "Share PianoFlow:"));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(MainActivity.this, "There are no Share clients installed.", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void loadPlaylist() {
		if(isNetworkConnected()){			
			if(isGoodSpeed()){
				new Connection(this, new Notifier() {
					@Override
					public void operationFinished(Context context, int city, String cityname) {
					}
				}).execute();
			} else {
				Toast.makeText(this, getResources().getString(R.string.slow_connection),Toast.LENGTH_LONG).show();
				//finish();
			}			
		} else {
			Toast.makeText(this, getResources().getString(R.string.download_error),Toast.LENGTH_LONG).show();
			//finish();
		}
	}
	
	public static String removeCharAt(String s, int pos) {
		return s.substring(0, pos) + s.substring(pos + 1);
	}
	
	private void notification(boolean isPaused){
		int icon=R.drawable.play;
		if (isPaused) {
			icon=R.drawable.pause;
		}		
		Intent notificationIntent = new Intent(this, MainActivity.class);
	    NotificationCompat.Builder nb = new NotificationCompat.Builder(this)	    
	        .setSmallIcon(icon)
	        .setContentText("Music to quiet your world")
	        .setContentTitle("PianoFlow")
	        .setContentIntent(PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT));
	        
	        Notification notification = nb.build();
	        notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
	        manager.notify(1, notification);
	      
	}
	
	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// There are no active networks.
			return false;
		} else
			return true;
	}
	
	private boolean isGoodSpeed() {
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = cm.getActiveNetworkInfo();		
		if(activeNetInfo.getType()==ConnectivityManager.TYPE_WIFI){
			return true;
		}		
		int type = telephonyManager.getNetworkType();
		switch (type) {
		case TelephonyManager.NETWORK_TYPE_UNKNOWN:
			return true;
		case TelephonyManager.NETWORK_TYPE_GPRS:
			return false;
		case TelephonyManager.NETWORK_TYPE_EDGE:
			return false;
		case TelephonyManager.NETWORK_TYPE_UMTS:
			return true;
		case TelephonyManager.NETWORK_TYPE_HSDPA:
			return true;
		case TelephonyManager.NETWORK_TYPE_HSUPA:
			return true;
		case TelephonyManager.NETWORK_TYPE_HSPA:
			return true;
		case TelephonyManager.NETWORK_TYPE_CDMA:
			return true;
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
			return true;
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
			return true;		
		case TelephonyManager.NETWORK_TYPE_EVDO_B:
			return true;		 
		case TelephonyManager.NETWORK_TYPE_1xRTT:
			return true;
		case TelephonyManager.NETWORK_TYPE_IDEN:
			return true;			
		case TelephonyManager.NETWORK_TYPE_LTE:
			return true;	
		case TelephonyManager.NETWORK_TYPE_EHRPD:
			return true;
		default:
			return true;
		}
	}
	
	private class Connection extends AsyncTask<String, Void, Exception> {
		private ProgressDialog progressDialog;
		private Context context;
		private Notifier notifier;
		@SuppressWarnings("unused")
		private Exception exception;		
		public Connection(Context context, Notifier notifier) {
            if (context == null) throw new IllegalArgumentException("parentActivity");           
            this.context = context;  
            this.notifier=notifier;
            this.progressDialog = new ProgressDialog(this.context);
            this.progressDialog.setIndeterminate(true);
            this.progressDialog.setCancelable(false);
        }		
		@Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog.setTitle(getResources().getString(R.string.please_wait));
            this.progressDialog.setMessage(getResources().getString(R.string.loading_forecast));
            this.progressDialog.show();
        }					
		@Override
		protected Exception doInBackground(String... params) {
			try {
				songsList = songManager.getPlayList();
			} catch (Exception e) {				
				exception=e;
			}
			return null;
		}
		@Override
		protected void onPostExecute(Exception result) {
			super.onPostExecute(result);
			if (result != null) {
				//this.progressDialog.dismiss();
				Log.d("DownloadManager", "Error: " + result);
				Toast.makeText(context, getResources().getString(R.string.download_error),Toast.LENGTH_LONG).show();
	         } else {
	        	 if (this.progressDialog.isShowing()) {
	                 this.progressDialog.dismiss();
	             } 	             
	             if (notifier != null) {
	                 notifier.operationFinished(context, 0, "");
	             }
	         }				 
		}		
	}
}