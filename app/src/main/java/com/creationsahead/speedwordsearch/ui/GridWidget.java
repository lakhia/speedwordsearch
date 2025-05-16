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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.creationsahead.speedwordsearch.Cell;
import com.creationsahead.speedwordsearch.Game;
import com.creationsahead.speedwordsearch.GridCallback;
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
    private int cellSizeX, cellSizeY;
    private GridCallback callback;

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
                row.addView(textView);
                cell.tag = textView;
            }
        }

        center = new Center(tracker.displayRect);
    }

    public void setupTouchHandler(GridCallback gridCallback) {
        callback = gridCallback;
        handler = new TouchHandler(this, gridCallback, cellSizeX, cellSizeY);
        setOnTouchListener(handler);
        handler.setAsOverlay(this);
    }

    public void setCellResource(int x, int y, int resource) {
        TableRow child = (TableRow) getChildAt(y);
        View view = child.getChildAt(x);
        view.setBackgroundResource(resource);
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
        cellSizeX = widgetSize / tracker.config.sizeX;
        cellSizeY = widgetSize / tracker.config.sizeY;
        setMeasuredDimension(widgetSize, widgetSize);

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
            setOnTouchListener(null);
            {
                Transition explode = new Explode();
                explode.setEpicenterCallback(center);
                explode.setDuration((long) (ANIMATION_DURATION * 0.9f));
                setEndTransitionCallback(explode);
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
                explode.setDuration((long) (ANIMATION_DURATION * 1.3f));
                TransitionManager.beginDelayedTransition(this, explode);
                removeSomeViews(3);
            }
            {
                Transition explode = new Explode();
                explode.setEpicenterCallback(center);
                explode.setDuration(ANIMATION_DURATION);
                TransitionManager.beginDelayedTransition(this, explode);
                removeSomeViews(2);
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

    private void setEndTransitionCallback(@NonNull Transition explode) {
        explode.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {}

            @Override
            public void onTransitionEnd(Transition transition) {
                callback.onWin();
            }

            @Override
            public void onTransitionCancel(Transition transition) {}

            @Override
            public void onTransitionPause(Transition transition) {}

            @Override
            public void onTransitionResume(Transition transition) {}
        });
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

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        handler.setVisibility(visibility);
    }
}
