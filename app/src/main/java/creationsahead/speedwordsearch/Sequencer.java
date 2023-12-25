package creationsahead.speedwordsearch;

import android.support.annotation.NonNull;

/**
 * A class that specifies permutations of letters, directions, and coordinates
 */
public interface Sequencer {
    @NonNull
    int[] getLetterSequence();
    @NonNull
    int[] getXCoordinateSequence();
    @NonNull
    int[] getYCoordinateSequence();
    @NonNull
    int[] getDirectionSequence();

    int getMisc(int max);

    int getBonus(int index);
}
