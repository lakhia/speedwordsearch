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
        int len = word.length();
        if (!position.inBounds(maxX, maxY, len)) {
            return false;
        }

        // Cannot add same word twice
        if (mWords.containsKey(word)) {
            return false;
        }

        // Check overwriting letters
        Direction dir = position.direction;
        for (int x = position.x, y = position.y, i=0; i < len; i++) {
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
    public Position findEmptyCell(Sequencer sequencer, AssignCallback callback) {
        int rows[] = sequencer.getNextCoordinateSequence();
        for (int x : rows) {
            int cols[] = sequencer.getNextCoordinateSequence();
            for (int y : cols) {
                if (mGrid[x][y].isEmpty()) {
                    int dirs[] = sequencer.getDirectionSequence();
                    for (int dirIndex: dirs) {
                        Direction dir = ALL_DIRECTIONS[dirIndex];
                        Position newPos = new Position(x, y, dir);
                        if (callback != null) {
                            if (callback.onUpdate(newPos, null)) {
                                return null;
                            }
                        } else {
                            return newPos;
                        }
                    }
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
    public void findEmptyCell(Sequencer sequencer, final int size, final AssignCallback callback) {
        findEmptyCell(sequencer, new AssignCallback() {
            @Override
            public boolean onUpdate(Position position, String contents) {
                if (!position.inBounds(maxX, maxY, size)) {
                    int lacking = size - position.getDistanceToBoundary(maxX, maxY);
                    position = new Position(position.x - position.direction.x * lacking,
                                            position.y - position.direction.y * lacking,
                                            position.direction);
                }
                if (position.inBounds(maxX, maxY, size)) {
                    return callback.onUpdate(position, findContents(position, size));
                }
                return false;
            }
        });

    }

    /**
     * Returns content stored in grid at specified position
     * @param size size for content, must be within bounds
     * @return String with blanks and letters
     */
    public String findContents(Position position, int size) {
        int x = position.x, y = position.y;
        StringBuilder result = new StringBuilder();
        Direction dir = position.direction;

        for (int i = 0; i < size; i++) {
            result.append(mGrid[x][y].letter);
            x += dir.x;
            y += dir.y;
        }
        return result.toString();
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
