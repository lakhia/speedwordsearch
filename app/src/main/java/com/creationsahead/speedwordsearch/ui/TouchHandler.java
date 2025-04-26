package com.creationsahead.speedwordsearch.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Insets;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.DisplayCutout;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.creationsahead.speedwordsearch.R;
import static android.os.Build.VERSION.SDK_INT;
import static java.lang.Math.min;

public class TouchHandler extends View implements View.OnTouchListener {

    private View lastSelection;
    private float rawX1 = -1;
    private float rawY1 = -1;
    private float rawX2 = -1;
    private float rawY2 = -1;
    private int topMargin = 0;
    private int lastIndexX, lastIndexY;
    private float cellSizeX, cellSizeY;
    private GameActivity gameActivity;
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
    public void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (rawX1 != -1 || rawX2 != -1) {
            canvas.drawLine(rawX1, rawY1 - topMargin, rawX2, rawY2 - topMargin, paint);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (SDK_INT >= Build.VERSION_CODES.P) {
            WindowInsets insets = getRootWindowInsets();
            if (insets != null) {
                DisplayCutout cutout = insets.getDisplayCutout();
                if (cutout != null) {
                    topMargin = cutout.getSafeInsetTop();
                }
            }
        }
    }

    public void setCellSize(int sizeX, int sizeY) {
        cellSizeX = sizeX;
        cellSizeY = sizeY;
        paint.setStrokeWidth(min(sizeX, sizeY) / 3.0f);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            lastIndexX = (int) view.getTag(R.string.column);
            lastIndexY = (int) view.getTag(R.string.row);
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

            int indexX = (int) (lastIndexX + (event.getX() / cellSizeX));
            int indexY = (int) (lastIndexY + (event.getY() / cellSizeY));

            if (indexX == lastIndexX && indexY == lastIndexY) {
                // Single click action
                if (lastSelection == null) {
                    // First single click, save state and mark selected cell
                    lastSelection = view;
                    lastSelection.setBackgroundResource(R.drawable.cell_selected);
                    return true;
                } else {
                    // Two single clicks determine a selection, clear selection
                    lastSelection.setBackgroundResource(R.drawable.cell);
                    lastIndexX = (int) lastSelection.getTag(R.string.column);
                    lastIndexY = (int) lastSelection.getTag(R.string.row);
                    lastSelection = null;
                }
            }

            gameActivity.onGuess(lastIndexX, lastIndexY, indexX, indexY);
            return true;
        }
        return false;
    }

    public void setActivity(GameActivity activity) {
        gameActivity = activity;
    }
}
