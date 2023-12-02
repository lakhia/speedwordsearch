package creationsahead.speedwordsearch.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.widget.LinearLayout;
import android.widget.TextView;
import creationsahead.speedwordsearch.R;
import creationsahead.speedwordsearch.TickerCallback;
import java.util.Locale;

/**
 * Top bar showing time
 */
public class ScoreBar extends LinearLayout implements TickerCallback {
    private final TextView timeWidget;

    public ScoreBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);

        ContextThemeWrapper newContext = new ContextThemeWrapper(getContext(), R.style.WordList);
        timeWidget = new TextView(newContext, null);
        addView(timeWidget);
    }

    @Override
    public void onTick(int tickCount) {
        int minutes = tickCount / 60;
        int seconds = tickCount % 60;
        timeWidget.setText(String.format(Locale.ENGLISH,
                                         "%02d:%02d", minutes, seconds));
    }
}
