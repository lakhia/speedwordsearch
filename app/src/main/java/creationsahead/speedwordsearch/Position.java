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
        if (!Position.inBounds(x, y, sizeX, sizeY)) {
            return false;
        }

        if (length > 1) {
            int endX = x + direction.x * (length-1);
            int endY = y + direction.y * (length-1);
            return Position.inBounds(endX, endY, sizeX, sizeY);
        }
        return true;
    }

    public static boolean inBounds(int x, int y, int maxX, int maxY) {
        return (x >= 0 && y >= 0 && x < maxX && y < maxY);
    }

    public int getDistanceToBoundary(int maxX, int maxY) {
        int xDist = maxX, yDist = maxY;
        if (direction.x == 1) {
            xDist = maxX - x;
        } else if (direction.x == -1) {
            xDist = x;
        }
        if (direction.y == 1) {
            yDist = maxY - y;
        } else if (direction.y == -1) {
            yDist = y;
        }
        return Math.min(xDist, yDist);
    }
}
