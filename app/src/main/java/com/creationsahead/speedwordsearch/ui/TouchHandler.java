package com.creationsahead.speedwordsearch.ui;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.creationsahead.speedwordsearch.GridCallback;
import com.creationsahead.speedwordsearch.R;
import static java.lang.Math.min;

@SuppressLint("ViewConstructor")
public class TouchHandler extends View implements View.OnTouchListener {

    private boolean lastSelected = false;
    private float rawX1 = -1;
    private float rawY1 = -1;
    private float rawX2 = -1;
    private float rawY2 = -1;
    private int lastIndexX, lastIndexY;
    private int lastLastIndexX, lastLastIndexY;
    private final float cellSizeX, cellSizeY;
    private final Paint paint = new Paint();
    private final GridCallback gridCallback;

    public TouchHandler(View view, GridCallback callback, int sizeX, int sizeY) {
        super(view.getContext());
        gridCallback = callback;
        lastLastIndexX = -1;
        lastLastIndexY = -1;
        cellSizeX = sizeX;
        cellSizeY = sizeY;
        paint.setColor(view.getResources().getColor(R.color.bright_yellow_alpha));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(min(sizeX, sizeY) / 3.0f);
    }

    // Get gridView's position and size and use that to set overlay
    public void setAsOverlay(View view) {
        ViewGroup parent = (android.view.ViewGroup) view.getParent();
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(view.getWidth(), view.getHeight());
        int index = parent.indexOfChild(view);
        parent.addView(this, index + 1, params);
        setX(view.getX());
        setY(view.getY());
        setZ(Float.MAX_VALUE);
    }

    @Override
    public void onDraw(@NonNull Canvas canvas) {
        if (rawX1 != -1 && rawX2 != -1) {
            canvas.drawLine(rawX1, rawY1, rawX2, rawY2, paint);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            rawX1 = event.getX();
            rawY1 = event.getY();
            lastIndexX = (int) (rawX1 / cellSizeX);
            lastIndexY = (int) (rawY1 / cellSizeY);
            rawX2 = -1;
            rawY2 = -1;
            invalidate();
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            rawX2 = event.getX();
            rawY2 = event.getY();
            invalidate();
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            rawX1 = -1;
            rawX2 = -1;
            invalidate();

            int indexX = (int) (event.getX() / cellSizeX);
            int indexY = (int) (event.getY() / cellSizeY);

            if (indexX == lastIndexX && indexY == lastIndexY) {
                // Single click action
                if (!lastSelected) {
                    // First single click, save state and mark selected cell
                    lastSelected = true;
                    lastLastIndexX = indexX;
                    lastLastIndexY = indexY;
                    gridCallback.onCellSelected(lastIndexX, lastIndexY);
                    return true;
                } else {
                    lastSelected = false;
                    gridCallback.onCellDeselected(lastIndexX, lastIndexY);
                }
            }
            if (lastLastIndexX != -1 && lastLastIndexY != -1) {
                gridCallback.onCellDeselected(lastLastIndexX, lastLastIndexY);
                if (indexX == lastIndexX && indexY == lastIndexY) {
                    lastIndexX = lastLastIndexX;
                    lastIndexY = lastLastIndexY;
                }
                lastLastIndexX = -1;
                lastLastIndexY = -1;
                lastSelected = false;
            }

            if (gridCallback != null) {
                gridCallback.onGuess(lastIndexX, lastIndexY, indexX, indexY);
            }
            return true;
        }
        return false;
    }
}
