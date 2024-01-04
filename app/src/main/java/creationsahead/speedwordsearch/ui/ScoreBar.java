package creationsahead.speedwordsearch.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.widget.LinearLayout;
import android.widget.TextView;
import creationsahead.speedwordsearch.Answer;
import creationsahead.speedwordsearch.Event;
import creationsahead.speedwordsearch.ProgressTracker;
import creationsahead.speedwordsearch.R;
import creationsahead.speedwordsearch.TickerCallback;
import java.util.Locale;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Top bar showing time
 */
public class ScoreBar extends LinearLayout implements TickerCallback {
    @NonNull private final TextView timeWidget;
    @NonNull private final TextView scoreWidget;

    public ScoreBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        EventBus.getDefault().register(this);

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
        int minutes = tickCount / 60;
        int seconds = tickCount % 60;
        timeWidget.setText(String.format(Locale.ENGLISH,
                                         "%02d:%02d", minutes, seconds));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateScore(Answer answer) {
        if (answer == null || answer.event == Event.SCORE_AWARDED) {
            scoreWidget.setText(String.format(Locale.ENGLISH,
                                              "%03d",
                                              ProgressTracker.getInstance().getCurrentScore()));
        }
    }
}
