package creationsahead.speedwordsearch;

import androidx.annotation.NonNull;

/**
 * A class that specifies permutations of letters, directions, and coordinates
 */
public interface Sequencer {

    @NonNull
    SequenceIterator<Character> getLetterSequence();
    @NonNull
    SequenceIterator<Integer> getXCoordinateSequence();
    @NonNull
    SequenceIterator<Integer> getYCoordinateSequence();
    @NonNull
    SequenceIterator<Direction> getDirectionSequence();

    int getMisc(int max);
}
