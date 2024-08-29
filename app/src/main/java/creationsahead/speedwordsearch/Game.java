package creationsahead.speedwordsearch;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicReference;
import org.greenrobot.eventbus.EventBus;

/**
 * A game manages the puzzle grid
 */
public class Game {
    @NonNull private final Grid grid;
    @NonNull private final Config config;
    @NonNull private final RandomSequencer sequencer;
    @NonNull private final Trie dictionary;
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
        EventBus.getDefault().post(this);
    }

    /**
     * Populate entire puzzle based on configuration
     */
    public void populatePuzzle() {
        int maxSize = config.sizeX;
        int minSize = config.sizeX;
        while (letterCount < config.letterLimit) {
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
     * Periodic ticker for game to handle events
     */
    public void onTick(int tickCount) {
        if (tickCount % (config.difficulty + 5) == 4) {
            clearPlaceholders(1);
        }
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
        AtomicReference<Boolean> success = new AtomicReference<>(false);
        grid.findUnusedSelection(maxSize, (selection, contents) -> {
            dictionary.searchWithWildcards(contents, sequencer, result -> {
                int len = result.length();
                if (len < minSize) {
                    return false;
                }
                success.set(grid.addWord(selection, result));
                if (success.get()) {
                    letterCount += len;
                }
                return success.get();
            });
            return success.get();
        });
        return success.get();
    }

    public Answer findWordToAdd() {
        AtomicReference<Answer> ans = new AtomicReference<>();
        grid.findUnusedSelection(config.sizeX - 1, (selection, contents) -> {
            dictionary.searchWithWildcards(contents, sequencer, result -> {
                int len = result.length();
                if (len >= config.sizeX - 1) {
                    ans.set(new Answer(selection, result));
                    return true;
                }
                return false;
            });
            return ans.get() != null;
        });
        return ans.get();
    }

    /**
     * Get cell at coordinate x and y, assumes coordinates are within range
     */
    public Cell getCell(int x, int y) {
        return grid.getCell(x, y);
    }

    /**
     * Retrieve all the answers
     */
    public ArrayList<Answer> getAnswers() {
        Collection<Answer> values = grid.answerMap.values();
        ArrayList<Answer> list = new ArrayList<>(values);
        if (config.isWordListSorted) {
            Collections.sort(list, comparator);
        }
        return list;
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
