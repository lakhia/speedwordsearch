package creationsahead.speedwordsearch.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TextView;
import creationsahead.speedwordsearch.Answer;
import creationsahead.speedwordsearch.AnswerCallback;
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
        setChildSpacing(SPACING_AUTO);
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
    public void onUpdate(@NonNull Answer answer) {
        if (answer.tag == null) {
            ContextThemeWrapper newContext = new ContextThemeWrapper(getContext(), R.style.WordList);
            TextView textView = new TextView(newContext, null);
            textView.setText(answer.word);
            addView(textView);
            answer.tag = textView;
        } else {
            View view = (View) answer.tag;
            removeView(view);
        }
    }

}
