package creationsahead.speedwordsearch.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.joanzapata.iconify.widget.IconTextView;
import creationsahead.speedwordsearch.Answer;
import creationsahead.speedwordsearch.Event;
import creationsahead.speedwordsearch.ProgressTracker;
import creationsahead.speedwordsearch.R;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static android.util.TypedValue.COMPLEX_UNIT_PX;

/**
 * Widget that displays list of words
 */
public class WordListWidget extends com.nex3z.flowlayout.FlowLayout {

    public WordListWidget(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setBackgroundResource(R.color.black_overlay);
        setChildSpacing(5);
        setChildSpacingForLastRow(5);
        setRowSpacing(5);
        setPadding(5, 5, 5, 5);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
        ProgressTracker.getInstance().game.visitAnswers();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdate(@NonNull Answer answer) {
        TextView textView;
        switch (answer.event) {
            case WORD_ADDED:
                ContextThemeWrapper newContext = new ContextThemeWrapper(getContext(), R.style.WordList);
                textView = new IconTextView(newContext, null);
                textView.setText(answer.getDisplay());
                addView(textView);
                answer.tag = textView;
                break;
            case SCORE_AWARDED:
                textView = (TextView) answer.tag;
                removeView(textView);
                break;
            case BONUS_ADDED:
                textView = (TextView) answer.tag;
                textView.setText(answer.getDisplay());
                break;
        }
    }

    // TODO: Implicit dependency that font size event comes after textviews have been created
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFontUpdate(@NonNull Event event) {
        if (event == Event.FONT_SIZE) {
            float fontSize = event.fontSize * 0.5f;
            for (int i = 0; i < getChildCount(); i++) {
                View view = getChildAt(i);
                if (view instanceof TextView) {
                    TextView textView = (TextView) view;
                    textView.setTextSize(COMPLEX_UNIT_PX, fontSize);
                }
            }
        }
    }
}
