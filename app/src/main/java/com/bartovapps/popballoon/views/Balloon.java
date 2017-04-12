package com.bartovapps.popballoon.views;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import com.bartovapps.popballoon.R;
import com.bartovapps.popballoon.utils.PixelHelper;

/**
 * Created by motibartov on 09/04/2017.
 */

public class Balloon extends android.support.v7.widget.AppCompatImageView {

    int mDpHeight;
    int mDpWidth;
    BalloonTouchListener mListener;
    boolean mPopped;
    AnimatorSet mAnimatorSet;


    public Balloon(Context context) {
        super(context);
    }

    public Balloon(Context context, int color, int rawHeight) {
        super(context);
        mAnimatorSet = new AnimatorSet();
        mListener = (BalloonTouchListener) context;

        this.setImageResource(R.drawable.balloon);
        this.setColorFilter(color);

        int rawWidth = rawHeight/2;

        mDpHeight = PixelHelper.pixelToDp(rawHeight, context);
        mDpWidth = PixelHelper.pixelToDp(rawWidth, context);


        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(mDpWidth, mDpHeight);

        this.setLayoutParams(params);

    }

    public int getmDpHeight() {
        return mDpHeight;
    }

    public int getmDpWidth() {
        return mDpWidth;
    }

    public void releaseBalloon(int screenHeight, int duration){

        ObjectAnimator flyAnimator = ObjectAnimator.ofFloat(this, this.TRANSLATION_Y, (screenHeight), 0 - mDpHeight);
        flyAnimator.setDuration(duration);
        flyAnimator.setInterpolator(new LinearInterpolator());
        flyAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(!mPopped){
                    mListener.popBalloon(Balloon.this, false);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        ObjectAnimator rotatationAnim = ObjectAnimator.ofFloat(this, View.ROTATION, 0, 360);
        rotatationAnim.setInterpolator(new LinearInterpolator());
        rotatationAnim.setRepeatCount(Animation.INFINITE);
        rotatationAnim.setDuration(duration);
        this.startAnimation(rotateAnimation);

        mAnimatorSet.playTogether(flyAnimator);
        mAnimatorSet.setDuration(duration);
        mAnimatorSet.start();

        //mAnimator.start();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!mPopped && event.getAction() == MotionEvent.ACTION_DOWN){
            mListener.popBalloon(this, true);
            mPopped = true;

            if(mAnimatorSet != null && mAnimatorSet.isStarted()){
                mAnimatorSet.cancel();
            }

        }
        return super.onTouchEvent(event);
    }

    public void setPopped(boolean popped) {

        mPopped = popped;
        if(mAnimatorSet.isStarted()){
            mAnimatorSet.cancel();
            this.clearAnimation();
        }
    }


    public interface BalloonTouchListener{
        void popBalloon(Balloon balloon, boolean touched);
    }
}
