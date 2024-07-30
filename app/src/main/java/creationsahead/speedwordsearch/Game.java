package creationsahead.speedwordsearch;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import org.greenrobot.eventbus.EventBus;

/**
 * A game manages the puzzle grid
 */
public class Game {
    @NonNull private final Grid grid;
    @NonNull private final Config config;
    @NonNull private final RandomSequencer sequencer;
    @NonNull private final Trie dictionary;
    private boolean success;
    private int letterCount;
    private final static Comparator<Answer> comparator = (a, b) -> a.word.compareTo(b.word);

    /**
     * Construct a game with a square puzzle grid
     */
    public Game(@NonNull Config config, @NonNull Trie dictionary,
                @NonNull RandomSequencer sequencer) {
        letterCount = 0;
        grid = new Grid(config.sizeX, config.sizeY, sequencer);
        this.config = config;
        this.sequencer = sequencer;
        this.dictionary = dictionary;
    }

    /**
     * Populate entire puzzle based on configuration
     */
    public void populatePuzzle() {
        int letterLimit = config.letterLimit;
        int maxSize = config.sizeX;
        int minSize = config.sizeX;
        while (letterCount < letterLimit) {
            if (!addOneWord(minSize, maxSize)) {
                maxSize--;
                minSize--;
                if (minSize < 2) {
                    break;
                }
            }
        }
        fillEmptyCells();
    }

    /**
     * Fill all the empty cells with random letters
     */
    public void fillEmptyCells() {
        final SequenceIterator<Character> iterator = sequencer.getLetterSequence();
        grid.findCells(Cell::isEmpty,
                (position) -> {
                    char letter = iterator.next();
                    if (!grid.addLetter(position, letter)) {
                        throw new RuntimeException("Could not add letter");
                    }
                    if (!iterator.hasNext()) {
                        iterator.shuffle();
                    }
                    return false;
                });
    }

    /**
     * Clear placeholder cells
     * @param count clear count, must be at least 1
     */
    public void clearPlaceholders(int count) {
        final int[] letterCount = { count };
        grid.findCells(Cell::isNotEmpty,
                (position) -> {
                    if (grid.clearLetter(position)) {
                        --letterCount[0];
                    }
                    return letterCount[0] == 0;
                });
    }

    /**
     * Add one word that uses unused letter(s)
     * @param maxSize size of square grid
     * @return true if last iteration added at least 1 letter
     */
    public boolean addOneWord(final int minSize, final int maxSize) {
        success = false;
        grid.findUnusedSelection(maxSize, (selection, contents) -> {
            dictionary.searchWithWildcards(contents, sequencer, result -> {
                int len = result.length();
                if (len < minSize) {
                    return false;
                }
                success = grid.addWord(selection, result);
                if (success) {
                    letterCount += len;
                }
                return success;
            });
            return success;
        });
        return success;
    }

    /**
     * Get cell at coordinate x and y, assumes coordinates are within range
     */
    public Cell getCell(int x, int y) {
        return grid.getCell(x, y);
    }

    /**
     * Visit all the answers
     */
    public void visitAnswers() {
        Collection<Answer> values = grid.answerMap.values();
        ArrayList<Answer> list = new ArrayList<>(values);
        if (config.isWordListSorted) {
            Collections.sort(list, comparator);
        }
        for (Answer answer : list) {
            EventBus.getDefault().post(answer);
        }
    }

    /**
     * Validate a guess and mark word from grid as un-used
     * @param selection selection made by user
     * @return Guess object that indicates status of guess
     */
    public Guess guess(@NonNull Selection selection) {
        return grid.guess(selection);
    }

    @NonNull
    @Override
    public String toString() {
        return grid.toString();
    }
}
