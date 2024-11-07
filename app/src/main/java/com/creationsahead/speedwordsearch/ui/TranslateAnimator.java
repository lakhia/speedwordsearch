package com.creationsahead.speedwordsearch.ui;

import android.animation.ValueAnimator;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import static com.creationsahead.speedwordsearch.ui.GameApplication.ANIMATION_DURATION;

/**
 * Animate one cell
 */
public class TranslateAnimator implements ValueAnimator.AnimatorUpdateListener {
    @NonNull
    private final TextView textView;

    public TranslateAnimator(@NonNull TextView textView, float offsetX, float speedX) {
        this.textView = textView;
        ValueAnimator anim = ValueAnimator.ofFloat(offsetX, 0);

        anim.setDuration((long) (ANIMATION_DURATION * 5 / speedX));
        anim.setInterpolator(new FastOutSlowInInterpolator());
        anim.addUpdateListener(this);
        anim.start();
    }

    @Override
    public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator) {
        float v = (float) valueAnimator.getAnimatedValue();
        textView.setTranslationX(v);
    }
}
