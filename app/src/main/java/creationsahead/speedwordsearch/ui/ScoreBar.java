package creationsahead.speedwordsearch.ui;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import androidx.annotation.NonNull;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import creationsahead.speedwordsearch.Event;
import creationsahead.speedwordsearch.ProgressTracker;
import creationsahead.speedwordsearch.R;
import creationsahead.speedwordsearch.TickerCallback;
import creationsahead.speedwordsearch.mod.Level;
import creationsahead.speedwordsearch.utils.Utils;
import java.util.Locale;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static creationsahead.speedwordsearch.ui.GameApplication.ANIMATION_DURATION;

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
    private int currentTick;

    public ScoreBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);

        EventBus.getDefault().register(this);

        Level level = ProgressTracker.getInstance().getCurrentLevel();
        currentScore = level.score;
        prevScore = currentScore;

        inflate(context, R.layout.score_bar, this);

        TextView levelNameWidget = findViewById(R.id.levelName);
        timeWidget = findViewById(R.id.time);
        scoreWidget = findViewById(R.id.score);
        levelNameWidget.setText(level.name);

        updateScoreWidget();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onTick(int timeLeft) {
        currentTick = timeLeft;
        timeWidget.setText(Utils.formatTime(timeLeft));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateScore(@NonNull Event event) {
        if (event == Event.SCORE_AWARDED) {
            if (anim != null) {
                anim.cancel();
            }
            prevScore = currentScore;
            deltaScore = ProgressTracker.getInstance().getCurrentScore() - prevScore;

            anim = ValueAnimator.ofFloat(1, 1.75f, 1);
            anim.setDuration(ANIMATION_DURATION);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.addUpdateListener(this);
            if (event.lastWordGuessed) {
                // Last word guessed, trigger event after animation is finished
                anim.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {}

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        Event event = Event.LEVEL_WON;
                        event.timeLeft = currentTick;
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
    public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator) {
        float fraction = (float) valueAnimator.getAnimatedValue();
        scoreWidget.setScaleX(fraction);
        scoreWidget.setScaleY(fraction);
        currentScore = (int) (prevScore + valueAnimator.getAnimatedFraction() * deltaScore);
        updateScoreWidget();
    }
}
