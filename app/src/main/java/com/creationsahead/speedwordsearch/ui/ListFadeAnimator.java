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
public abstract class ListFadeAnimator implements ValueAnimator.AnimatorUpdateListener,
        Animator.AnimatorListener {
    private final ViewGroup parent;
    private final View child;

    public ListFadeAnimator(@NonNull View child, boolean fadeOut) {
        this.child = child;
        parent = (ViewGroup) child.getParent();
        ValueAnimator anim;
        if (fadeOut) {
            anim = ValueAnimator.ofFloat(1f, 0f);
        } else {
            anim = ValueAnimator.ofFloat(0f, 0.8f, 1f);
        }
        anim.setDuration(ANIMATION_DURATION / 2);
        anim.setInterpolator(new LinearInterpolator());
        anim.addUpdateListener(this);
        anim.addListener(this);
        if (!fadeOut) {
            anim.setStartDelay(ANIMATION_DURATION / 2);
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

    @Override
    public void onAnimationCancel(@NonNull Animator animator) {}

    @Override
    public void onAnimationRepeat(@NonNull Animator animator) {}
}
