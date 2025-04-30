package com.creationsahead.speedwordsearch.ui;

import android.graphics.Rect;
import android.transition.Transition;
import androidx.annotation.NonNull;
import java.util.Random;

/**
 * Epicenter class
 */
public class Center extends Transition.EpicenterCallback {
    private final int width;
    private final int centerY, bottomY;
    @NonNull
    private final Random mRandom;

    public Center(@NonNull Rect rect) {
        width = rect.width();
        centerY = rect.centerY();
        bottomY = centerY + rect.height()/4;
        mRandom = new Random();
    }

    @NonNull
    @Override
    public Rect onGetEpicenter(@NonNull Transition transition) {
        return new Rect(0, centerY,
                mRandom.nextInt(width), centerY + mRandom.nextInt(bottomY));
    }
}
