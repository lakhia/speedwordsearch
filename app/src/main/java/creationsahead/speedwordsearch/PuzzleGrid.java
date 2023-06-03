package creationsahead.speedwordsearch;

import java.util.HashMap;

/**
 * A puzzle grid
 */
public class PuzzleGrid {
    private final Cell[][] mGrid;
    private final HashMap<String, Position> mWords;

    /**
     * Create a grid using specified x and y size
     */
    public PuzzleGrid(int x, int y) {
        mGrid = new Cell[x][y];
        mWords = new HashMap<>();
        for (Cell[] row: mGrid) {
            for (int i=0; i<row.length; i++) {
                row[i] = new Cell();
            }
        }
    }

    /**
     * Add word to grid in specified direction and coordinates
     * @return True if successfully added
     * @throws RuntimeException if word is partially added
     */
    public boolean addWord(int startX, int startY, Direction dir, String word) {
        // Check bounds
        if (startX < 0 || startY < 0 || startX >= mGrid.length || startY >= mGrid[0].length) {
            return false;
        }
        int len = word.length() - 1;
        int endX = startX + dir.x * len;
        int endY = startY + dir.y * len;
        if (endX < 0 || endY < 0 || endX >= mGrid.length || endY >= mGrid[0].length) {
            return false;
        }
        // Cannot add same word twice
        if (mWords.containsKey(word)) {
            return false;
        }

        // Check overwriting letters
        for (int x = startX, y = startY, i=0; i <= len; i++) {
            if (!mGrid[x][y].store(word.charAt(i))) {
                throw new RuntimeException("Board in inconsistent state, word partially inserted");
            }
            x += dir.x;
            y += dir.y;
        }

        mWords.put(word, new Position(dir, startX, startY));
        return true;
    }

    /**
     * Remove word previously added
     * @return True if successfully removed
     * @throws RuntimeException if word is partially removed
     */
    public boolean removeWord(String word) {
        Position pos = mWords.get(word);
        if (pos == null) {
            return false;
        }
        int len = word.length() - 1;
        for (int x = pos.x, y = pos.y, i=0; i <= len; i++) {
            if (!mGrid[x][y].erase(word.charAt(i))) {
                throw new RuntimeException("Board in inconsistent state, word not stored in previous step");
            }
            x += pos.direction.x;
            y += pos.direction.y;
        }
        return true;
    }

    /**
     * Find an empty cell based on randomness controlled by sequencer
     * @return Position that indicates vacant cell coordinates
     */
    public Position findEmptyCell(Sequencer sequencer) {
        int rows[] = sequencer.getNextCoordinateSequence();
        for (int x : rows) {
            int cols[] = sequencer.getNextCoordinateSequence();
            for (int y : cols) {
                if (mGrid[x][y].isEmpty()) {
                    // TODO: Fix dir
                    return new Position(Direction.NONE, x, y);
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y=0; y < mGrid[0].length; y++) {
            for (Cell[] cells: mGrid) {
                sb.append(cells[y]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
