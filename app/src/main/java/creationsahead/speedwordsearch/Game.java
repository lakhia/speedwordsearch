package creationsahead.speedwordsearch;

import android.support.annotation.NonNull;

/**
 * A game manages the puzzle grid
 */
public class Game {
    @NonNull private final PuzzleGrid grid;
    @NonNull private final Config config;
    private boolean success;
    private int letterCount;

    /**
     * Construct a game with a square puzzle grid
     */
    public Game(@NonNull Config config, @NonNull ScoreInterface scoreInterface) {
        letterCount = 0;
        grid = new PuzzleGrid(config, scoreInterface);
        this.config = config;
    }

    /**
     * Populate entire puzzle based on configuration
     */
    public void populatePuzzle() {
        int letterLimit = config.sizeX * config.sizeY * config.letterRatio / 100;
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
        // Need indirection to allow modification in lambda function
        final int[][] letterSeq = { config.sequencer.getNextLetterSequence() };
        final int[] index = { 0 };
        grid.findEmptyCell((position) -> {
            char letter = (char) ('A' + letterSeq[0][index[0]]);
            if (!grid.addLetter(position, letter)) {
                throw new RuntimeException("Could not add letter");
            }
            index[0]++;
            if (index[0] >= letterSeq[0].length) {
                index[0] = 0;
                letterSeq[0] = config.sequencer.getNextLetterSequence();
            }
            return false;
        });
    }

    /**
     * Populate an empty puzzle
     * @param maxSize size of square grid
     * @return true if last iteration added at least 1 letter
     */
    public boolean addOneWord(final int minSize, final int maxSize) {
        success = false;
        grid.findEmptyCell(maxSize, (selection, contents) -> {
            config.dictionary.searchWithWildcards(contents, config.sequencer, result -> {
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
    public void visitAllAnswers(AnswerCallback callback) {
        for (Answer answer : grid.answerMap.values()) {
            callback.onUpdate(answer);
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
        return grid.removeWord(answer);
    }

    @Override
    public String toString() {
        return grid.toString();
    }
}
