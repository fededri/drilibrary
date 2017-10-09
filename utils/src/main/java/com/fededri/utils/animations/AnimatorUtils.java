package com.fededri.utils.animations;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.PathInterpolator;

import static com.fededri.utils.animations.SpringAnimationUtil.createSpringAnimation;

/**
 * Created by Federico Torres
 * drihtion@gmail.com
 */

public class AnimatorUtils {

    public static final float STIFFNESS_HIGH = 10_000f;
    /**
     * Stiffness constant for medium stiff spring. This is the default stiffness for spring force.
     */
    public static final float STIFFNESS_MEDIUM = 1500f;
    /**
     * Stiffness constant for a spring with low stiffness.
     */
    public static final float STIFFNESS_LOW = 200f;
    /**
     * Stiffness constant for a spring with very low stiffness.
     */
    public static final float STIFFNESS_VERY_LOW = 50f;

    /**
     * Damping ratio for a very bouncy spring. Note for under-damped springs
     * (i.e. damping ratio < 1), the lower the damping ratio, the more bouncy the spring.
     */
    public static final float DAMPING_RATIO_HIGH_BOUNCY = 0.2f;
    /**
     * Damping ratio for a medium bouncy spring. This is also the default damping ratio for spring
     * force. Note for under-damped springs (i.e. damping ratio < 1), the lower the damping ratio,
     * the more bouncy the spring.
     */
    public static final float DAMPING_RATIO_MEDIUM_BOUNCY = 0.5f;
    /**
     * Damping ratio for a spring with low bounciness. Note for under-damped springs
     * (i.e. damping ratio < 1), the lower the damping ratio, the higher the bounciness.
     */
    public static final float DAMPING_RATIO_LOW_BOUNCY = 0.75f;
    /**
     * Damping ratio for a spring with no bounciness. This damping ratio will create a critically
     * damped spring that returns to equilibrium within the shortest amount of time without
     * oscillating.
     */
    public static final float DAMPING_RATIO_NO_BOUNCY = 1f;

    public static void animAlpha(int duration, View target, float finalValue) {
        ObjectAnimator animAlphaLogo = ObjectAnimator.ofFloat(target, "alpha", finalValue);
        animAlphaLogo.setDuration(duration);
        animAlphaLogo.start();
    }


    /**
     * Scale animation using Spring physics
     * @param target: view to animate
     * @param finalScale: final scale when animation is finished, if this is the same scale than the current scale nothing will happen
     */
    public static void animScaleSpring(View target, float finalScale) {
        SpringAnimation scaleX = createSpringAnimation(target, SpringAnimation.SCALE_X, finalScale, SpringForce.STIFFNESS_LOW, SpringForce.DAMPING_RATIO_HIGH_BOUNCY);
        SpringAnimation scaleY = createSpringAnimation(target, SpringAnimation.SCALE_Y, finalScale, SpringForce.STIFFNESS_LOW, SpringForce.DAMPING_RATIO_HIGH_BOUNCY);

        scaleX.start();
        scaleY.start();
    }


    /**
     * Scale animation with a specfific duration using ObjectAnimator, withouth spring physics
     */
    public static void animScale(View target, float initialScale, float finalScale, int duration, TimeInterpolator interpolator) {
        ObjectAnimator animX = ObjectAnimator.ofFloat(target, "scaleX", initialScale, finalScale);
        ObjectAnimator animY = ObjectAnimator.ofFloat(target, "scaleY", initialScale, finalScale);

       animX.setInterpolator(interpolator);

        animX.setDuration(duration);
        animY.setDuration(duration);

        animX.start();
        animY.start();
    }


    /**
     * @param offset:       value of the displacement  the view will ve moved across the Y axis
     * @param finalPosition:  position where the view will stay when animation ends
     * @param stiffness:     stiffness of spring, more stiffness  less likely to move
     */
    public static void animPositionSpringY(View target, float offset, float finalPosition, float stiffness, float dampingRatio) {
        SpringAnimation animY = createSpringAnimation(target, SpringAnimation.Y, finalPosition, stiffness, dampingRatio);

        float posY = target.getY() + offset;
        target.setY(posY);
        animY.start();
    }

}
