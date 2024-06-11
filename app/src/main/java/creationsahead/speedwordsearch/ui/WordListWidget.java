package creationsahead.speedwordsearch.ui;

import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.widget.TextView;
import creationsahead.speedwordsearch.Answer;
import creationsahead.speedwordsearch.ProgressTracker;
import creationsahead.speedwordsearch.R;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static android.util.TypedValue.COMPLEX_UNIT_PX;
import static creationsahead.speedwordsearch.ui.GameApplication.ANIMATION_DURATION;

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
            case ANSWER_ADDED:
                ContextThemeWrapper newContext = new ContextThemeWrapper(getContext(), R.style.WordList);
                textView = new TextView(newContext, null);
                // TODO: Using fixed font-size for now
                textView.setTextSize(COMPLEX_UNIT_PX, ProgressTracker.getInstance().normalizedFontSize/12.0f);

                textView.setText(answer.getDisplay());
                Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.artifika);
                textView.setTypeface(typeface);

                addView(textView);
                answer.tag = textView;
                break;
            case ANSWER_CORRECT:
                textView = (TextView) answer.tag;
                if (textView != null) {
                    Transition transition = new Fade();
                    transition.setDuration(ANIMATION_DURATION);
                    transition.addListener(new Transition.TransitionListener() {
                        @Override
                        public void onTransitionStart(Transition transition) {}

                        @Override
                        public void onTransitionEnd(Transition transition) {
                            textView.setVisibility(GONE);
                        }

                        @Override
                        public void onTransitionCancel(Transition transition) {}

                        @Override
                        public void onTransitionPause(Transition transition) {}

                        @Override
                        public void onTransitionResume(Transition transition) {}
                    });
                    TransitionManager.beginDelayedTransition(this, transition);
                    answer.tag = null;
                }
                break;
        }
    }
}
