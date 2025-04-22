package com.creationsahead.speedwordsearch.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.creationsahead.speedwordsearch.Answer;
import com.creationsahead.speedwordsearch.Guess;
import com.creationsahead.speedwordsearch.ProgressTracker;
import com.creationsahead.speedwordsearch.R;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;

import static android.util.TypedValue.COMPLEX_UNIT_PX;
import static com.creationsahead.speedwordsearch.ui.GameApplication.ANIMATION_DURATION;

/**
 * Widget that displays list of words
 */
public class WordListWidget extends com.nex3z.flowlayout.FlowLayout {

    private Typeface typeface;

    public WordListWidget(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setChildSpacing(5);
        setChildSpacingForLastRow(5);
        setRowSpacing(5);
        setPadding(5, 5, 5, 5);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        GameApplication app = (GameApplication) getContext().getApplicationContext();
        typeface = app.loader.wordListTypeface;
        EventBus.getDefault().register(this);
        ArrayList<Answer> answers = ProgressTracker.getInstance().getGame().getAnswers();
        for (Answer answer : answers) {
            onNewAnswer(answer);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewAnswer(@NonNull Answer answer) {
        TextView textView;
        ContextThemeWrapper newContext = new ContextThemeWrapper(getContext(), R.style.WordList);
        textView = new TextView(newContext, null);
        textView.setTextSize(COMPLEX_UNIT_PX, ProgressTracker.getInstance().normalizedFontSize / 12.0f);
        textView.setText(answer.getDisplay());
        textView.setTypeface(typeface);

        addView(textView);
        answer.tag = textView;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGuess(@NonNull Guess guess) {
        if (guess.answer == null) {
            return;
        }
        TextView textView = (TextView) guess.answer.tag;
        if (textView == null) {
            return;
        }
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        textView.startAnimation(fadeOut);
        fadeOut.setDuration(ANIMATION_DURATION);
        fadeOut.setFillAfter(true);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                removeView(textView);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }
}
