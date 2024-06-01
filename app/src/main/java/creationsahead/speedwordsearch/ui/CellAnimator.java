package creationsahead.speedwordsearch.ui;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import java.util.ArrayList;

import static creationsahead.speedwordsearch.ui.GameApplication.ANIMATION_DURATION;

/**
 * Animates one or more cells
 */
public class CellAnimator implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {
    @NonNull private final ArrayList<TextView> arrayList = new ArrayList<>();
    private final int transitionColor;

    public CellAnimator(boolean correct) {
        if (correct) {
            // Bright green in AARRGGBB format
            transitionColor = 0xff38C838;
        } else {
            // Bright red in AARRGGBB format
            transitionColor = 0xfff82828;
        }
        ValueAnimator anim = ValueAnimator.ofFloat(0, 1, 1, 1, 0);
        anim.setDuration(ANIMATION_DURATION);
        anim.setInterpolator(new LinearInterpolator());
        anim.addUpdateListener(this);
        anim.addListener(this);
        anim.start();
    }

    @Override
    public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator) {
        // Dark grey in AARRGGBB format, text color copied from colors.xml
        int initColor = 0xff404040;
        float v = (float) valueAnimator.getAnimatedValue();
        int s = ColorUtils.blendARGB(initColor, transitionColor, v);
        for (TextView textView : arrayList) {
            textView.setTextColor(s);
        }
    }

    public void add(TextView textView) {
        arrayList.add(textView);
    }

    @Override
    public void onAnimationStart(Animator animator) {}

    @Override
    public void onAnimationCancel(Animator animator) {}

    @Override
    public void onAnimationRepeat(Animator animator) {}

    @Override
    public void onAnimationEnd(Animator animator) {
        arrayList.clear();
    }
}
