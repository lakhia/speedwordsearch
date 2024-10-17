package com.creationsahead.speedwordsearch.ui;

import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import java.util.ArrayList;
import com.creationsahead.speedwordsearch.Guess;
import static com.creationsahead.speedwordsearch.ui.GameApplication.ANIMATION_DURATION;

/**
 * Animates one or more cells
 */
public class GuessAnimator implements ValueAnimator.AnimatorUpdateListener {
    @NonNull private final ArrayList<Object> arrayList;
    private final int transitionColor;

    public GuessAnimator(@NonNull Guess guess) {
        if (guess.answer != null) {
            // Bright green in AARRGGBB format
            transitionColor = 0xff38C838;
        } else {
            // Bright red in AARRGGBB format
            transitionColor = 0xfff82828;
        }
        arrayList = guess.tags;
        ValueAnimator anim = ValueAnimator.ofFloat(0, 1, 1, 1, 0);
        anim.setDuration(ANIMATION_DURATION);
        anim.setInterpolator(new LinearInterpolator());
        anim.addUpdateListener(this);
        anim.start();
    }

    @Override
    public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator) {
        // Dark grey in AARRGGBB format, text color copied from colors.xml
        int initColor = 0xff404040;
        float v = (float) valueAnimator.getAnimatedValue();
        int s = ColorUtils.blendARGB(initColor, transitionColor, v);
        for (Object obj : arrayList) {
            TextView textView = (TextView) obj;
            textView.setTextColor(s);
        }
        if (valueAnimator.getAnimatedFraction() >= 1.0) {
            // Clear list to prevent memory leaks
            arrayList.clear();
        }
    }
}
