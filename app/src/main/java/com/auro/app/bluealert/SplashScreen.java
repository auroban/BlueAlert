package com.auro.app.bluealert;

import android.animation.Animator;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created on 3/14/2016.
 * @author Arijit Banerjee
 * @since 1.0
 */
public class SplashScreen extends AppCompatActivity {

    private static final int ANIM_DURATION = 1500;
    private static final int SPLASH_ICON_ANIM_DURATION = 500;
    private static final int MEDIA_DURATION = 4000;
    private static final float SLIDE_UP_Y_BY = -0.15f;
    private static final float SLIDE_DOWN_Y_BY = 0.15f;

    private Handler mHandler;
    private Runnable mRunnableChangeActivity, mRunnableShowIcon;
    private TextView mSplashHeader;
    private LinearLayout mSplashFooter;
    private ImageView mSplashIcon;
    private MediaPlayer mMediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splashscreen);

        init();
        slideDownAnim();
        slideUpAnim();

        mHandler.postDelayed(mRunnableShowIcon,ANIM_DURATION);

        int waitTime = 7000;
        mHandler.postDelayed(mRunnableChangeActivity, waitTime);
    }

    private void init() {

        mSplashHeader = (TextView) findViewById(R.id.splash_header);
        mSplashFooter = (LinearLayout) findViewById(R.id.splash_footer);
        mSplashIcon = (ImageView) findViewById(R.id.splash_icon);

        if (mMediaPlayer == null)
            mMediaPlayer = MediaPlayer
                    .create(SplashScreen.this,R.raw.long_chime_sound);


        if  (mHandler == null) {
            mHandler = new Handler();
        }

        if (mRunnableChangeActivity == null) {
            mRunnableChangeActivity = new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            };
        }

        if (mRunnableShowIcon == null) {
            mRunnableShowIcon = new Runnable() {
                @Override
                public void run() {
                    popIcon();

                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(MEDIA_DURATION);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            mMediaPlayer.stop();
                        }
                    });
                    thread.start();
                }
            };
        }
    }

    private void slideDownAnim() {

        final Animation slideDown =
                new TranslateAnimation(
                        Animation.ABSOLUTE,0.0f,Animation.RELATIVE_TO_PARENT,0.0f,
                        Animation.ABSOLUTE,0.0f,Animation.RELATIVE_TO_PARENT,SLIDE_DOWN_Y_BY);
        slideDown.setDuration(ANIM_DURATION);
        slideDown.setFillAfter(true);
        slideDown.setFillEnabled(true);

        mSplashHeader.setAnimation(slideDown);

    }

    private void slideUpAnim() {

        final Animation slideUp =
                new TranslateAnimation(
                        Animation.ABSOLUTE,0.0f,Animation.RELATIVE_TO_PARENT,0.0f,
                        Animation.ABSOLUTE,0.0f,Animation.RELATIVE_TO_PARENT,SLIDE_UP_Y_BY);
        slideUp.setDuration(ANIM_DURATION);
        slideUp.setFillAfter(true);
        slideUp.setFillEnabled(true);

        mSplashFooter.setAnimation(slideUp);

    }

    private void popIcon() {

        mSplashIcon.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .alpha(1.0f)
                .setDuration(SPLASH_ICON_ANIM_DURATION)
                .setInterpolator(new OvershootInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                        mMediaPlayer.start();

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {


                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });

    }
}
