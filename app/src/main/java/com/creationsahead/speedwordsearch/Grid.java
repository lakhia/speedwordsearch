package com.creationsahead.speedwordsearch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import org.greenrobot.eventbus.EventBus;

/**
 * Grid to manage the puzzle
 */
public class Grid {
    @NonNull private final Cell[][] cells;
    private final int sizeX, sizeY;
    @NonNull private final RandomSequencer mSequencer;

    /**
     * Create a grid using specified x and y size
     */
    public Grid(int sizeX, int sizeY, @NonNull RandomSequencer sequencer) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        mSequencer = sequencer;
        cells = new Cell[sizeX][sizeY];
        for (int x=0; x < sizeX; x++) {
            for (int y=0; y < sizeY; y++) {
                cells[x][y] = new Cell();
            }
        }
    }

    /**
     * Add a letter that is used by an answer
     * @param position where letter is inserted
     * @param letter letter to be added
     * @return true if store was successful
     */
    public boolean addLetter(@NonNull Position position, char letter) {
        return cells[position.x][position.y].store(letter);
    }

    /**
     * Add a letter as placeholder
     * @param position where letter is inserted
     * @param letter letter to be added
     * @return true if store was successful
     */
    public boolean addPlaceholderLetter(@NonNull Position position, char letter) {
        return cells[position.x][position.y].storePlaceholder(letter);
    }

    /**
     * Clear letter if it is a placeholder
     * @param position where letter is cleared
     * @return true if clear was successful
     */
    public boolean clearLetter(@NonNull Position position) {
        return cells[position.x][position.y].clear();
    }

    /**
     * Add word to grid in specified direction and coordinates
     * @return True if successfully added
     * @throws RuntimeException if word is partially added
     */
    public boolean addWord(@NonNull Selection selection, @NonNull String word) {
        // Check bounds
        if (!selection.inBounds(sizeX, sizeY)) {
            return false;
        }

        if (selection.length < word.length()) {
            throw new RuntimeException("Inconsistent length");
        }

        // Check overwriting letters
        Direction dir = selection.direction;
        for (int x = selection.position.x, y = selection.position.y, i=0; i < word.length(); i++) {
            if (!cells[x][y].store(word.charAt(i))) {
                throw new RuntimeException("Board in inconsistent state, word partially inserted");
            }
            x += dir.x;
            y += dir.y;
        }
        return true;
    }

    /**
     * Remove answer from grid
     * @throws RuntimeException if word is partially removed
     */
    public void removeWord(@NonNull Answer answer) {
        int len = answer.word.length() - 1;
        Selection selection = answer.selection;
        Position pos = selection.position;
        for (int x = pos.x, y = pos.y, i=0; i <= len; i++) {
            if (!cells[x][y].erase(answer.word.charAt(i))) {
                throw new RuntimeException("Board in inconsistent state, word not stored in previous step");
            }
            x += selection.direction.x;
            y += selection.direction.y;
        }
    }

    /**
     * Find an empty cell or placeholder cells based on randomness
     * @param cellCallback called for each cell to identify if it meets criteria
     * @param positionCallback called for each valid position
     */
    public void findCells(@NonNull CellCallback cellCallback,
                          @NonNull PositionCallback positionCallback) {
        SequenceIterator<Integer> rows = mSequencer.getXCoordinateSequence();
        SequenceIterator<Integer> cols = mSequencer.getYCoordinateSequence();
        while (rows.hasNext()) {
            int x = rows.next();
            while (cols.hasNext()) {
                int y = cols.next();
                if (cellCallback.callback(cells[x][y])) {
                    Position newPos = new Position(x, y);
                    if (positionCallback.onUpdate(newPos)) {
                        return;
                    }
                }
            }
            cols.shuffle();
        }
    }


    /**
     * Find a selection that starts with an unused cell and is of size length
     * @param length length of potential word
     * @param callback called for each assignment that is possible
     */
    public void findUnusedSelection(final int length, @NonNull final SelectionCallback callback) {
        SequenceIterator<Direction> dirs = mSequencer.getDirectionSequence();
        findCells(Cell::isUnused,
                (position) -> {
                    while (dirs.hasNext()) {
                        Direction dir = dirs.next();
                        // If position can accommodate length, process it
                        if (Selection.inBounds(position, dir, sizeX, sizeY, length)) {
                            Selection selection = new Selection(position, dir, length);
                            return callback.onUpdate(selection, findContents(selection, true));
                        }
                    }
                    dirs.shuffle();
                    return false;
                });
    }

    /**
     * Returns content stored in grid at specified selection
     * @param searchValue if true, placeholders become blanks
     * @return String with blanks and letters
     */
    @NonNull
    public String findContents(@NonNull Selection selection, boolean searchValue) {
        int x = selection.position.x, y = selection.position.y;
        StringBuilder result = new StringBuilder();
        Direction dir = selection.direction;

        for (int i = 0; i < selection.length; i++) {
            char letter;
            if (searchValue) {
                letter = cells[x][y].getSearchValue();
            } else {
                letter = cells[x][y].letter;
            }
            result.append(letter);
            x += dir.x;
            y += dir.y;
        }
        return result.toString();
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y=0; y < sizeY; y++) {
            for (Cell[] cells: cells) {
                sb.append(cells[y]).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Visit selection and return an array of tags for each cell
     */
    @NonNull
    private ArrayList<Object> createTagsFromSelection(@NonNull Selection selection) {
        ArrayList<Object> arrayList = new ArrayList<>();
        for (int x = selection.position.x, y = selection.position.y, i=0; i < selection.length; i++) {
            arrayList.add(cells[x][y].tag);
            x += selection.direction.x;
            y += selection.direction.y;
        }
        return arrayList;
    }

    /**
     * Get cell at coordinate x and y, assumes coordinates are within range
     */
    public Cell getCell(int x, int y) {
        return cells[x][y];
    }

    @NonNull
    public Guess guess(@Nullable Answer answer, @NonNull Selection selection, boolean solved) {
        if (answer != null) {
            removeWord(answer);
        }
        // Create guess event
        Guess guess = new Guess(createTagsFromSelection(selection), answer, solved);
        EventBus.getDefault().post(guess);
        return guess;
    }
}
