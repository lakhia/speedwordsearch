package creationsahead.speedwordsearch.ui;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import creationsahead.speedwordsearch.Cell;
import creationsahead.speedwordsearch.CellCallback;
import creationsahead.speedwordsearch.ProgressTracker;
import creationsahead.speedwordsearch.R;
import creationsahead.speedwordsearch.Selection;

import static android.util.TypedValue.COMPLEX_UNIT_PX;

/**
 * Widget that displays grid
 */
public class GridWidget extends TableLayout implements CellCallback, View.OnClickListener {
    private int lastX = -1, lastY = -1;
    private int cellSize = -1;
    @Nullable private View lastSelection = null;
    private Rect bounds = new Rect();
    private float fontSize = -1;

    public GridWidget(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setBackgroundResource(R.color.black_overlay);

        int numCells = ProgressTracker.getInstance().config.sizeX;
        for (int i = 0; i < numCells; i++) {
            TableRow row = new TableRow(context);
            addView(row);

            for (int j = 0; j < numCells; j++) {
                ContextThemeWrapper newContext = new ContextThemeWrapper(context, R.style.PuzzleLetter);
                TextView textView = new TextView(newContext, null);
                Cell cell = ProgressTracker.getInstance().game.getCell(i, j);
                textView.setText(cell.toString());
                textView.setOnClickListener(this);
                textView.setTag(R.string.row, i);
                textView.setTag(R.string.column, j);
                row.addView(textView);
                cell.tag = textView;
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Cell.callback = this;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Cell.callback = null;
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
                }
                textView.setTextSize(COMPLEX_UNIT_PX, fontSize);
                textView.measure(cellSize, cellSize);
                textView.setMinimumHeight(cellSize);
                textView.setMinimumWidth(cellSize);
            }
        }
    }

    @Override
    public void onChanged(@NonNull Cell cell) {
        if (cell.tag != null) {
            TextView textView = (TextView) cell.tag;
            textView.setText(cell.toString());
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
            view.setBackgroundResource(R.drawable.border_selected);
        } else {
            lastSelection.setBackgroundResource(R.drawable.border);
            lastSelection = null;
            Selection selection = Selection.isValid(lastX, lastY, currentX, currentY);
            String error = null;
            if (selection != null) {
                if (!ProgressTracker.getInstance().game.guess(selection)) {
                    error = "Did not find word";
                }
            } else {
                if (currentX != lastX && currentY != lastY) {
                    error = "Invalid Selection";
                }
            }
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
