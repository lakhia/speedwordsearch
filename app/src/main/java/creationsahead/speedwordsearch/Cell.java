package creationsahead.speedwordsearch;

/**
 * A cell in a puzzle grid contains a letter and reference count
 */
public class Cell {
    public static final char EMPTY = '.';
    public char letter;
    private int refCount;

    public Cell() {
        this(EMPTY);
    }

    public Cell(char letter) {
        this.letter = letter;
        refCount = 0;
    }

    /**
     * Erase letter and switch to empty cell if not used
     * @return true if erase was successful
     */
    public boolean erase(char letter) {
        if (this.letter == letter) {
            refCount--;
            if (refCount <= 0) {
                this.letter = EMPTY;
                refCount = 0;
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
        if (isUnused()) {
            this.letter = letter;
            return true;
        }
        return false;
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
    @Override
    public String toString() {
        return letter + " ";
    }

    /**
     * Return search value of cell (placeholders become wildcards
     */
    public char getSearchValue() {
        return isUnused() ? EMPTY : letter;
    }
}
