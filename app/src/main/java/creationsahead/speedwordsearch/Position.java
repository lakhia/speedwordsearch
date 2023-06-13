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
}
