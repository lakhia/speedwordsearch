package creationsahead.speedwordsearch;

import android.support.annotation.NonNull;

/**
 * A game manages the puzzle grid
 */
public class Game {
    @NonNull private final PuzzleGrid mGrid;
    private final Config mConfig;
    private boolean success;
    private int letterCount;

    /**
     * Construct a game with a square puzzle grid
     */
    public Game(Config config) {
        letterCount = 0;
        mGrid = new PuzzleGrid(config);
        mConfig = config;
    }

    /**
     * Populate entire puzzle based on configuration
     */
    public void populatePuzzle() {
        int letterLimit = mConfig.sizeX * mConfig.sizeY * mConfig.letterRatio / 100;
        int maxSize = mConfig.sizeX;
        int minSize = mConfig.sizeX;
        while (letterCount < letterLimit) {
            if (!addOneWord(minSize, maxSize)) {
                maxSize--;
                minSize--;
                if (minSize < 2) {
                    break;
                }
            }
        }
    }

    /**
     * Fill all the empty cells with random letters
     * TODO: Currently replaces placeholder cells too
     */
    public void fillEmptyCells() {
        // Need indirection to allow modification in lambda function
        final int[][] letterSeq = { mConfig.sequencer.getNextLetterSequence() };
        final int[] index = { 0 };
        mGrid.findEmptyCell((position) -> {
            char letter = (char) ('A' + letterSeq[0][index[0]]);
            if (!mGrid.addLetter(position, letter)) {
                throw new RuntimeException("Could not add letter");
            }
            index[0]++;
            if (index[0] >= letterSeq[0].length) {
                index[0] = 0;
                letterSeq[0] = mConfig.sequencer.getNextLetterSequence();
            }
            return false;
        });
    }

    /**
     * Populate an empty puzzle
     * @param maxSize size of square grid
     * @return true if last iteration added at least 1 letter
     */
    public boolean addOneWord(final int minSize, final int maxSize) {
        success = false;
        mGrid.findEmptyCell(maxSize, (selection, contents) -> {
            mConfig.dictionary.searchWithWildcards(contents, mConfig.sequencer, result -> {
                int len = result.length();
                if (len < minSize) {
                    return false;
                }
                success = mGrid.addWord(selection, result);
                if (success) {
                    letterCount += len;
                }
                return success;
            });
            return success;
        });
        return success;
    }

    /**
     * Get cell at coordinate x and y, assumes coordinates are within range
     */
    public Cell getCell(int x, int y) {
        return mGrid.getCell(x, y);
    }

    /**
     * Validate a guess and mark word from grid as un-used
     * @param selection selection made by user
     * @return true if word was removed
     */
    public boolean guess(Selection selection) {
        // Use placeholder letters to find word so that accidental words are found
        String answer = mGrid.findContents(selection, false);
        return mGrid.removeWord(answer);
    }

    @Override
    public String toString() {
        return mGrid.toString();
    }
}
