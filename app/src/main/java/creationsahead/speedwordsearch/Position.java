package creationsahead.speedwordsearch;

/**
 * Indicates a position in a grid
 */
public class Position {
    public final int x, y;

    public Position(int xVal, int yVal) {
        x = xVal;
        y = yVal;
    }

    public boolean inBounds(int sizeX, int sizeY) {
         return Position.inBounds(x, y, sizeX, sizeY);
    }

    public static boolean inBounds(int x, int y, int maxX, int maxY) {
        return (x >= 0 && y >= 0 && x < maxX && y < maxY);
    }

    public int getDistanceToBoundary(Direction direction, int maxX, int maxY) {
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Position) {
            Position that = (Position) obj;
            return (that.x == x && that.y == y);
        }
        return false;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public boolean equals(int xVal, int yVal) {
        return x == xVal && y == yVal;
    }
}
