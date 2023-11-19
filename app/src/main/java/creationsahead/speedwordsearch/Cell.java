package creationsahead.speedwordsearch;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * A cell in a puzzle grid contains a letter and reference count
 */
public class Cell {
    @Nullable public static CellCallback callback;
    public static final char EMPTY = '.';
    public char letter;
    @Nullable public Object tag;
    private int refCount;

    /**
     * Create a cell, defaults to empty
     */
    Cell() {
        this.letter = EMPTY;
        refCount = 0;
        tag = null;
    }

    /**
     * Erase letter's use but do not clear the contents
     * @return true if erase was successful
     */
    public boolean erase(char letter) {
        if (this.letter == letter) {
            refCount--;
            if (refCount < 0) {
                throw new RuntimeException("Erase occurred on placeholder");
            }
            return true;
        }
        return false;
    }

    /**
     * Clear letter content
     */
    public boolean clear() {
        if (refCount == 0) {
            letter = EMPTY;
            if (callback != null) {
                callback.onChanged(this);
            }
            return true;
        }
        return false;
    }

    /**
     * Store letter that is part of puzzle
     * @return true if store was successful
     */
    public boolean store(char letter) {
        if (storePlaceholder(letter)) {
            refCount++;
            return true;
        }
        return false;
    }

    /**
     * Store placeholder which is a letter that is not used in grid
     * @return true if store was successful
     */
    public boolean storePlaceholder(char letter) {
        if (this.letter == letter) {
            return true;
        }
        if (!isUnused()) {
            return false;
        }
        this.letter = letter;
        if (callback != null) {
            callback.onChanged(this);
        }
        return true;
    }

    /**
     * Returns true if cell content is unused and can be overwritten
     */
    public boolean isUnused() {
        return letter == EMPTY || refCount == 0;
    }

    /**
     * Returns true if cell content is blank
     */
    public boolean isEmpty() {
        return letter == EMPTY;
    }

    /**
     * Returns display value of cell
     */
    @NonNull
    @Override
    public String toString() {
        return letter + " ";
    }

    /**
     * Return search value of cell (placeholders become wildcards)
     */
    public char getSearchValue() {
        return isUnused() ? EMPTY : letter;
    }
}
