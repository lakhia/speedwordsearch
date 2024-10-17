package com.creationsahead.speedwordsearch.ui;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import androidx.core.content.res.ResourcesCompat;
import com.creationsahead.speedwordsearch.R;

/**
 * Class derived from:
 * <a href="https://github.com/xckevin/AndroidSmartRatingBar">AndroidSmartRatingBar</a> repo
 */
public class SmartRatingBar extends View {

    private int mMaxStarNum = 5;
    private float mRatingNum = 3.3f;
    private Drawable mRatingDrawable;
    private Drawable mRatingBackgroundDrawable;
    private int mOrientation = LinearLayout.HORIZONTAL;

    public SmartRatingBar(Context context) {
        super(context);
        init(context, null);
    }

    public SmartRatingBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            try (TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SmartRatingBar)) {
                mRatingNum = typedArray.getFloat(R.styleable.SmartRatingBar_rating, 2.5f);
                mMaxStarNum = typedArray.getInt(R.styleable.SmartRatingBar_maxRating, 4);
                mOrientation = typedArray.getInt(R.styleable.SmartRatingBar_orientation, LinearLayout.HORIZONTAL);
                mRatingDrawable = typedArray.getDrawable(R.styleable.SmartRatingBar_ratingDrawable);
                mRatingBackgroundDrawable = typedArray.getDrawable(R.styleable.SmartRatingBar_backgroundDrawable);
            }
        }
        if (mRatingDrawable == null && mRatingBackgroundDrawable == null) {
            Resources res = context.getResources();
            mRatingDrawable = ResourcesCompat.getDrawable(res,
                    R.drawable.smart_ratingbar_rating, null);
            mRatingBackgroundDrawable = ResourcesCompat.getDrawable(res,
                    R.drawable.smart_ratingbar_background, null);
        }
    }

    public void setRatingNum(float ratingNum) {
        mRatingNum = ratingNum;
        postInvalidate();
    }

    public Drawable getRatingDrawable() {
        return mRatingDrawable;
    }

    public Drawable getRatingBackgroundDrawable() {
        return mRatingBackgroundDrawable;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth;
        int measuredHeight;
        if (mOrientation == LinearLayout.HORIZONTAL) {
            measuredHeight = measureHeight(heightMeasureSpec);
            if (mRatingDrawable.getIntrinsicHeight() > measuredHeight) {
                mRatingDrawable.setBounds(0, 0, measuredHeight, measuredHeight);
                mRatingBackgroundDrawable.setBounds(0, 0, measuredHeight, measuredHeight);
            } else {
                mRatingDrawable.setBounds(0, 0, mRatingDrawable.getIntrinsicWidth(), mRatingDrawable.getIntrinsicHeight());
                mRatingBackgroundDrawable.setBounds(0, 0, mRatingDrawable.getIntrinsicWidth(),
                        mRatingDrawable.getIntrinsicHeight());
            }
            measuredWidth = measureWidth(widthMeasureSpec);

        } else {
            measuredWidth = measureWidth(widthMeasureSpec);
            if (mRatingDrawable.getIntrinsicWidth() > measuredWidth) {
                mRatingDrawable.setBounds(0, 0, measuredWidth, measuredWidth);
                mRatingBackgroundDrawable.setBounds(0, 0, measuredWidth, measuredWidth);
            } else {
                mRatingDrawable.setBounds(0, 0, mRatingDrawable.getIntrinsicWidth(), mRatingDrawable.getIntrinsicHeight());
                mRatingBackgroundDrawable.setBounds(0, 0, mRatingDrawable.getIntrinsicWidth(),
                        mRatingDrawable.getIntrinsicHeight());
            }
            measuredHeight = measureHeight(heightMeasureSpec);

        }
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final Drawable ratingDrawable = mRatingDrawable;
        final Drawable backgroundDrawable = mRatingBackgroundDrawable;
        final float ratingNum = mRatingNum;
        final Rect drawableBounds = ratingDrawable.getBounds();

        final int ratingNumInt = (int) Math.floor(ratingNum);
        canvas.translate(getPaddingLeft(), getPaddingTop());
        int i = 0;
        for (; i < ratingNumInt; i++) {
            ratingDrawable.draw(canvas);
            translateCanvas(canvas, drawableBounds);
        }

        float ratingPart = ratingNum - ratingNumInt;
        int partWidth, partHeight;
        if (mOrientation == LinearLayout.HORIZONTAL) {
            partWidth = (int) (drawableBounds.width() * ratingPart);
            partHeight = drawableBounds.height();
        } else {
            partWidth = drawableBounds.width();
            partHeight = (int) (drawableBounds.height() * ratingPart);
        }
        // draw rating part
        canvas.save();
        canvas.clipRect(0, 0, partWidth, partHeight);
        ratingDrawable.draw(canvas);
        canvas.restore();

        // draw background part
        canvas.save();
        if (mOrientation == LinearLayout.HORIZONTAL) {
            canvas.clipRect(partWidth, 0, drawableBounds.right, drawableBounds.bottom);
        } else {
            canvas.clipRect(0, partHeight, drawableBounds.right, drawableBounds.bottom);
        }
        backgroundDrawable.draw(canvas);
        canvas.restore();

        translateCanvas(canvas, drawableBounds);
        i++; // move to next
        for (; i < mMaxStarNum; i++) {
            backgroundDrawable.draw(canvas);
            translateCanvas(canvas, drawableBounds);
        }
    }

    private void translateCanvas(Canvas canvas, Rect rect) {
        int mGapSize = 0;
        if (mOrientation == LinearLayout.HORIZONTAL) {
            canvas.translate(mGapSize + rect.width(), 0);
        } else {
            canvas.translate(0, mGapSize + rect.height());
        }
    }

    private int measureWidth(int widthMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            return widthSize;
        }
        int drawableWidth = mRatingDrawable.getBounds().width();
        if (drawableWidth == 0) {
            drawableWidth = mRatingDrawable.getIntrinsicWidth();
        }
        int maxSize = getPaddingLeft() + getPaddingRight();
        if (mOrientation == LinearLayout.HORIZONTAL) {
            maxSize += mMaxStarNum * drawableWidth;
        } else {
            maxSize += drawableWidth;
        }
        if (widthMode == MeasureSpec.AT_MOST) {
            return Math.min(maxSize, widthSize);
        }
        return maxSize;
    }

    private int measureHeight(int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (heightMode == MeasureSpec.EXACTLY) {
            return heightSize;
        }
        int drawableHeight = mRatingDrawable.getBounds().height();
        if (drawableHeight == 0) {
            drawableHeight = mRatingDrawable.getIntrinsicHeight();
        }
        int maxHeight = getPaddingBottom() + getPaddingTop();
        if (mOrientation == LinearLayout.HORIZONTAL) {
            maxHeight += drawableHeight;
        } else {
            maxHeight += mMaxStarNum * drawableHeight;
        }
        if (heightMode == MeasureSpec.AT_MOST) {
            return Math.min(heightSize, maxHeight);
        }
        return maxHeight;
    }
}