package creationsahead.speedwordsearch;

import android.support.annotation.NonNull;

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

    public final int x;
    public final int y;

    Direction(int xDir, int yDir) {
        x = xDir;
        y = yDir;
    }

    /**
     * Given a direction, return the opposite direction
     */
    @NonNull
    public Direction negate() {
        return find(-x, -y);
    }

    /**
     * Given x and y, find the Direction
     */
    @NonNull
    public static Direction find(int x, int y) {
        switch (x*10 + y) {
            case 1:
                return SOUTH;
            case -1:
                return NORTH;
            case 10:
                return EAST;
            case -10:
                return WEST;
            case -11:
                return NORTH_WEST;
            case 11:
                return SOUTH_EAST;
            case -9:
                return SOUTH_WEST;
            case 9:
                return NORTH_EAST;
            default:
                return NONE;
        }
    }
}
