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
    boolean getPositiveBonus(int index);
    boolean getNegativeBonus(int index);
}
