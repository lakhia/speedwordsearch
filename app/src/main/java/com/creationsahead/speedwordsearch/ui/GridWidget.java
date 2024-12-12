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
import android.widget.Toast;
import com.creationsahead.speedwordsearch.Cell;
import com.creationsahead.speedwordsearch.Game;
import com.creationsahead.speedwordsearch.Guess;
import com.creationsahead.speedwordsearch.ProgressTracker;
import com.creationsahead.speedwordsearch.R;
import com.creationsahead.speedwordsearch.Selection;
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
    @NonNull private final Game game;
    private TouchHandler handler;

    public GridWidget(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        EventBus.getDefault().register(this);

        setBackgroundResource(R.color.black_overlay);
        GameApplication app = (GameApplication) getContext().getApplicationContext();
        Typeface typeface = app.loader.letterTypeface;
        tracker = ProgressTracker.getInstance();
        game = tracker.getGame();

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
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int widgetSize = Math.min(measuredHeight, measuredWidth);
        int cellSizeX = widgetSize / tracker.config.sizeX;
        int cellSizeY = widgetSize / tracker.config.sizeY;
        setMeasuredDimension(measuredWidth, widgetSize);
        handler.setWidgetAndSize(this, cellSizeX, cellSizeY);

        int childCount = getChildCount();
        float fontSize = tracker.normalizedFontSize / childCount;
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

    private void updateScore(@NonNull Guess guess) {
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

    public void onGuess(int x1, int y1, int x2, int y2) {
        Selection selection = Selection.isValid(x1, y1, x2, y2);
        if (selection != null) {
            Guess guess = game.guess(selection);
            updateScore(guess);
        } else {
            if (x1 != x2 && y1 != y2) {
                Toast.makeText(getContext(), "Invalid Selection", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
