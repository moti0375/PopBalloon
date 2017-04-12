package com.bartovapps.popballoon;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bartovapps.popballoon.dialogs.HighScoreDialog;
import com.bartovapps.popballoon.utils.HighScoreManager;
import com.bartovapps.popballoon.utils.SoundHelper;
import com.bartovapps.popballoon.views.Balloon;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements Balloon.BalloonTouchListener {

    public static final int MIN_ANIMATION_DELAY = 500;
    public static final int MAX_ANIMATION_DELAY = 1500;
    public static final int MIN_ANIMATION_DURATION = 1000;
    public static final int MAX_ANIMATION_DURATION = 8000;
    public static final int NUMBER_OF_PINS = 5;
    public static final int BALLOONS_FOR_LEVEL = 5;


    public static final int BALLOON_RAW_HEIGHT = 100;
    private static final String TAG = MainActivity.class.getSimpleName();
    ViewGroup mContentView;
    private int[] mBalloonColors = new int[3];
    Random random;

    int mScreenWidth;
    int mScreenHeight;
    int mLevel = 0;
    int mScore = 0;
    int mPinsUsed = 0;
    boolean mPlaying;
    int mPoppedBalloons = 0;

    List<ImageView> mPinsImages = new ArrayList<>();

    Button playButton;
    TextView tvScore;
    TextView tvLevel;
    TextView tvHighScore;

    List<Balloon> mBalloons = new ArrayList<>();

    SoundHelper soundHelper;
    GameThread mGameThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setViews();


        random = new Random();

        mBalloonColors[0] = Color.rgb(255, 0  ,0);
        mBalloonColors[1] = Color.rgb(0  , 255,0);
        mBalloonColors[2] = Color.rgb(0  , 0  ,255);

        setFullScreen();
        soundHelper = new SoundHelper(this);
        soundHelper.prepareMusicPlayer();
        soundHelper.prepareSoundPool();
    }

    private void setViews() {
        getWindow().setBackgroundDrawableResource(R.drawable.modern_background);
        mContentView = (ViewGroup) findViewById(R.id.mainActivity);

        playButton = (Button) findViewById(R.id.go_button);
        playButton.setOnClickListener((v) -> startGame());

        tvScore = (TextView) findViewById(R.id.score_display);
        tvLevel = (TextView) findViewById(R.id.level_display);
        tvHighScore = (TextView) findViewById(R.id.high_score_display);

        mPinsImages.add((ImageView) findViewById(R.id.pin1));
        mPinsImages.add((ImageView) findViewById(R.id.pin2));
        mPinsImages.add((ImageView) findViewById(R.id.pin3));
        mPinsImages.add((ImageView) findViewById(R.id.pin4));
        mPinsImages.add((ImageView) findViewById(R.id.pin5));



        ViewTreeObserver viewTreeObserver = mContentView.getViewTreeObserver();

        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mContentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mScreenWidth = mContentView.getWidth();
                    mScreenHeight = mContentView.getHeight();
                }
            });
        }

        mContentView.setOnClickListener((v) -> setFullScreen());
        updateDisplay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setFullScreen();
    }

    private void setFullScreen() {
        ViewGroup rootLayout = (ViewGroup) findViewById(R.id.mainActivity);

        rootLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

    }

    void startGame() {
        mScore = 0;
        mPoppedBalloons = 0;
        mLevel = 0;
        mPinsUsed = 0;
        mPlaying = true;


        for (ImageView pin : mPinsImages) {
            pin.setImageResource(R.drawable.pin);
        }

        playButton.setEnabled(false);

        mGameThread = new GameThread();
        mGameThread.isPlaying = true;
        updateLevel();
        mGameThread.start();
        soundHelper.playMusic();
    }

    void updateLevel() {
        mLevel++;
        mGameThread.setLevel(mLevel);
        updateDisplay();
    }

    @Override
    public void popBalloon(Balloon balloon, boolean touched) {

        Log.i(TAG, "popBalloon: is touched = " + touched);
        mContentView.removeView(balloon);
        mBalloons.remove(balloon);

        if (touched) {
            soundHelper.playPopSound(null);
            mScore++;
            mPoppedBalloons++;

            if (mPoppedBalloons == BALLOONS_FOR_LEVEL) {
                finishedLevel();
            }


        } else {
            soundHelper.playMissedBalloon();
            if (mPinsUsed < NUMBER_OF_PINS) {
                Toast.makeText(this, "Missed that one...", Toast.LENGTH_SHORT).show();
                mPinsImages.get(mPinsUsed).setImageResource(R.drawable.pin_off);
                mPinsUsed++;
            }
            if (mPinsUsed == NUMBER_OF_PINS) {
                soundHelper.playGameOver();
                gameOver(true);
            }
        }
        updateDisplay();
    }

    private void gameOver(boolean status) {
        Toast.makeText(this, "Game Over", Toast.LENGTH_LONG).show();
        soundHelper.pauseMusic();
        mPlaying = false;
        mGameThread.stopGame();
        playButton.setEnabled(true);
        playButton.setText(getString(R.string.play_game));

        for (Balloon balloon : mBalloons) {
            mContentView.removeView(balloon);
            balloon.setPopped(true);

        }
        mBalloons.clear();

        if (HighScoreManager.isHighScore(this, mScore)) {
            HighScoreManager.setHighScore(this, mScore);
            HighScoreDialog dialog = HighScoreDialog.newInstance("High Score!", "Great new high score " + mScore);
            dialog.show(getSupportFragmentManager(), null);
        }

        updateDisplay();
    }

    private void finishedLevel() {
        mPoppedBalloons = 0;
        Toast.makeText(this, String.format(Locale.getDefault(), "You've finished level: %d", mLevel), Toast.LENGTH_SHORT).show();
        updateLevel();
    }

    private void updateDisplay() {
        tvLevel.setText(String.valueOf(mLevel));
        tvScore.setText(String.valueOf(mScore));
        tvHighScore.setText(String.valueOf(HighScoreManager.getHighScore(this)));

        if (mPlaying) {
            playButton.setText(String.format(Locale.getDefault(), "Level: %d", mLevel));
        }
        // TODO: 10/04/2017 update the display

    }



    private class GameThread extends Thread{

        private boolean isPlaying = true;
        private int mLevel = 1;


        @Override
        public void run() {
            super.run();

            while(isPlaying){
                int maxDelay = Math.max(MIN_ANIMATION_DELAY, MAX_ANIMATION_DELAY - ((mLevel - 1) * 500));
                int minDelay = maxDelay / 2;
                while (mPlaying) {
                    Random random = new Random(new Date().getTime());
                    int xPos = random.nextInt(mScreenWidth - 200);
                    runOnUiThread(() -> launchBalloon(xPos));

//                    launchBalloon(xPos);
                    int delay = random.nextInt(minDelay) + minDelay;

                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }

        void stopGame(){
            isPlaying = false;
        }

        private void setLevel(int level){
            mLevel = level;
        }
    }

    private void launchBalloon(int newPosition) {

        Balloon newBalloon = new Balloon(this, mBalloonColors[random.nextInt(mBalloonColors.length)], BALLOON_RAW_HEIGHT);
        mBalloons.add(newBalloon);
        newBalloon.setX(newPosition);
        newBalloon.setY(mScreenHeight);
        mContentView.addView(newBalloon);

        int duration = Math.max(MIN_ANIMATION_DURATION, MAX_ANIMATION_DURATION - (mLevel * 1000));
        newBalloon.releaseBalloon(mScreenHeight, duration);

    }
}
