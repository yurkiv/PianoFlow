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
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.yurkiv.pianoflow.api.Playlist;
import com.yurkiv.pianoflow.api.SoundCloud;
import com.yurkiv.pianoflow.api.SoundCloudService;
import com.yurkiv.pianoflow.api.Track;
import com.yurkiv.pianoflow.util.Connectivity;
import com.yurkiv.pianoflow.util.ImageUtil;
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


public class MainActivity extends ActionBarActivity implements MediaPlayer.OnCompletionListener {
    private static final String TAG = "MainActivity";

    private List<Track> tracks;
    private MediaPlayer mp;
    private NotificationManager notificationManager;

    @InjectView(R.id.play_pause_view) protected PlayPauseView play_pause_view;
    @InjectView(R.id.bt_next_track) FloatingActionButton btNextTrack;
    @InjectView(R.id.player_progress_bar) ProgressBar progressBar;
    @InjectView(R.id.tv_title) protected TextView trackTitle;
    @InjectView(R.id.iv_background) ImageView ivBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        play_pause_view.setPlay(false);
        ivBackground.setImageBitmap(ImageUtil.decodeSampledBitmapFromResource(MainActivity.this));

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                toggleSongState();
            }
        });

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
        progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        tracks = new ArrayList<Track>();
        SoundCloudService service = SoundCloud.getService();
        service.getPlaylist(new Integer(49640862), new Callback<Playlist>() {
            @Override
            public void success(Playlist playlist, Response response) {
                updateTracks(playlist.getTracks());
                playRandomSong();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Failed call: " + error.toString());
            }
        });
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
        if(!tracks.isEmpty() && Connectivity.isConnected(getApplicationContext())){
            if(!Connectivity.isConnectedFast(getApplicationContext())){
                Toast.makeText(this, getResources().getString(R.string.slow_connection), Toast.LENGTH_LONG).show();
                return;
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.connect_error),Toast.LENGTH_LONG).show();
            return;
        }
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
            icon=R.drawable.ic_pause;
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


}
