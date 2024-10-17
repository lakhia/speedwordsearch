package com.creationsahead.speedwordsearch.ui;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import androidx.annotation.NonNull;
import static com.creationsahead.speedwordsearch.ui.GameApplication.ANIMATION_DURATION;

/**
 * Animates list of levels
 */
public class ListAnimator implements  ValueAnimator.AnimatorUpdateListener {
    private final ViewGroup parent;
    private final View child;

    public ListAnimator(ViewGroup parent, View child, boolean forward,
                        Animator.AnimatorListener listener) {
        this.parent = parent;
        this.child = child;
        ValueAnimator anim;
        if (forward) {
            anim = ValueAnimator.ofFloat(1f, 0f);
        } else {
            anim = ValueAnimator.ofFloat(0f, 0.8f, 1f);
        }
        anim.setDuration(ANIMATION_DURATION/2);
        anim.setInterpolator(new LinearInterpolator());
        anim.addUpdateListener(this);
        if (listener != null) {
            anim.addListener(listener);
        }
        if (!forward) {
            anim.setStartDelay(ANIMATION_DURATION/3);
        }
        anim.start();
    }

    @Override
    public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator) {
        float fraction = (float) valueAnimator.getAnimatedValue();
        for (int itemPos = parent.getChildCount() - 1; itemPos >= 0; itemPos--) {
            View view = parent.getChildAt(itemPos);
            if (view != child) {
                view.setAlpha(fraction);
            }
        }
    }
}
