package creationsahead.speedwordsearch.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.widget.LinearLayout;
import android.widget.TextView;
import creationsahead.speedwordsearch.ProgressTracker;
import creationsahead.speedwordsearch.R;
import creationsahead.speedwordsearch.ScoreCallback;
import creationsahead.speedwordsearch.TickerCallback;
import java.util.Locale;

/**
 * Top bar showing time
 */
public class ScoreBar extends LinearLayout implements TickerCallback, ScoreCallback {
    @NonNull private final TextView timeWidget;
    @NonNull private final TextView scoreWidget;

    public ScoreBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);

        ContextThemeWrapper newContext = new ContextThemeWrapper(getContext(), R.style.WordList);
        timeWidget = new TextView(newContext, null);
        addView(timeWidget);
        scoreWidget = new TextView(newContext, null);
        addView(scoreWidget);
        if (isInEditMode()) {
            timeWidget.setText("4:55");
        }
        updateScore(ProgressTracker.getInstance().getCurrentScore());
    }

    @Override
    public void onTick(int tickCount) {
        int minutes = tickCount / 60;
        int seconds = tickCount % 60;
        timeWidget.setText(String.format(Locale.ENGLISH,
                                         "%02d:%02d", minutes, seconds));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ProgressTracker.getInstance().callback = this;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ProgressTracker.getInstance().callback = null;
    }

    @Override
    public void updateScore(int score) {
        scoreWidget.setText(String.format(Locale.ENGLISH,
                                          "%03d", score));
    }
}
