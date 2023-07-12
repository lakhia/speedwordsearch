package creationsahead.speedwordsearch;

/**
 * A game manages the puzzle grid
 */
public class Game {
    private final PuzzleGrid mGrid;
    private final Trie mDictionary;
    private final Sequencer mSequencer;
    private final int maxDim;

    /**
     * Construct a game with a square puzzle grid
     */
    public Game(Trie dictionary, int size, int seed) {
        maxDim = size;
        mGrid = new PuzzleGrid(size, size);
        mDictionary = dictionary;
        mSequencer = new Sequencer(seed, size);
    }

    public void populatePuzzle(int iterations) {
        for (int i = 0; i < iterations; i++) {
            mGrid.findEmptyCell(mSequencer, maxDim, new AssignCallback() {
                boolean success = false;
                @Override
                public boolean onUpdate(final Position position, String contents) {
                    mDictionary.searchWithWildcards(contents, mSequencer, new ValidateCallback() {
                        @Override
                        public boolean onValid(String result) {
                            success = mGrid.addWord(position, result);
                            return success;
                        }
                    });
                    return success;
                }
            });
        }
    }

    @Override
    public String toString() {
        return mGrid.toString();
    }
}
