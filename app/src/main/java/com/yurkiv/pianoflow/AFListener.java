package com.yurkiv.pianoflow;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * Created by yurkiv on 07.02.2015.
 */
public class AFListener implements AudioManager.OnAudioFocusChangeListener {
    String label = "";
    MediaPlayer mpl;
    public AFListener(MediaPlayer mpl, String label) {
        this.label = label;
        this.mpl = mpl;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        String event = "";
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS:
                event = "AUDIOFOCUS_LOSS";
                mpl.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                event = "AUDIOFOCUS_LOSS_TRANSIENT";
                mpl.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                event = "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK";
                mpl.setVolume(0.5f, 0.5f);
                break;
            case AudioManager.AUDIOFOCUS_GAIN:
                event = "AUDIOFOCUS_GAIN";
                if (!mpl.isPlaying())
                    mpl.start();
                mpl.setVolume(1.0f, 1.0f);
                break;
        }
        Log.d("AudioFocus", label + " onAudioFocusChange: " + event);
    }
}