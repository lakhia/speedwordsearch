package creationsahead.speedwordsearch;

import androidx.annotation.NonNull;

/**
 * Track scoring
 */
public interface ScoreInterface {
    int computeScore(@NonNull String word);
}
