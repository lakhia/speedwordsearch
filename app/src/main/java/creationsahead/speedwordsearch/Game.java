package creationsahead.speedwordsearch;

/**
 * A game manages the puzzle grid
 */
public class Game {
    private final PuzzleGrid mGrid;
    private final Trie mDictionary;
    private final Sequencer mSequencer;

    public Game(Trie dictionary) {
        mGrid = new PuzzleGrid(5, 5);
        mDictionary = dictionary;
        mSequencer = new Sequencer(1, 5);
    }

    public void populatePuzzle() {
        for (int i = 0; i < 2; i++) {
            mGrid.findEmptyCell(mSequencer, 5, new AssignCallback() {
                @Override
                public void onUpdate(Position position) {
                    String word = mDictionary.searchWithWildcards("....", mSequencer);
                    mGrid.addWord(position, word);
                }
            });
        }
    }

    @Override
    public String toString() {
        return mGrid.toString();
    }
}
