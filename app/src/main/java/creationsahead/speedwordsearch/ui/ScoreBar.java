package creationsahead.speedwordsearch.ui;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import creationsahead.speedwordsearch.Event;
import creationsahead.speedwordsearch.ProgressTracker;
import creationsahead.speedwordsearch.R;
import creationsahead.speedwordsearch.TickerCallback;
import creationsahead.speedwordsearch.utils.Utils;
import java.util.Locale;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Top bar showing time
 */
public class ScoreBar extends LinearLayout implements TickerCallback, ValueAnimator.AnimatorUpdateListener {
    @NonNull private final TextView timeWidget;
    @NonNull private final TextView scoreWidget;
    private ValueAnimator anim;
    private int currentScore;
    private int prevScore;
    private int deltaScore;

    public ScoreBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        EventBus.getDefault().register(this);
        currentScore = ProgressTracker.getInstance().getCurrentScore();
        prevScore = currentScore;

        setOrientation(HORIZONTAL);

        ContextThemeWrapper newContext = new ContextThemeWrapper(getContext(), R.style.WordList);
        timeWidget = new TextView(newContext, null);
        addView(timeWidget);
        scoreWidget = new TextView(newContext, null);
        addView(scoreWidget);
        if (isInEditMode()) {
            timeWidget.setText("4:55");
        }
        updateScore(null);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onTick(int tickCount) {
        timeWidget.setText(Utils.formatTime(tickCount));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateScore(Event event) {
        if (event == null) {
            updateScoreWidget();
        } else if (event == Event.SCORE_AWARDED) {
            if (anim != null) {
                anim.cancel();
            }
            prevScore = currentScore;
            deltaScore = ProgressTracker.getInstance().getCurrentScore() - prevScore;

            anim = ValueAnimator.ofFloat(1, 1.75f, 1);
            anim.setDuration(1000);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.addUpdateListener(this);
            if (event.lastWordGuessed) {
                // Last word guessed, trigger event after animation is finished
                anim.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {}

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        EventBus.getDefault().post(Event.LEVEL_WON);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {}

                    @Override
                    public void onAnimationRepeat(Animator animator) {}
                });
            }
            anim.start();
        }
    }

    private void updateScoreWidget() {
        scoreWidget.setText(String.format(Locale.ENGLISH, "%03d", currentScore));
    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        float fraction = (float) valueAnimator.getAnimatedValue();
        scoreWidget.setScaleX(fraction);
        scoreWidget.setScaleY(fraction);
        currentScore = (int) (prevScore + valueAnimator.getAnimatedFraction() * deltaScore);
        updateScoreWidget();
    }
}
