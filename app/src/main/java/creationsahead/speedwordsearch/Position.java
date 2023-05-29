package creationsahead.speedwordsearch;

/**
 * Indicates position of a word in a grid
 */
public class Position {
    public final Direction direction;
    public final int x, y;

    public Position(Direction dir, int startX, int startY) {
        direction = dir;
        x = startX;
        y = startY;
    }
}
