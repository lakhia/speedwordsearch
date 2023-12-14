package creationsahead.speedwordsearch;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.HashMap;

import static creationsahead.speedwordsearch.Direction.ALL_DIRECTIONS;

/**
 * A puzzle grid
 */
public class PuzzleGrid {
    @NonNull private final Cell[][] mGrid;
    @NonNull final HashMap<String, Answer> answerMap;
    @NonNull private final Config mConfig;
    @NonNull private final ScoreInterface mScoreTracker;
    @NonNull private final Sequencer mSequencer;

    /**
     * Create a grid using specified x and y size
     */
    public PuzzleGrid(@NonNull Config config, @NonNull ScoreInterface scoreInterface,
                      @NonNull Sequencer sequencer) {
        mConfig = config;
        mSequencer = sequencer;
        mGrid = new Cell[config.sizeX][config.sizeY];
        answerMap = new HashMap<>();
        mScoreTracker = scoreInterface;
        for (int x=0; x < config.sizeX; x++) {
            for (int y=0; y < config.sizeY; y++) {
                mGrid[x][y] = new Cell();
            }
        }
    }

    /**
     * Add a letter as placeholder
     * @param position where letter is inserted
     * @param letter letter to be added
     * @return true if store was successful
     */
    public boolean addLetter(@NonNull Position position, char letter) {
        return mGrid[position.x][position.y].storePlaceholder(letter);
    }

    /**
     * Clear letter if it is a placeholder
     * @param position where letter is cleared
     * @return true if clear was successful
     */
    public boolean clearLetter(@NonNull Position position) {
        return mGrid[position.x][position.y].clear();
    }

    /**
     * Add word to grid in specified direction and coordinates
     * @return True if successfully added
     * @throws RuntimeException if word is partially added
     */
    public boolean addWord(@NonNull Selection selection, @NonNull String word) {
        // Cannot add same word twice
        if (answerMap.containsKey(word)) {
            return false;
        }

        // Check bounds
        if (!selection.inBounds(mConfig.sizeX, mConfig.sizeY)) {
            return false;
        }

        if (selection.length < word.length()) {
            throw new RuntimeException("Inconsistent length");
        }

        // Check overwriting letters
        Direction dir = selection.direction;
        for (int x = selection.position.x, y = selection.position.y, i=0; i < word.length(); i++) {
            if (!mGrid[x][y].store(word.charAt(i))) {
                throw new RuntimeException("Board in inconsistent state, word partially inserted");
            }
            x += dir.x;
            y += dir.y;
        }

        int score = mScoreTracker.computeScore(word);
        answerMap.put(word, new Answer(selection, word, score));
        return true;
    }

    /**
     * Remove word previously added
     * @return True if successfully removed
     * @throws RuntimeException if word is partially removed
     */
    public boolean removeWord(@NonNull String word) {
        Answer answer = answerMap.get(word);
        if (answer == null) {
            return false;
        }
        int len = word.length() - 1;
        Selection selection = answer.selection;
        Position pos = selection.position;
        for (int x = pos.x, y = pos.y, i=0; i <= len; i++) {
            if (!mGrid[x][y].erase(word.charAt(i))) {
                throw new RuntimeException("Board in inconsistent state, word not stored in previous step");
            }
            x += selection.direction.x;
            y += selection.direction.y;
        }
        answerMap.remove(word);
        mScoreTracker.addScore(answer.score);
        answer.notifyScoreClaimed();
        return true;
    }

    /**
     * Find an empty cell or placeholder cells based on randomness
     * @param callback called for each valid empty position
     * @return Position that indicates vacant cell coordinates
     */
    @Nullable
    public Position findUnusedCells(@Nullable PositionCallback callback) {
        int rows[] = mSequencer.getXCoordinateSequence();
        for (int x : rows) {
            int cols[] = mSequencer.getYCoordinateSequence();
            for (int y : cols) {
                if (mGrid[x][y].isUnused()) {
                    Position newPos = new Position(x, y);
                    if (callback != null) {
                        if (callback.onUpdate(newPos)) {
                            return null;
                        }
                    } else {
                        return newPos;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Find a selection that includes an empty cell and is of size length
     * @param length length of potential word
     * @param callback called for each assignment that is possible
     */
    public void findUnusedSelection(final int length, @NonNull final SelectionCallback callback) {
        findUnusedCells(position -> {
            int dirs[] = mSequencer.getDirectionSequence();
            for (int dirIndex: dirs) {
                Direction dir = ALL_DIRECTIONS[dirIndex];

                // If position can accommodate length, process it
                if (Selection.inBounds(position, dir, mConfig.sizeX, mConfig.sizeY, length)) {
                    Selection selection = new Selection(position, dir, length);
                    return callback.onUpdate(selection, findContents(selection, true));
                }
            }
            return false;
        });
    }

    /**
     * Find a random selection of size length
     * @param length length of potential word
     * @param callback called for each assignment that is possible
     */
    public void findRandomSelection(final int length, @NonNull final SelectionCallback callback) {
        int rows[] = mSequencer.getXCoordinateSequence();
        for (int x : rows) {
            int cols[] = mSequencer.getYCoordinateSequence();
            for (int y : cols) {
                Position position = new Position(x, y);
                int dirs[] = mSequencer.getDirectionSequence();
                for (int dirIndex: dirs) {
                    Direction dir = ALL_DIRECTIONS[dirIndex];

                    // If position can accommodate length, process it
                    if (Selection.inBounds(position, dir, mConfig.sizeX, mConfig.sizeY, length)) {
                        Selection selection = new Selection(position, dir, length);
                        if (callback.onUpdate(selection, findContents(selection, true))) {
                            return;
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns content stored in grid at specified selection
     * @param searchValue if true, placeholders become blanks
     * @return String with blanks and letters
     */
    @NonNull
    public String findContents(@NonNull Selection selection, boolean searchValue) {
        int x = selection.position.x, y = selection.position.y;
        StringBuilder result = new StringBuilder();
        Direction dir = selection.direction;

        for (int i = 0; i < selection.length; i++) {
            char letter;
            if (searchValue) {
                letter = mGrid[x][y].getSearchValue();
            } else {
                letter = mGrid[x][y].letter;
            }
            result.append(letter);
            x += dir.x;
            y += dir.y;
        }
        return result.toString();
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y=0; y < mConfig.sizeY; y++) {
            for (Cell[] cells: mGrid) {
                sb.append(cells[y]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Give a negative bonus in the grid
     */
    public void giveNegativeBonus() {

    }

    /**
     * Give a positive bonus in the grid
     */
    public void givePositiveBonus() {
    }

    /**
     * Get cell at coordinate x and y, assumes coordinates are within range
     */
    public Cell getCell(int x, int y) {
        return mGrid[x][y];
    }
}
