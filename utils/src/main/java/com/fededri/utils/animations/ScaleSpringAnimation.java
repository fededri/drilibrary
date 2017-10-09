package com.fededri.utils.animations;

import android.support.animation.DynamicAnimation;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import static com.fededri.utils.animations.SpringAnimationUtil.createSpringAnimation;


/**
 * Created by Federico Torres on 6/10/2017.
 */

public class ScaleSpringAnimation {

    private static float INITIAL_SCALE = 1f;
    private float scaleFactor = 1f;

    private SpringAnimation scaleXAnimation;
    private SpringAnimation scaleYAnimation;
    private ScaleGestureDetector scaleGestureDetector;

    private View animateView;
    private TextView infoView;



    private boolean enabledPinchToZoom = false;

    public ScaleSpringAnimation(View animateView) {
        this.infoView = infoView;
        this.animateView = animateView;
        // create scaleX and scaleY animations
        scaleXAnimation = createSpringAnimation(animateView, SpringAnimation.SCALE_X,
                INITIAL_SCALE, SpringForce.STIFFNESS_MEDIUM, SpringForce.DAMPING_RATIO_HIGH_BOUNCY);
        scaleYAnimation = createSpringAnimation(animateView, SpringAnimation.SCALE_Y,
                INITIAL_SCALE, SpringForce.STIFFNESS_MEDIUM, SpringForce.DAMPING_RATIO_HIGH_BOUNCY);

        enabledPinchToZoom = true;
        setupPinchToZoom();
        this.animateView.setOnTouchListener(touchListener);
        scaleXAnimation.addUpdateListener(updateListener);
    }




    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (!enabledPinchToZoom) return false;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                scaleXAnimation.start();
                scaleYAnimation.start();
            } else {
                // cancel animations so we can grab the view during previous animation
                scaleXAnimation.cancel();
                scaleYAnimation.cancel();
                // pass touch event to ScaleGestureDetector
                scaleGestureDetector.onTouchEvent(event);
            }
            return true;
        }
    };

    public boolean isEnabledPinchToZoom() {
        return enabledPinchToZoom;
    }

    public void setEnabledPinchToZoom(boolean enabledPinchToZoom) {
        this.enabledPinchToZoom = enabledPinchToZoom;
    }

    private void setupPinchToZoom() {
        scaleGestureDetector = new ScaleGestureDetector(animateView.getContext(),
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    @Override
                    public boolean onScale(ScaleGestureDetector detector) {
                        scaleFactor *= detector.getScaleFactor();
                        animateView.setScaleX(animateView.getScaleX() * scaleFactor);
                        animateView.setScaleY(animateView.getScaleY() * scaleFactor);
                        return true;
                    }
                }
        );
    }

    private DynamicAnimation.OnAnimationUpdateListener updateListener = new DynamicAnimation.OnAnimationUpdateListener() {
        @Override
        public void onAnimationUpdate(DynamicAnimation animation, float value, float velocity) {
        }
    };


}
