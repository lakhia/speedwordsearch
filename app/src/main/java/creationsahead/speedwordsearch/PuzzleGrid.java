package creationsahead.speedwordsearch;

import android.support.annotation.NonNull;
import java.util.HashMap;

import static creationsahead.speedwordsearch.Direction.ALL_DIRECTIONS;

/**
 * A puzzle grid
 */
public class PuzzleGrid {
    private final Cell[][] mGrid;
    private final HashMap<String, Answer> mWords;
    private final Config mConfig;

    /**
     * Create a grid using specified x and y size
     */
    public PuzzleGrid(Config config) {
        mConfig = config;
        mGrid = new Cell[config.sizeX][config.sizeY];
        mWords = new HashMap<>();
        for (int x=0; x < config.sizeX; x++) {
            for (int y=0; y < config.sizeY; y++) {
                mGrid[x][y] = new Cell();
            }
        }
    }

    /**
     * Add word to grid in specified direction and coordinates
     * @return True if successfully added
     * @throws RuntimeException if word is partially added
     */
    public boolean addWord(Selection selection, String word) {
        // Cannot add same word twice
        if (mWords.containsKey(word)) {
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

        int score = ProgressTracker.computeScore(word);
        mWords.put(word, new Answer(selection, word, score));
        return true;
    }

    /**
     * Remove word previously added
     * @return True if successfully removed
     * @throws RuntimeException if word is partially removed
     */
    public boolean removeWord(String word) {
        Answer answer = mWords.get(word);
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
        mWords.remove(word);
        ProgressTracker.addScore(answer.score);
        answer.notifyScoreClaimed();
        return true;
    }

    /**
     * Find an empty cell based on randomness controlled by sequencer
     * @param callback called for each valid empty position
     * @return Position that indicates vacant cell coordinates
     */
    public Position findEmptyCell(PositionCallback callback) {
        int rows[] = mConfig.sequencer.getNextCoordinateSequence();
        for (int x : rows) {
            int cols[] = mConfig.sequencer.getNextCoordinateSequence();
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
    public void findEmptyCell(final int length, @NonNull final AssignCallback callback) {
        findEmptyCell(position -> {
            int dirs[] = mConfig.sequencer.getDirectionSequence();
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
    public void findRandomCells(final int length, @NonNull final AssignCallback callback) {
        int rows[] = mConfig.sequencer.getNextCoordinateSequence();
        for (int x : rows) {
            int cols[] = mConfig.sequencer.getNextCoordinateSequence();
            for (int y : cols) {
                Position position = new Position(x, y);
                int dirs[] = mConfig.sequencer.getDirectionSequence();
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
    public String findContents(Selection selection, boolean searchValue) {
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
     * Get cell at coordinate x and y, assumes coordinates are within range
     */
    public Cell getCell(int x, int y) {
        return mGrid[x][y];
    }
}
