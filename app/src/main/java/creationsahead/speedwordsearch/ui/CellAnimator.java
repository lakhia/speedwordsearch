package creationsahead.speedwordsearch.ui;

import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static creationsahead.speedwordsearch.ui.GameApplication.ANIMATION_DURATION;


/**
 * Animate one cell
 */
public class CellAnimator implements ValueAnimator.AnimatorUpdateListener {
    @Nullable
    private TextView textView;
    private final String newValue;

    public CellAnimator(@NonNull TextView textView, String new_value) {
        this.textView = textView;
        newValue = new_value;
        ValueAnimator anim = ValueAnimator.ofFloat(1, 0, 1);
        anim.setDuration(ANIMATION_DURATION / 2);
        anim.setInterpolator(new LinearInterpolator());
        anim.addUpdateListener(this);
        anim.start();
    }

    @Override
    public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator) {
        float v = (float) valueAnimator.getAnimatedValue();
        if (textView != null) {
            textView.setTextScaleX(v);
        }
        if (valueAnimator.getAnimatedFraction() >= 0.5f) {
            textView.setText(newValue);
            if (valueAnimator.getAnimatedFraction() >= 1.0) {
                // Clear textview ref to prevent memory leak
                textView = null;
            }
        }
    }
}
