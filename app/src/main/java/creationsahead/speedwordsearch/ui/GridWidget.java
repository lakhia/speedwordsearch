package creationsahead.speedwordsearch.ui;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.Explode;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import creationsahead.speedwordsearch.Cell;
import creationsahead.speedwordsearch.Event;
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
    private int cellSize = -1;
    @Nullable private View lastSelection = null;
    @NonNull private final Rect bounds = new Rect();
    private float fontSize = -1;
    @Nullable private CellAnimator anim;
    private Center center = null;

    public GridWidget(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setBackgroundResource(R.color.black_overlay);
        Typeface typeface = ResourcesCompat.getFont(context, R.font.archivo_black);

        int numCells = ProgressTracker.getInstance().config.sizeX;
        for (int i = 0; i < numCells; i++) {
            TableRow row = new TableRow(context);
            addView(row);

            for (int j = 0; j < numCells; j++) {
                ContextThemeWrapper newContext = new ContextThemeWrapper(context, R.style.PuzzleLetter);
                TextView textView = new TextView(newContext, null);
                Cell cell = ProgressTracker.getInstance().game.getCell(i, j);
                textView.setText(cell.toString());
                textView.setTypeface(typeface);
                textView.setOnClickListener(this);
                textView.setTag(R.string.row, i);
                textView.setTag(R.string.column, j);
                row.addView(textView);
                cell.tag = textView;
            }
        }

        final Rect rect = new Rect();
        getWindowVisibleDisplayFrame(rect);
        center = new Center(rect);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
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
        cellSize = Math.max(widgetSize / ProgressTracker.getInstance().config.sizeX, cellSize);
        setMeasuredDimension(widgetSize, widgetSize);

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            TableRow row = (TableRow) child;
            row.measure(widgetSize, cellSize);
            row.setMinimumWidth(widgetSize);
            row.setMinimumHeight(cellSize);

            int cellCount = row.getChildCount();
            for (int j = 0; j < cellCount; j++) {
                View cell = row.getChildAt(j);
                TextView textView = (TextView) cell;
                if (fontSize == -1) {
                    textView.getPaint().getTextBounds("W", 0, 1, bounds);
                    int max = Math.max(bounds.width(), bounds.height());
                    fontSize = 0.6f * textView.getTextSize() * cellSize / max;
                    Event event = Event.FONT_SIZE;
                    event.fontSize = fontSize;
                    EventBus.getDefault().post(event);
                }
                textView.setTextSize(COMPLEX_UNIT_PX, fontSize);
                textView.measure(cellSize, cellSize);
                textView.setMinimumHeight(cellSize);
                textView.setMinimumWidth(cellSize);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChanged(@NonNull Cell cell) {
        if (cell.tag != null) {
            TextView textView = (TextView) cell.tag;
            if (cell.event == Event.CELL_STORED) {
                textView.setText(cell.toString());
            } else if (cell.event == Event.CELL_SELECTION_CORRECT ||
                cell.event == Event.CELL_SELECTION_INCORRECT) {
                if (anim == null) {
                    anim = new CellAnimator(cell.event == Event.CELL_SELECTION_CORRECT);
                }
                anim.add(textView);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateScore(@NonNull Event event) {
        if (event == Event.SCORE_AWARDED && event.lastWordGuessed) {
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
            anim = null;
            view.setBackgroundResource(R.drawable.cell_selected);
        } else {
            lastSelection.setBackgroundResource(R.drawable.cell);
            lastSelection = null;
            Selection selection = Selection.isValid(lastX, lastY, currentX, currentY);
            if (selection != null) {
                ProgressTracker.getInstance().game.guess(selection);
            } else {
                if (currentX != lastX && currentY != lastY) {
                    Toast.makeText(getContext(), "Invalid Selection", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
