package com.creationsahead.speedwordsearch;

import androidx.annotation.NonNull;

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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Position that) {
            return (that.x == x && that.y == y);
        }
        return false;
    }

    @NonNull
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public boolean equals(int xVal, int yVal) {
        return x == xVal && y == yVal;
    }

    @Override
    public int hashCode() {
        return x * 31 + y;
    }
}
