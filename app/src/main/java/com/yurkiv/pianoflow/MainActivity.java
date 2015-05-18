package com.yurkiv.pianoflow;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.melnykov.fab.FloatingActionButton;
import com.yurkiv.pianoflow.api.SoundCloud;
import com.yurkiv.pianoflow.api.SoundCloudService;
import com.yurkiv.pianoflow.model.Playlist;
import com.yurkiv.pianoflow.model.Track;
import com.yurkiv.pianoflow.util.AudioFocusListener;
import com.yurkiv.pianoflow.util.Connectivity;
import com.yurkiv.pianoflow.view.PlayPauseView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
    private static final String TAG = "MainActivity";

    private List<Track> tracks;
    private MediaPlayer mp;
    private NotificationManager notificationManager;
    private AudioManager audioManager;
    private AudioFocusListener audioFocusListenerMusic;

    @InjectView(R.id.play_pause_view) protected PlayPauseView play_pause_view;
    @InjectView(R.id.bt_next_track) protected FloatingActionButton btNextTrack;
    @InjectView(R.id.player_progress_bar) protected ProgressBar progressBar;
    @InjectView(R.id.tv_title) protected TextView trackTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        play_pause_view.setPlay(false);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                toggleSongState();
            }
        });

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        audioFocusListenerMusic = new AudioFocusListener(mp, "PianoFlow");

        play_pause_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSongState();
            }
        });
        btNextTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playRandomSong();
            }
        });

        tracks = new ArrayList<>();
        int[] playlistIdArray={49640862, 56088270};
        final int playlistId=playlistIdArray[new Random().nextInt(playlistIdArray.length)];
        if (checkConnection()){
            SoundCloudService service = SoundCloud.getService();
            service.getPlaylist(new Integer(playlistId), new Callback<Playlist>() {
                @Override
                public void success(Playlist playlist, Response response) {
                    updateTracks(playlist.getTracks());
                    Log.d(TAG, "loaded playlist id="+playlistId+" title: "+playlist.getTitle());
                    playRandomSong();
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d(TAG, "Failed call: " + error.toString());
                }
            });
        }
    }

    private void updateTracks(List<Track> tracks){
        this.tracks.clear();
        this.tracks.addAll((tracks));
    }


    private void toggleSongState() {
        if (mp.isPlaying()){
            mp.pause();
            play_pause_view.setPlay(false);
            notificatePlayingStatus(true);
        }else{
            checkConnection();
            mp.start();
            toggleProgressBar();
            play_pause_view.setPlay(true);
            notificatePlayingStatus(false);
        }
    }

    private void toggleProgressBar() {
        if (mp.isPlaying()){
            progressBar.setVisibility(View.INVISIBLE);
            play_pause_view.setClickable(true);
        }else{
            progressBar.setVisibility(View.VISIBLE);
            play_pause_view.setClickable(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mp != null){
            if(mp.isPlaying()){
                mp.stop();
            }
            mp.release();
            mp = null;
            notificationManager.cancel(1);
        }
        if (audioFocusListenerMusic != null){
            audioManager.abandonAudioFocus(audioFocusListenerMusic);
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
        if (id == R.id.search_view) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playRandomSong();
    }

    private void playRandomSong(){
        checkConnection();
        Random rand = new Random();
        int currentSongIndex = rand.nextInt((tracks.size() - 1) - 0 + 1) + 0;
        Log.d(TAG, String.valueOf(currentSongIndex));
        Track selectedTrack = tracks.get(currentSongIndex);
        trackTitle.setText(selectedTrack.getTitle());

        if (mp.isPlaying()){
            mp.stop();
        }
        mp.reset();
        toggleProgressBar();

        try {
            mp.setDataSource(selectedTrack.getStreamURL() + "?client_id=" + SoundCloudService.CLIENT_ID);
            mp.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void notificatePlayingStatus(boolean isPaused) {
        int icon=R.drawable.ic_play_not;
        if (isPaused) {
            icon=R.drawable.ic_pause_not;
        }
        Intent notificationIntent = new Intent(this, MainActivity.class);
        NotificationCompat.Builder nb = new NotificationCompat.Builder(this)
                .setSmallIcon(icon)
                .setColor(getResources().getColor(R.color.primary_color))
                .setContentText("Music to quiet your world")
                .setContentTitle("PianoFlow")
                .setContentIntent(PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT));

        Notification notification = nb.build();
        notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(1, notification);
    }

    private boolean checkConnection(){
        if(Connectivity.isConnected(getApplicationContext())){
            if(!Connectivity.isConnectedFast(getApplicationContext())){
                showDialog(getResources().getString(R.string.slow_connection));
                return false;
            }
        } else {
            showDialog(getResources().getString(R.string.connect_error));
            return false;
        }
        return true;
    }

    private void showDialog(String msg){
        if(!this.isFinishing()){
            new MaterialDialog.Builder(this)
                    .content(msg)
                    .positiveText("OK")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .show();
        }
    }

}
