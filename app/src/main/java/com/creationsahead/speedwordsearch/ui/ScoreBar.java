package com.creationsahead.speedwordsearch.ui;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
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
public class ScoreBar extends LinearLayout implements TickerCallback {
    @NonNull private final TextView timeWidget;
    @NonNull private final TextView scoreWidget;
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
        if (timeLeft < 30) {
            NumberAnimator timeAnim = new NumberAnimator(timeWidget, 250, 1.75f, timeLeft) {
                @Override
                public void setWidget(int n) {
                    timeWidget.setText(Utils.formatTime(n));
                }
            };
            timeAnim.start(timeLeft);
        } else {
            timeWidget.setText(Utils.formatTime(timeLeft));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateScore(@NonNull Guess guess) {
        if (guess.answer == null) {
            return;
        }
        int prevScore = level.score;
        int deltaScore = guess.answer.score;
        level.score += deltaScore;

        NumberAnimator anim = new NumberAnimator(scoreWidget, ANIMATION_DURATION, 4.5f, prevScore) {
            @Override
            public void setWidget(int n) {
                scoreWidget.setText(String.format(Locale.ENGLISH, "%03d", n));
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                if (guess.last) {
                    level.won = true;
                }
            }
        };
        anim.start(level.score);
    }

    private void updateScoreWidget(int score) {
        scoreWidget.setText(String.format(Locale.ENGLISH, "%03d", score));
    }
}
