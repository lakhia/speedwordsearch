package com.creationsahead.speedwordsearch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Indicates a selection in a grid
 */
public class Selection {
    public final Position position;
    public final Direction direction;
    public final int length;

    @NonNull
    @Override
    public String toString() {
        return "pos: " + position.toString() + ", dir: " + direction + ", len: " + length;
    }

    public Selection(int x, int y, Direction dir, int len) {
        position = new Position(x, y);
        direction = dir;
        length = len;
    }

    public Selection(Position pos, Direction dir, int len) {
        position = pos;
        direction = dir;
        length = len;
    }

    public boolean inBounds(int sizeX, int sizeY) {
        return Selection.inBounds(position, direction, sizeX, sizeY, length);
    }

    public static boolean inBounds(@NonNull Position position, @NonNull Direction direction,
                                   int sizeX, int sizeY, int length) {
        if (!position.inBounds(sizeX, sizeY)) {
            return false;
        }
        if (length > 1) {
            int endX = position.x + direction.x * (length - 1);
            int endY = position.y + direction.y * (length - 1);
            return Position.inBounds(endX, endY, sizeX, sizeY);
        }
        return true;
    }

    /**
     * Returns a Selection if valid start and end Pos.
     * Note that start and end points are inclusive
     */
    @Nullable
    public static Selection isValid(int startX, int startY, int endX, int endY) {
        int diffX = endX - startX;
        int diffY = endY - startY;
        int len;
        if (diffX == 0 && diffY == 0) {
            return null;
        } else if (diffX == 0) {
            len = Math.abs(diffY);
            diffY = (diffY > 0 ? 1 : -1);
        } else if (diffY == 0) {
            len = Math.abs(diffX);
            diffX = (diffX > 0 ? 1 : -1);
        } else {
            len = Math.abs(diffX);
            if (Math.abs(diffY) != len) {
                return null;
            }
            diffX /= len;
            diffY /= len;
        }
        return new Selection(startX, startY, Direction.find(diffX, diffY), len+1);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Selection that) {
            if (that.length != length) {
                return false;
            }
            if (that.direction == direction && that.position.equals(position)) {
                return true;
            }
            if (that.direction.negate() == direction) {
                return that.position.equals(position.x + direction.x * (length -1),
                                            position.y + direction.y * (length -1));
            }
        }
        return false;
    }
}