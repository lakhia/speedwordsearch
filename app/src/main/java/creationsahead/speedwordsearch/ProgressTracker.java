package creationsahead.speedwordsearch;

import java.io.InputStream;

/**
 * Tracks progress and manages instance of current game
 */
public class ProgressTracker {
    private static final int MAX_LEVEL = 10;
    private static int currentLevel = 0;
    private static int currentScore = 0;
    private static Game currentGame;
    private static Config currentConfig;

    /**
     * Initialize progress tracker and load dictionary of words
     * @param inputStream InputStream of words to load
     */
    public static void init(InputStream inputStream) {
        WordList.init(inputStream);
        reset();
    }

    /**
     * Get current configuration
     */
    public static Config getCurrentConfig() {
        return currentConfig;
    }

    /**
     * Get current game instance
     */
    public static Game getCurrentGame() {
        return currentGame;
    }

    /**
     * Get current level number
     */
    public static int getCurrentLevel() {
        return currentLevel;
    }

    /**
     * User has finished current level
     */
    public static void incrementLevel() {
        currentLevel++;
        reset();
    }

    /**
     * Get current score
     */
    public static int getCurrentScore() {
        return currentScore;
    }

    /**
     * Score ranges from 5 to 10 points, and bigger words score less
     */
    public static int computeScore(String word) {
        return 11 - word.length()/2;
    }

    /**
     * Add score to current user score
     */
    public static void addScore(int score) {
        currentScore += score;
    }

    private static void reset() {
        if (currentLevel > MAX_LEVEL) {
            currentGame = null;
            currentConfig = null;
        } else {
            currentConfig = new Config(currentLevel + 5,
                                       currentLevel + 5,
                                       WordList.dictionary,
                                       5);
            currentGame = new Game(currentConfig);
        }
    }
}
