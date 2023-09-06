package creationsahead.speedwordsearch;

/**
 * A game manages the puzzle grid
 */
public class Game {
    private final PuzzleGrid mGrid;
    private final Config mConfig;
    private boolean success;

    /**
     * Construct a game with a square puzzle grid
     */
    public Game(Config config) {
        mGrid = new PuzzleGrid(config);
        mConfig = config;
    }

    /**
     * Populate an empty puzzle
     * @param size size of square grid
     * @param maxIterations use iterations specified or less
     * @return true if last iteration added at least 1 letter
     */
    public boolean populatePuzzle(final int size, int maxIterations) {
        for (int i = 0; i < maxIterations; i++) {
            success = false;
            mGrid.findEmptyCell(size, (selection, contents) -> {
                mConfig.dictionary.searchWithWildcards(contents, mConfig.sequencer, result -> {
                    success = mGrid.addWord(selection, result);
                    return success;
                });
                return success;
            });
            if (!success) {
                return false;
            }
        }
        return true;
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
        // Account for selection made in flipped order
        String altAnswer = mGrid.findContents(selection.flipped(), false);
        return mGrid.removeWord(answer) || mGrid.removeWord(altAnswer);
    }

    @Override
    public String toString() {
        return mGrid.toString();
    }
}
