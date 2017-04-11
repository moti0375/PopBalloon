package com.bartovapps.popballoon.utils;

import android.app.Activity;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.view.View;

import com.bartovapps.popballoon.R;

import java.util.jar.Attributes;

/**
 * Created by motibartov on 11/04/2017.
 */

public class SoundHelper {

    SoundPool mSoundPool;
    MediaPlayer mMusicPlayer;
    MediaPlayer mPopSound;
    Activity mActivity;

    boolean mLoaded;
    float mVolume;

    int mSoundID;

    public SoundHelper(Activity context) {
        mActivity = context;

    }

    public void prepareMusicPlayer() {
        //Using getApplicationContext will help to improve media player operation during configuration changes
        mPopSound = MediaPlayer.create(mActivity.getApplicationContext(), R.raw.balloon_pop);
        mMusicPlayer = MediaPlayer.create(mActivity.getApplicationContext(), R.raw.pleasant_music);
        mMusicPlayer.setVolume(.5f, .5f);
        mMusicPlayer.setLooping(true);
    }

    public void prepareSoundPool() {
        AudioManager audioManager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
        float actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mVolume = actVolume / maxVolume;

        mActivity.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attributes = new AudioAttributes.Builder().
                    setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            mSoundPool = new SoundPool.Builder().setAudioAttributes(attributes).build();

        } else {
            mSoundPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
        }

        mSoundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
            mLoaded = true;
        });

        mSoundID = mSoundPool.load(mActivity, R.raw.balloon_pop, 1);

    }

    public void playMusic() {
        if (mMusicPlayer != null) {
            mMusicPlayer.start();
        }
    }

    public void pauseMusic() {
        if (mMusicPlayer != null && mMusicPlayer.isPlaying()) {
            mMusicPlayer.pause();
        }
    }

    public void playPopSound(View view) {
        if(mLoaded){
            mSoundPool.play(mSoundID, mVolume, mVolume, 1, 0, 1f);
        }
    }
}
