package com.yurkiv.pianoflow;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.yurkiv.pianoflow.util.Connectivity;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class MainActivity extends ActionBarActivity implements MediaPlayer.OnCompletionListener {

    private static final String TAG="MainActivity";

    private ImageButton btnPlayPause;
    private MediaPlayer mp;

    private int currentSongIndex = 0;
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    private NotificationManager notificationManager;
    private AudioManager audioManager;
    private AFListener afListenerMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        //tintManager.setTintColor(Color.parseColor("#99000FF"));

        btnPlayPause = (ImageButton) findViewById(R.id.fab);

        mp =new MediaPlayer();

        mp.setOnCompletionListener(this);

        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

        loadPlaylist();

        afListenerMusic = new AFListener(mp, "PianoFlow");
        int requestResult = audioManager.requestAudioFocus(afListenerMusic,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        Log.d(TAG, "Music request focus, result: " + requestResult);

        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (songsList.isEmpty()){
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.connect_error), Toast.LENGTH_LONG).show();
                    return;
                }
                if (mp!=null){
                    if (mp.isPlaying()){
                        mp.pause();
                        notificationPlayingStatus(true);
                        // Changing button image to play button
                        btnPlayPause.setImageResource(R.drawable.ic_play);
                    } else {
                        // Resume song
                        mp.start();
                        notificationPlayingStatus(false);
                        // Changing button image to pause button
                        btnPlayPause.setImageResource(R.drawable.ic_pause);
                    }
                }
            }
        });
    }

    private void loadPlaylist() {
        if(Connectivity.isConnected(getApplicationContext())){
            if(Connectivity.isConnectedFast(getApplicationContext())){
                new LoadPlaylistAsync(MainActivity.this).execute();
            } else {
                Toast.makeText(this, getResources().getString(R.string.slow_connection),Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.connect_error),Toast.LENGTH_LONG).show();
        }
    }

    private void playTrack(int trackIndex){
        new AsyncTask<Integer, Void, String>(){
            @Override
            protected String doInBackground(Integer... params) {
                playSongIntoAsyncTask(params[0]);
                return null;
            }
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (result != null) {
                    Log.d("playTrackAsyncTask", "Error: " + result);
                }
            }
        }.execute(trackIndex);
    }

    private void playSongIntoAsyncTask(Integer songIndex) {
        // Play song
        if(songsList.isEmpty()){
            Toast.makeText(this, getResources().getString(R.string.connect_error),Toast.LENGTH_LONG).show();
            return;
        }
        try {
            mp.reset();
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            String trackId=songsList.get(songIndex).get("songPath");

            Log.d(TAG, songsList.get(songIndex).get("songTitle"));

            String trackUrl=SongsManager.getTrackStreamUrl(trackId);
            mp.setDataSource(trackUrl);
            mp.prepare();
            mp.start();

            // Changing Button Image to pause image
            btnPlayPause.setImageResource(R.drawable.ic_pause);

        } catch (IllegalArgumentException e) {
            Log.e(TAG, "IllegalArgumentException", e);
        } catch (IllegalStateException e) {
            Log.e(TAG, "IllegalStateException", e);
        } catch (IOException e) {
            Log.e(TAG, "IOException", e);
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Can't connect to server!", Toast.LENGTH_LONG).show();
            Log.e(TAG, "JSONException into SongsManager", e);
        }
    }

    private void notificationPlayingStatus(boolean isPaused) {
        int icon=R.drawable.ic_play;
        if (isPaused) {
            icon=R.drawable.ic_pause;
        }
        Intent notificationIntent = new Intent(this, MainActivity.class);
        NotificationCompat.Builder nb = new NotificationCompat.Builder(this)
                .setSmallIcon(icon)
                .setContentText("Music to quiet your world")
                .setContentTitle("PianoFlow")
                .setContentIntent(PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT));

        Notification notification = nb.build();
        notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(1, notification);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(!songsList.isEmpty() && Connectivity.isConnected(getApplicationContext())){
            if(!Connectivity.isConnectedFast(getApplicationContext())){
                Toast.makeText(this, getResources().getString(R.string.slow_connection),Toast.LENGTH_LONG).show();
                return;
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.connect_error),Toast.LENGTH_LONG).show();
            return;
        }
        // play a random song
        Random rand = new Random();
        currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
        playTrack(currentSongIndex);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mp != null){
            mp.release();
            notificationManager.cancel(1);
        }
        if (afListenerMusic != null){
            audioManager.abandonAudioFocus(afListenerMusic);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    private class LoadPlaylistAsync extends AsyncTask<String, Void, Exception>{
        private MaterialDialog materialDialog;

        public LoadPlaylistAsync(MainActivity context){
            MaterialDialog.Builder builder=new MaterialDialog.Builder(context);
            builder.customView(R.layout.dialog_customview, false).cancelable(false);
            materialDialog=builder.build();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            materialDialog.show();
        }

        @Override
        protected Exception doInBackground(String... params) {
            songsList = SongsManager.getPlayList();
            return null;
        }

        @Override
        protected void onPostExecute(Exception result) {
            super.onPostExecute(result);
            materialDialog.dismiss();
        }
    }
}
