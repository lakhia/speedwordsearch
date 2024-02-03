package creationsahead.speedwordsearch.ui;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Animates one or more cells
 */
public class CellAnimator implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {
    private boolean correct;
    private ArrayList<TextView> arrayList = new ArrayList<>();

    public CellAnimator(boolean correct) {
        this.correct = correct;
        ValueAnimator anim = ValueAnimator.ofFloat(0, 1, 0);
        anim.setDuration(1000);
        anim.setInterpolator(new LinearInterpolator());
        anim.addUpdateListener(this);
        anim.addListener(this);
        anim.start();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        int color;
        float v = (float) valueAnimator.getAnimatedValue();
        float s = 32 * v;
        int a = (int) (255 * v);
        if (correct) {
            color = Color.argb(a, 50, 200, 50);
        } else {
            color = Color.argb(a, 250, 112, 112);
        }
        for (TextView textView : arrayList) {
            textView.setShadowLayer(s, 4, 4, color);
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
