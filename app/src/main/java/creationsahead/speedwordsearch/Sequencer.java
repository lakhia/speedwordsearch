package creationsahead.speedwordsearch;

/**
 * A class that specifies permutations of letters, directions, and coordinates
 */
public interface Sequencer {
    int[] getLetterSequence();
    int[] getXCoordinateSequence();
    int[] getYCoordinateSequence();
    int[] getDirectionSequence();
}
