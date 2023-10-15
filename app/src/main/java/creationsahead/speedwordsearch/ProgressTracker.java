package creationsahead.speedwordsearch;

/**
 * Tracks progress and manages instance of current game
 */
public class ProgressTracker {
    public static final String SCORE = "score";
    public static final String LEVEL = "level";
    /** Current game */
    public static Game game;
    /** Current game's configuration */
    public static Config config;

    private static final int MAX_LEVEL = 10;
    private static int currentLevel = 0;
    private static int currentScore = 0;
    private static StorageInterface storageInterface;

    /**
     * Initialize progress tracker and use storage interface to load words
     * @param storageInterface InputStream of words to load
     */
    public static void init(StorageInterface storageInterface, int seed) {
        ProgressTracker.storageInterface = storageInterface;
        currentScore = storageInterface.getPreference(SCORE);
        currentLevel = storageInterface.getPreference(LEVEL);
        WordList.init(storageInterface.getAssetInputStream("words_9k.db"));

        reset(seed);
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
    public static void incrementLevel(int seed) {
        currentLevel++;
        storageInterface.storePreference(LEVEL, currentLevel);
        storageInterface.storePreference(SCORE, currentScore);
        reset(seed);
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

    private static void reset(int seed) {
        if (currentLevel > MAX_LEVEL) {
            game = null;
            config = null;
        } else {
            config = new Config(currentLevel + 5,
                                currentLevel + 5,
                                WordList.dictionary,
                                seed);
            game = new Game(config);
        }
    }
}
