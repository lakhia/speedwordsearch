package creationsahead.speedwordsearch;

import android.support.annotation.NonNull;
import org.greenrobot.eventbus.EventBus;

/**
 * A game manages the puzzle grid
 */
public class Game implements TickerCallback {
    @NonNull private final PuzzleGrid grid;
    @NonNull private final Config config;
    @NonNull private final Sequencer sequencer;
    private boolean success;
    private int letterCount;

    /**
     * Construct a game with a square puzzle grid
     */
    public Game(@NonNull Config config, @NonNull ScoreInterface scoreInterface,
                @NonNull Sequencer sequencer) {
        letterCount = 0;
        grid = new PuzzleGrid(config, scoreInterface, sequencer);
        this.config = config;
        this.sequencer = sequencer;
    }

    /**
     * Called when a second has elapsed
     */
    @Override
    public void onTick(int tickCount) {
        // TODO: convert tick to events, and unregister on win
        if (grid.answerMap.isEmpty()) {
            return;
        }
        if (tickCount % 20 == 5) {
            tickCount /= 20;
            int bonusVal = sequencer.getBonus(tickCount);
            Bonus bonus = Bonus.find(bonusVal);
            if (bonus != null) {
                grid.assignBonus(bonus);
            }
        } else if (tickCount % 20 == 0) {
            grid.removeLastBonus();
        }
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

        grid.findUnusedCells((position) -> {
            // Ignore placeholder cells
            if (!grid.getCell(position.x, position.y).isEmpty()) {
                return false;
            }
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
        grid.findUnusedCells((position) -> {
            // Ignore empty cells
            if (grid.getCell(position.x, position.y).isEmpty()) {
                return false;
            }
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
            config.dictionary.searchWithWildcards(contents, sequencer, result -> {
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
        for (Answer answer : grid.answerMap.values()) {
            answer.event = Event.WORD_ADDED;
            EventBus.getDefault().post(answer);
        }
    }

    /**
     * Validate a guess and mark word from grid as un-used
     * @param selection selection made by user
     * @return true if word was removed
     */
    public boolean guess(@NonNull Selection selection) {
        // Use placeholder letters to find word so that accidental words are found
        String answer = grid.findContents(selection, false);
        boolean success = grid.removeWord(answer);
        Event event = Event.SCORE_AWARDED;
        event.lastWordGuessed = grid.answerMap.isEmpty();
        EventBus.getDefault().post(event);
        return success;
    }

    @NonNull
    @Override
    public String toString() {
        return grid.toString();
    }
}
