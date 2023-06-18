package creationsahead.speedwordsearch;

import java.util.HashMap;

import static creationsahead.speedwordsearch.Direction.ALL_DIRECTIONS;

/**
 * A puzzle grid
 */
public class PuzzleGrid {
    private final Cell[][] mGrid;
    private final HashMap<String, Position> mWords;
    private final int maxX, maxY;

    /**
     * Create a grid using specified x and y size
     */
    public PuzzleGrid(int x, int y) {
        maxX = x;
        maxY = y;
        mGrid = new Cell[x][y];
        mWords = new HashMap<>();
        for (int i=0; i<maxX; i++) {
            for (int j=0; j<maxY; j++) {
                mGrid[i][j] = new Cell();
            }
        }
    }

    /**
     * Add word to grid in specified direction and coordinates
     * @return True if successfully added
     * @throws RuntimeException if word is partially added
     */
    public boolean addWord(Position position, String word) {
        // Check bounds
        if (position.x < 0 || position.y < 0 || position.x >= maxX || position.y >= maxY) {
            return false;
        }
        Direction dir = position.direction;
        int len = word.length() - 1;
        int endX = position.x + dir.x * len;
        int endY = position.y + dir.y * len;
        if (endX < 0 || endY < 0 || endX >= maxX || endY >= maxY) {
            return false;
        }
        // Cannot add same word twice
        if (mWords.containsKey(word)) {
            return false;
        }

        // Check overwriting letters
        for (int x = position.x, y = position.y, i=0; i <= len; i++) {
            if (!mGrid[x][y].store(word.charAt(i))) {
                throw new RuntimeException("Board in inconsistent state, word partially inserted");
            }
            x += dir.x;
            y += dir.y;
        }

        mWords.put(word, position);
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
        mWords.remove(word);
        return true;
    }

    /**
     * Find an empty cell based on randomness controlled by sequencer
     * @param sequencer controls randomness
     * @return Position that indicates vacant cell coordinates
     */
    public Position findEmptyCell(Sequencer sequencer) {
        int rows[] = sequencer.getNextCoordinateSequence();
        for (int x : rows) {
            int cols[] = sequencer.getNextCoordinateSequence();
            for (int y : cols) {
                if (mGrid[x][y].isEmpty()) {
                    return new Position(x, y, Direction.NONE);
                }
            }
        }
        return null;
    }

    /**
     * Find an empty cell based on randomness controlled by sequencer
     * @param sequencer controls randomness
     * @param size specified size of potential word
     * @param callback called for each assignment that is possible
     */
    public void findEmptyCell(Sequencer sequencer, int size, AssignCallback callback) {
        Position position = findEmptyCell(sequencer);
        int dirs[] = sequencer.getDirectionSequence();
        for (int dirIndex: dirs) {
            Direction dir = ALL_DIRECTIONS[dirIndex];
            if (dir.x * size < maxX && dir.y * size < maxY) {
                callback.onUpdate(new Position(position.x, position.y, dir));
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y=0; y < maxY; y++) {
            for (Cell[] cells: mGrid) {
                sb.append(cells[y]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
