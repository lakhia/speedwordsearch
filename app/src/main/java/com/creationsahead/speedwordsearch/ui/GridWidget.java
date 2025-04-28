package com.creationsahead.speedwordsearch.ui;

import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.transition.Explode;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.creationsahead.speedwordsearch.Cell;
import com.creationsahead.speedwordsearch.Game;
import com.creationsahead.speedwordsearch.Guess;
import com.creationsahead.speedwordsearch.ProgressTracker;
import com.creationsahead.speedwordsearch.R;
import java.util.Random;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static android.util.TypedValue.COMPLEX_UNIT_PX;
import static com.creationsahead.speedwordsearch.ui.GameApplication.ANIMATION_DURATION;

/**
 * Widget that displays grid
 */
public class GridWidget extends TableLayout {
    @NonNull private final Center center;
    @NonNull private final ProgressTracker tracker;
    private TouchHandler handler;

    public GridWidget(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        EventBus.getDefault().register(this);

        GameApplication app = (GameApplication) getContext().getApplicationContext();
        Typeface typeface = app.loader.letterTypeface;
        tracker = ProgressTracker.getInstance();
        Game game = tracker.getGame();

        for (int j = 0; j < tracker.config.sizeY; j++) {
            TableRow row = new TableRow(context);
            addView(row);

            for (int i = 0; i < tracker.config.sizeX; i++) {
                ContextThemeWrapper newContext = new ContextThemeWrapper(context, R.style.PuzzleLetter);
                TextView textView = new TextView(newContext, null);
                Cell cell = game.getCell(i, j);
                textView.setText(cell.toString());
                textView.setTypeface(typeface);
                textView.setTag(R.string.column, i);
                textView.setTag(R.string.row, j);
                row.addView(textView);
                cell.tag = textView;
            }
        }

        center = new Center(tracker.displayRect);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        View view = this;
        do {
            view = (View) view.getParent();
            handler = view.findViewById(R.id.touchOverlay);
        } while (handler == null);
        for (int i = 0; i < getChildCount(); i++) {
            ViewGroup row = (ViewGroup) getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                View cell = row.getChildAt(j);
                cell.setOnTouchListener(handler);
            }
        }
        handler.setActivity((GameActivity) getContext());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);
        final int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        final int widgetSize = Math.min(measuredHeight, measuredWidth);
        final boolean isLandscape = measuredHeight < measuredWidth;
        final int cellSizeX = widgetSize / tracker.config.sizeX;
        final int cellSizeY = widgetSize / tracker.config.sizeY;
        setMeasuredDimension(widgetSize, widgetSize);
        handler.setCellSize(cellSizeX, cellSizeY);

        final int childCount = getChildCount();
        float fontSize = tracker.normalizedFontSize / childCount;
        if (isLandscape) {
            // In landscape mode, min(height,width) is smaller than in portrait mode
            fontSize *= 0.92f;
        }
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            TableRow row = (TableRow) child;
            row.measure(measuredWidth, cellSizeY);
            row.setMinimumWidth(measuredWidth);
            row.setMinimumHeight(cellSizeY);

            int cellCount = row.getChildCount();
            for (int j = 0; j < cellCount; j++) {
                View cell = row.getChildAt(j);
                TextView textView = (TextView) cell;
                textView.setTextSize(COMPLEX_UNIT_PX, fontSize);
                textView.measure(cellSizeX, cellSizeY);
                textView.setMinimumHeight(cellSizeY);
                textView.setMinimumWidth(cellSizeX);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChanged(@NonNull Cell cell) {
        TextView textView = (TextView) cell.tag;
        if (textView != null) {
            new CellAnimator(textView, cell.toString());
        }
    }

    public void animateGuess(@NonNull Guess guess) {
        new GuessAnimator(guess);
        if (guess.last) {
            {
                Transition explode = new Explode();
                explode.setEpicenterCallback(center);
                explode.setDuration(ANIMATION_DURATION);
                TransitionManager.beginDelayedTransition(this, explode);
                removeSomeViews(8);
            }
            {
                Transition explode = new Explode();
                explode.setEpicenterCallback(center);
                explode.setDuration(ANIMATION_DURATION);
                TransitionManager.beginDelayedTransition(this, explode);
                removeSomeViews(5);
            }
            {
                Transition explode = new Explode();
                explode.setEpicenterCallback(center);
                explode.setDuration(ANIMATION_DURATION);
                TransitionManager.beginDelayedTransition(this, explode);
                removeSomeViews(3);
            }
            {
                Transition explode = new Explode();
                explode.setEpicenterCallback(center);
                explode.setDuration(ANIMATION_DURATION);
                TransitionManager.beginDelayedTransition(this, explode);
                removeAllViews();
            }
        }
    }

    private void removeSomeViews(int ratio) {
        Random rand = new Random();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            TableRow child = (TableRow) getChildAt(i);
            int cellCount = child.getChildCount();
            for (int j = cellCount - 1; j >= 0; j--) {
                if (rand.nextInt(ratio) < 1) {
                    child.removeViewAt(j);
                }
            }
        }
    }
}
