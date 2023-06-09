package creationsahead.speedwordsearch;

/**
 * Enum indicating direction of word
 */
public enum Direction {

    /// No direction
    NONE(0, 0),

    // Horizontal / vertical directions
    NORTH(0, -1),
    EAST(1, 0),
    SOUTH(0, 1),
    WEST(-1, 0),

    // Diagonal directions
    NORTH_EAST(1, -1),
    NORTH_WEST(-1, -1),
    SOUTH_EAST(1, 1),
    SOUTH_WEST(-1, 1);

    // All the directions, useful for iterating
    public static final Direction ALL_DIRECTIONS[]  =
        { NORTH, EAST, SOUTH, WEST, NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST };

    public final int x;
    public final int y;

    Direction(int xDir, int yDir) {
        x = xDir;
        y = yDir;
    }
}
