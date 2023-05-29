package creationsahead.speedwordsearch;

/**
 * Enum indicating direction of word
 */
public enum Direction {

    NORTH(0, -1),
    EAST(1, 0),
    SOUTH(0, 1),
    WEST(-1, 0),

    NORTH_EAST(1, -1),
    NORTH_WEST(-1, -1),
    SOUTH_EAST(1, 1),
    SOUTH_WEST(-1, 1);

    public final int x;
    public final int y;

    Direction(int xDir, int yDir) {
        x = xDir;
        y = yDir;
    }
}
