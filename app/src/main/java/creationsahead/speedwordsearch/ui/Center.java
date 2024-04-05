package creationsahead.speedwordsearch.ui;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.transition.Transition;

/**
 * Epicenter class
 */
public class Center extends Transition.EpicenterCallback {
    private final Rect mRect;

    public Center(Rect rect) {
        mRect = rect;
    }

    @Override
    public Rect onGetEpicenter(@NonNull Transition transition) {
        return mRect;
    }
}
