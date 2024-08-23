package creationsahead.speedwordsearch.ui;

import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import android.transition.Explode;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import creationsahead.speedwordsearch.Cell;
import creationsahead.speedwordsearch.Guess;
import creationsahead.speedwordsearch.ProgressTracker;
import creationsahead.speedwordsearch.R;
import creationsahead.speedwordsearch.Selection;
import java.util.Random;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static android.util.TypedValue.COMPLEX_UNIT_PX;
import static creationsahead.speedwordsearch.ui.GameApplication.ANIMATION_DURATION;

/**
 * Widget that displays grid
 */
public class GridWidget extends TableLayout implements View.OnClickListener {
    private int lastX = -1, lastY = -1;
    @Nullable private View lastSelection = null;
    @NonNull
    private final Center center;

    public GridWidget(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        EventBus.getDefault().register(this);

        setBackgroundResource(R.color.black_overlay);
        Typeface typeface = ResourcesCompat.getFont(context, R.font.archivo_black);
        ProgressTracker tracker = ProgressTracker.getInstance();

        for (int j = 0; j < tracker.config.sizeY; j++) {
            TableRow row = new TableRow(context);
            addView(row);

            for (int i = 0; i < tracker.config.sizeX; i++) {
                ContextThemeWrapper newContext = new ContextThemeWrapper(context, R.style.PuzzleLetter);
                TextView textView = new TextView(newContext, null);
                Cell cell = tracker.game.getCell(i, j);
                textView.setText(cell.toString());
                textView.setTypeface(typeface);
                textView.setOnClickListener(this);
                textView.setTag(R.string.row, i);
                textView.setTag(R.string.column, j);
                row.addView(textView);
                cell.tag = textView;
            }
        }

        center = new Center(tracker.displayRect);
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
        int cellSizeX = widgetSize / ProgressTracker.getInstance().config.sizeX;
        int cellSizeY = widgetSize / ProgressTracker.getInstance().config.sizeY;
        setMeasuredDimension(measuredWidth, widgetSize);

        int childCount = getChildCount();
        float fontSize = ProgressTracker.getInstance().normalizedFontSize / childCount;
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

    public void updateScore(@NonNull Guess guess) {
        new GuessAnimator(guess);
        if (guess.last) {
            {
                Transition explode = new Explode();
                explode.setEpicenterCallback(center);
                explode.setDuration(ANIMATION_DURATION);
                TransitionManager.beginDelayedTransition(this, explode);
                removeSomeViews();
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

    private void removeSomeViews() {
        Random rand = new Random();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            TableRow child = (TableRow) getChildAt(i);
            int cellCount = child.getChildCount();
            for (int j = cellCount - 1; j >= 0; j--) {
                if (rand.nextBoolean()) {
                    child.removeViewAt(j);
                }
            }
        }
    }

    @Override
    public void onClick(@NonNull View view) {
        int currentX = (int) view.getTag(R.string.row);
        int currentY = (int) view.getTag(R.string.column);

        if (lastSelection == null) {
            lastX = currentX;
            lastY = currentY;
            lastSelection = view;
            view.setBackgroundResource(R.drawable.cell_selected);
        } else {
            lastSelection.setBackgroundResource(R.drawable.cell);
            lastSelection = null;
            Selection selection = Selection.isValid(lastX, lastY, currentX, currentY);
            if (selection != null) {
                Guess guess = ProgressTracker.getInstance().game.guess(selection);
                updateScore(guess);
            } else {
                if (currentX != lastX && currentY != lastY) {
                    Toast.makeText(getContext(), "Invalid Selection", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
