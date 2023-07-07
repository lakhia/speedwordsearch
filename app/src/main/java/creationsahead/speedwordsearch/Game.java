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
                @Override
                public boolean onUpdate(Position position, String contents) {
                    String word = mDictionary.searchWithWildcards(contents, mSequencer);
                    if (word != null) {
                        return mGrid.addWord(position, word);
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public String toString() {
        return mGrid.toString();
    }
}
