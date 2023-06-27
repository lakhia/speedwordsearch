package creationsahead.speedwordsearch;

/**
 * Indicates position of a word in a grid
 */
public class Position {
    public final int x, y;
    public final Direction direction;

    public Position(int startX, int startY, Direction dir) {
        x = startX;
        y = startY;
        direction = dir;
    }

    public boolean inBounds(int sizeX, int sizeY, int length) {
        if (!inBounds(x, y, sizeX, sizeY)) {
            return false;
        }

        if (length > 1) {
            int endX = x + direction.x * (length-1);
            int endY = y + direction.y * (length-1);
            return inBounds(endX, endY, sizeX, sizeY);
        }
        return true;
    }

    private static boolean inBounds(int x, int y, int maxX, int maxY) {
        return (x >= 0 && y >= 0 && x < maxX && y < maxY);
    }
}
