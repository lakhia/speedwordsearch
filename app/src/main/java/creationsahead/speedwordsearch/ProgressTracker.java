package creationsahead.speedwordsearch;

/**
 * Tracks progress and manages instance of current game
 */
public class ProgressTracker {
    private static final int MAX_LEVEL = 10;
    private static int currentLevel = 0;
    private static Game currentGame;
    private static Config currentConfig;

    /**
     * Initialize progress tracker
     */
    public static void init() {
        WordList.init();
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

    private static void reset() {
        if (currentLevel > MAX_LEVEL) {
            currentGame = null;
            currentConfig = null;
        } else {
            currentConfig = new Config(currentLevel + 5,
                                       currentLevel + 5,
                                       WordList.mDictionary,
                                       5);
            currentGame = new Game(currentConfig);
        }
    }
}
