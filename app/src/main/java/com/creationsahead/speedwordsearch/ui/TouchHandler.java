package com.creationsahead.speedwordsearch.ui;

import static java.lang.Math.min;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.creationsahead.speedwordsearch.R;

public class TouchHandler extends View implements View.OnTouchListener {

    private View lastHit, lastSelection;
    private float rawX1 = -1;
    private float rawY1 = -1;
    private float rawX2 = -1;
    private float rawY2 = -1;
    private int lastIndexX, lastIndexY;
    private float cellSizeX, cellSizeY;
    private GridWidget gridWidget;
    private final Paint paint = new Paint();

    public TouchHandler(Context context) {
        this(context, null, 0);
    }

    public TouchHandler(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TouchHandler(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint.setColor(getResources().getColor(R.color.bright_yellow_alpha));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (rawX1 != -1 || rawX2 != -1) {
            canvas.drawLine(rawX1, rawY1, rawX2, rawY2, paint);
        }
    }

    public void setWidgetAndSize(GridWidget widget, int sizeX, int sizeY) {
        gridWidget = widget;
        cellSizeX = sizeX;
        cellSizeY = sizeY;
        paint.setStrokeWidth(min(sizeX, sizeY) / 3.0f);
    }

    private static View findHit(ViewGroup parent, int index, int rawX, int rawY) {
        Rect hitRect = new Rect();

        for (int i = index-1; i < index+1; i++) {
            View child = parent.getChildAt(i);
            if (child == null)
                continue;
            child.getGlobalVisibleRect(hitRect);

            if (hitRect.contains(rawX, rawY)) {
                return child;
            }
        }
        return null;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int currentX = (int) view.getTag(R.string.row);
        int currentY = (int) view.getTag(R.string.column);

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            lastIndexX = currentX;
            lastIndexY = currentY;
            lastHit = view;
            rawX1 = event.getRawX();
            rawY1 = event.getRawY();
            rawX2 = -1;
            rawY2 = -1;
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            rawX2 = event.getRawX();
            rawY2 = event.getRawY();
            invalidate();
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            rawX1 = -1;
            rawX2 = -1;
            invalidate();

            float indexX = lastIndexX + (event.getX() / cellSizeX);
            float indexY = lastIndexY + (event.getY() / cellSizeY);

            ViewGroup row = (ViewGroup) findHit(gridWidget, (int) indexY, (int) event.getRawX(), (int) event.getRawY());
            if (row == null) {
                return false;
            }
            TextView textView = (TextView) findHit(row, (int) indexX, (int) event.getRawX(), (int) event.getRawY());
            if (textView == null) {
                return false;
            }
            if (lastHit != textView) {
                // Drag action
                lastHit = null;
            } else {
                // Single click action
                if (lastSelection == null) {
                    // First single click, save state and mark selected cell
                    lastSelection = lastHit;
                    lastHit = null;
                    lastSelection.setBackgroundResource(R.drawable.cell_selected);
                    return true;
                } else {
                    // Two single clicks determine a selection, clear selection
                    lastSelection.setBackgroundResource(R.drawable.cell);
                    lastIndexX = (int) lastSelection.getTag(R.string.row);
                    lastIndexY = (int) lastSelection.getTag(R.string.column);
                    lastSelection = null;
                }
            }
            currentX = (int) textView.getTag(R.string.row);
            currentY = (int) textView.getTag(R.string.column);
            gridWidget.onGuess(lastIndexX, lastIndexY, currentX, currentY);
            return true;
        }
        return false;
    }
}
