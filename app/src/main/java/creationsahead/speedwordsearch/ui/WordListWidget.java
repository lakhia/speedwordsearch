package creationsahead.speedwordsearch.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.widget.TextView;
import com.joanzapata.iconify.widget.IconTextView;
import creationsahead.speedwordsearch.Answer;
import creationsahead.speedwordsearch.AnswerCallback;
import creationsahead.speedwordsearch.Event;
import creationsahead.speedwordsearch.ProgressTracker;
import creationsahead.speedwordsearch.R;

/**
 * Widget that displays list of words
 */
public class WordListWidget extends com.nex3z.flowlayout.FlowLayout
    implements AnswerCallback {

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
        ProgressTracker.getInstance().game.visitAnswers(this);
        Answer.callback = this;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Answer.callback = null;
    }

    @Override
    public void onUpdate(@NonNull Answer answer, Event event) {
        TextView textView;
        switch (event) {
            case WORD_ADDED:
            case VISIT:
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
}
