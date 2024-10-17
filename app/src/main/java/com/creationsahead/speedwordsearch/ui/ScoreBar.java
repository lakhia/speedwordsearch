package com.creationsahead.speedwordsearch.ui;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.creationsahead.speedwordsearch.Guess;
import com.creationsahead.speedwordsearch.ProgressTracker;
import com.creationsahead.speedwordsearch.R;
import com.creationsahead.speedwordsearch.TickerCallback;
import com.creationsahead.speedwordsearch.mod.Level;
import com.creationsahead.speedwordsearch.utils.Utils;
import java.util.Locale;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.creationsahead.speedwordsearch.mod.Level.TIME_LEFT;
import static com.creationsahead.speedwordsearch.ui.GameApplication.ANIMATION_DURATION;

/**
 * Top bar showing time
 */
public class ScoreBar extends LinearLayout implements TickerCallback, ValueAnimator.AnimatorUpdateListener {
    @NonNull private final TextView timeWidget;
    @NonNull private final TextView scoreWidget;
    private ValueAnimator anim;
    private int deltaScore, prevScore;
    private final Level level;

    public ScoreBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
        inflate(context, R.layout.score_bar, this);

        EventBus.getDefault().register(this);

        TextView levelNameWidget = findViewById(R.id.levelName);
        timeWidget = findViewById(R.id.time);
        scoreWidget = findViewById(R.id.score);
        level = ProgressTracker.getInstance().currentLevel;
        levelNameWidget.setText(level.name);
        updateScoreWidget(level.score);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onTick(int timeLeft) {
        level.timeUsed = TIME_LEFT - timeLeft;
        timeWidget.setText(Utils.formatTime(timeLeft));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateScore(@NonNull Guess guess) {
        if (anim != null) {
            anim.cancel();
        }
        if (guess.answer == null) {
            return;
        }
        deltaScore = guess.answer.score;
        prevScore = level.score;
        level.score += deltaScore;

        anim = ValueAnimator.ofFloat(1, 4.5f, 1);
        anim.setDuration(ANIMATION_DURATION);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addUpdateListener(this);
        if (guess.last) {
            // Last word guessed, trigger event after animation is finished
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {}

                @Override
                public void onAnimationEnd(Animator animator) {
                    level.won = true;
                    EventBus.getDefault().post(level);
                }

                @Override
                public void onAnimationCancel(Animator animator) {}

                @Override
                public void onAnimationRepeat(Animator animator) {}
            });
        }
        anim.start();
    }

    private void updateScoreWidget(int score) {
        scoreWidget.setText(String.format(Locale.ENGLISH, "%03d", score));
    }

    @Override
    public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator) {
        float fraction = (float) valueAnimator.getAnimatedValue();
        scoreWidget.setScaleX(fraction);
        scoreWidget.setScaleY(fraction);
        int score = (int) (prevScore + valueAnimator.getAnimatedFraction() * deltaScore);
        updateScoreWidget(score);
    }
}
