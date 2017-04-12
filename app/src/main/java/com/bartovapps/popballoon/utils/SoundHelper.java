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

/**
 * Created by motibartov on 11/04/2017.
 */

public class SoundHelper {

    private SoundPool mSoundPool;
    private MediaPlayer mMusicPlayer;
    private MediaPlayer mPopSound;
    private Activity mActivity;

    private boolean mLoaded;
    private float mVolume;

    private int poppedSoundId;
    private int gameOverSoundId;
    private int missedSoundId;

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

        mSoundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> mLoaded = true);

        poppedSoundId = mSoundPool.load(mActivity, R.raw.balloon_pop, 1);
        gameOverSoundId = mSoundPool.load(mActivity, R.raw.game_over, 1);
        missedSoundId = mSoundPool.load(mActivity, R.raw.missed_ballon, 1);


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
            mSoundPool.play(poppedSoundId, mVolume, mVolume, 1, 0, 1f);
        }
    }

    public void playGameOver(){
        if(mLoaded){
            mSoundPool.play(gameOverSoundId, mVolume, mVolume, 1, 0, 1f);
        }
    }


    public void playMissedBalloon(){
        if(mLoaded){
            mSoundPool.play(missedSoundId, mVolume, mVolume, 1, 0, 1f);
        }
    }
}
