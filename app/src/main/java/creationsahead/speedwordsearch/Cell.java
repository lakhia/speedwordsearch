package creationsahead.speedwordsearch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import org.greenrobot.eventbus.EventBus;

/**
 * A cell in a puzzle grid contains a letter and reference count
 */
public class Cell {
    public static final char EMPTY = '.';
    public char letter;
    @Nullable public Object tag;
    @Nullable public Event event;
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
     * Clear letter content unless it is being referenced
     * @return True if clear was successful
     */
    public boolean clear() {
        if (refCount == 0) {
            if (letter != EMPTY) {
                letter = EMPTY;
                EventBus.getDefault().post(this);
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
        EventBus.getDefault().post(this);
        return true;
    }

    /**
     * Returns true if cell content is unused and can be overwritten
     */
    public boolean isUnused() {
        return refCount == 0;
    }

    /**
     * Returns true if cell content is blank
     */
    public boolean isEmpty() {
        return letter == EMPTY;
    }

    /**
     * Returns true if cell content can become a blank
     */
    public boolean isNotEmpty() {
        return refCount == 0 && letter != EMPTY;
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
