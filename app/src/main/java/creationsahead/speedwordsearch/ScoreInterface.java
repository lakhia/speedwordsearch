package creationsahead.speedwordsearch;

import android.support.annotation.NonNull;

/**
 * Track scoring
 */
public interface ScoreInterface {
    int computeScore(@NonNull String word);
}
