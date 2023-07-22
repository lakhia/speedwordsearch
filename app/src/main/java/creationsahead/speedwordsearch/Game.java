package creationsahead.speedwordsearch;

/**
 * A game manages the puzzle grid
 */
public class Game {
    private final PuzzleGrid mGrid;
    private final Trie mDictionary;
    private final Sequencer mSequencer;
    private boolean success;

    /**
     * Construct a game with a square puzzle grid
     */
    public Game(Trie dictionary, int size, int seed) {
        mGrid = new PuzzleGrid(size, size);
        mDictionary = dictionary;
        mSequencer = new Sequencer(seed, size);
    }

    public boolean populatePuzzle(final int size, int maxIterations) {
        for (int i = 0; i < maxIterations; i++) {
            success = false;
            mGrid.findEmptyCell(mSequencer, size, new AssignCallback() {
                @Override
                public boolean onUpdate(final Selection selection, String contents) {
                    mDictionary.searchWithWildcards(contents, mSequencer, new ValidateCallback() {
                        @Override
                        public boolean onValid(String result) {
                            success = mGrid.addWord(selection, result);
                            return success;
                        }
                    });
                    return success;
                }
            });
            if (!success) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return mGrid.toString();
    }
}
