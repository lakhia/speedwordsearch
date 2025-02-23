package com.creationsahead.speedwordsearch;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import org.greenrobot.eventbus.EventBus;
import static com.creationsahead.speedwordsearch.mod.Level.TIME_LEFT;

/**
 * A game manages the puzzle grid
 */
public class Game {
    @NonNull private final Grid grid;
    @NonNull private final Config config;
    @NonNull final AnswerMap answerMap;
    @NonNull private final RandomSequencer sequencer;
    @NonNull private final Trie dictionary;
    @NonNull private final SequenceIterator<Integer> clearEvents;
    @NonNull private final SequenceIterator<Integer> addEvents;
    private int letterCount;

    /**
     * Construct a game with a square puzzle grid
     */
    public Game(@NonNull Config config, @NonNull Trie dictionary,
                @NonNull RandomSequencer sequencer) {
        letterCount = 0;
        grid = new Grid(config.sizeX, config.sizeY, sequencer);
        answerMap = new AnswerMap();
        this.config = config;
        this.sequencer = sequencer;
        this.dictionary = dictionary;
        clearEvents = sequencer.getEventSequence(config.getFreqBasedOnSizeDifficulty(true), TIME_LEFT);
        addEvents = sequencer.getEventSequence(config.getFreqBasedOnSizeDifficulty(false), TIME_LEFT);
        EventBus.getDefault().post(this);
    }

    /**
     * Populate entire puzzle based on configuration
     */
    public void populatePuzzle() {
        int maxSize = config.sizeX;
        int minSize = config.sizeX - config.smallerWords;
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
    public void onTick(int ignoredTickCount) {
        clearPlaceholders(clearEvents.next());
        incrementallyAddWord(addEvents.next());
    }

    /**
     * Fill all the empty cells with random letters
     */
    public void fillEmptyCells() {
        final SequenceIterator<Character> iterator = sequencer.getLetterSequence();
        grid.findCells(Cell::isEmpty,
                (position) -> {
                    char letter = iterator.next();
                    if (!grid.addPlaceholderLetter(position, letter)) {
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
     * @param count clear count
     */
    public void clearPlaceholders(int count) {
        if (count < 1)
            return;
        final int[] letterCount = { count };
        grid.findCells(Cell::isNotEmpty,
                (position) -> {
                    if (grid.clearLetter(position)) {
                        --letterCount[0];
                    }
                    return letterCount[0] < 1;
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
                if (!answerMap.validate(result)) {
                    return false;
                }
                success.set(grid.addWord(selection, result));
                if (success.get()) {
                    answerMap.add(new Answer(selection, result));
                    letterCount += len;
                }
                return success.get();
            });
            return success.get();
        });
        return success.get();
    }

    public Answer findWordToAdd(int lenOffset) {
        AtomicReference<Answer> ans = new AtomicReference<>();
        grid.findUnusedSelection(config.sizeX - lenOffset, (selection, contents) -> {
            dictionary.searchWithWildcards(contents, sequencer, result -> {
                int len = result.length();
                if (len >= config.sizeX - lenOffset) {
                    ans.set(new Answer(selection, result));
                    return true;
                }
                return false;
            });
            return ans.get() != null;
        });
        return ans.get();
    }

    public void incrementallyAddWord(int letterCount) {
        int count = 0;
        for (int lenOffset = 1; lenOffset < 3; lenOffset++) {
            while (count < letterCount) {
                if (answerMap.hiddenAnswersEmpty()) {
                    Answer ans = findWordToAdd(lenOffset);
                    if (ans != null) {
                        answerMap.addHiddenAnswer(ans);
                    }
                }
                if (answerMap.addHiddenLetter(grid::addLetter)) {
                    count++;
                } else {
                    break;
                }
            }
        }
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
    @NonNull
    public ArrayList<Answer> getAnswers() {
        return answerMap.getAnswers(config.isWordListSorted);
    }

    /**
     * Validate a guess and mark word from grid as un-used
     * @return Guess object that indicates status of guess or null if selection is invalid
     */
    public Guess guess(int x1, int y1, int x2, int y2) {
        Selection selection = getSelection(x1, y1, x2, y2);
        if (selection == null) {
            return null;
        }
        String contents = grid.findContents(selection, false);
        Answer answer = answerMap.pop(contents);
        return grid.guess(answer, selection, answerMap.isSolved());
    }

    public Selection getSelection(int x1, int y1, int x2, int y2) {
        if (x1 < 0 || y1 < 0 || x2 < 0 || y2 < 0) {
            return null;
        }
        if (x1 >= config.sizeX || y1 >= config.sizeY || x2 >= config.sizeX || y2 >= config.sizeY) {
            return null;
        }
        return Selection.isValid(x1, y1, x2, y2);
    }

    @NonNull
    @Override
    public String toString() {
        return grid.toString();
    }
}
