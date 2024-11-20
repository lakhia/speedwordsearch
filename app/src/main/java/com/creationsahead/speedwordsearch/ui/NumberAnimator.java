package com.creationsahead.speedwordsearch.ui;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import androidx.annotation.NonNull;
import java.util.Locale;

public class NumberAnimator implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {
    @NonNull
    private final ValueAnimator anim;
    private int delta;
    private int mCurrent;
    private int mPrev;
    @NonNull
    private final TextView mWidget;

    public NumberAnimator(@NonNull TextView textView, int duration, float scale, int currentValue) {
        mPrev = currentValue;
        mCurrent = currentValue;
        delta = 0;
        mWidget = textView;
        anim = ValueAnimator.ofFloat(1.0f, scale, 1.0f);
        anim.setDuration(duration);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addUpdateListener(this);
        anim.addListener(this);
    }

    @NonNull
    protected String format(int n) {
        return String.format(Locale.ENGLISH, "%03d", n);
    }

    public void onAnimationCancel(@NonNull Animator animator) {}

    public void onAnimationEnd(@NonNull Animator animator) {}

    public void onAnimationRepeat(@NonNull Animator animator) {}

    public void onAnimationStart(@NonNull Animator animator) {}

    public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator) {
        float fraction = (float) valueAnimator.getAnimatedValue();
        mWidget.setScaleX(fraction);
        mWidget.setScaleY(fraction);
        mCurrent = (int)(mPrev + valueAnimator.getAnimatedFraction() * delta);
        mWidget.setText(format(mCurrent));
    }

    public void start(int newValue) {
        mPrev = mCurrent;
        delta = newValue - mPrev;
        anim.start();
    }
}

