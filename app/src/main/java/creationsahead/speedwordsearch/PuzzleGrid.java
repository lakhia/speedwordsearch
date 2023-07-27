package creationsahead.speedwordsearch;

import android.support.annotation.NonNull;
import java.util.HashMap;

import static creationsahead.speedwordsearch.Direction.ALL_DIRECTIONS;

/**
 * A puzzle grid
 */
public class PuzzleGrid {
    private final Cell[][] mGrid;
    private final HashMap<String, Selection> mWords;
    private final int sizeX, sizeY;

    /**
     * Create a grid using specified x and y size
     */
    public PuzzleGrid(int x, int y) {
        sizeX = x;
        sizeY = y;
        mGrid = new Cell[sizeX][sizeY];
        mWords = new HashMap<>();
        for (x=0; x< sizeX; x++) {
            for (y=0; y< sizeY; y++) {
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

        // TODO: selection and word size mismatch?
        // Check bounds
        if (!selection.inBounds(sizeX, sizeY)) {
            return false;
        }

        // Check overwriting letters
        Direction dir = selection.direction;
        for (int x = selection.position.x, y = selection.position.y, i=0; i < selection.length; i++) {
            if (!mGrid[x][y].store(word.charAt(i))) {
                throw new RuntimeException("Board in inconsistent state, word partially inserted");
            }
            x += dir.x;
            y += dir.y;
        }

        mWords.put(word, selection);
        return true;
    }

    /**
     * Remove word previously added
     * @return True if successfully removed
     * @throws RuntimeException if word is partially removed
     */
    public boolean removeWord(String word) {
        Selection selection = mWords.get(word);
        if (selection == null) {
            return false;
        }
        int len = word.length() - 1;
        Position pos = selection.position;
        for (int x = pos.x, y = pos.y, i=0; i <= len; i++) {
            if (!mGrid[x][y].erase(word.charAt(i))) {
                throw new RuntimeException("Board in inconsistent state, word not stored in previous step");
            }
            x += selection.direction.x;
            y += selection.direction.y;
        }
        mWords.remove(word);
        return true;
    }

    /**
     * Find an empty cell based on randomness controlled by sequencer
     * @param sequencer controls randomness
     * @param callback called for each valid empty position
     * @return Position that indicates vacant cell coordinates
     */
    public Position findEmptyCell(Sequencer sequencer, PositionCallback callback) {
        int rows[] = sequencer.getNextCoordinateSequence();
        for (int x : rows) {
            int cols[] = sequencer.getNextCoordinateSequence();
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
     * Find a selection that includes an empty cell
     * @param sequencer controls randomness
     * @param length length of potential word
     * @param callback called for each assignment that is possible
     */
    public void findEmptyCell(final Sequencer sequencer, final int length,
                              @NonNull final AssignCallback callback) {
        findEmptyCell(sequencer, new PositionCallback() {
            @Override
            public boolean onUpdate(final Position position) {
                int dirs[] = sequencer.getDirectionSequence();
                for (int dirIndex: dirs) {
                    Direction dir = ALL_DIRECTIONS[dirIndex];
                    Position pos = position;

                    // If position can accommodate length, process it
                    if (Selection.inBounds(pos, dir, sizeX, sizeY, length)) {
                        Selection selection = new Selection(pos, dir, length);
                        return callback.onUpdate(selection, findContents(selection, true));
                    }
                }
                return false;
            }
        });
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
        for (int y=0; y < sizeY; y++) {
            for (Cell[] cells: mGrid) {
                sb.append(cells[y]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
