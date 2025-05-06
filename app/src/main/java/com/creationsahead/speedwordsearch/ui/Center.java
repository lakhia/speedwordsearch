package com.creationsahead.speedwordsearch.ui;

import android.graphics.Rect;
import android.transition.Transition;
import androidx.annotation.NonNull;
import java.util.Random;

/**
 * Epicenter class
 */
public class Center extends Transition.EpicenterCallback {
    private final int width, height;
    private final int top, left;
    @NonNull
    private final Random mRandom;

    public Center(@NonNull Rect rect) {
        width = rect.width();
        height = rect.height();
        top = rect.top - height/2;
        left = rect.left - width/2;
        mRandom = new Random();
    }

    @NonNull
    @Override
    public Rect onGetEpicenter(@NonNull Transition transition) {
        int x = left + mRandom.nextInt(width*2);
        int y = top + mRandom.nextInt(height*2);
        return new Rect(x, y, x+1, y+1);
    }
}
