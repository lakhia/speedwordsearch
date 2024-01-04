package creationsahead.speedwordsearch;

import android.support.annotation.NonNull;
import java.io.IOException;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Tracks progress and manages instance of current game
 */
public class ProgressTracker implements ScoreInterface {
    public static final String SCORE = "score";
    public static final String LEVEL = "level";
    public static final int MAX_LEVEL = 10;
    public static final int MAX_DIFFICULTY = 100;

    /** Current game's configuration */
    public Config config;
    /** Current game */
    public Game game;

    private int currentLevel = 0;
    private int currentScore = 0;
    private static StorageInterface storageInterface;

    private static class SingletonHolder {
        private final static ProgressTracker instance = new ProgressTracker();
    }
    private ProgressTracker() {
        reset();
    }

    /**
     * Get singleton instance
     */
    @NonNull
    public static ProgressTracker getInstance() {
        return SingletonHolder.instance;
    }

    /**
     * Initialize progress tracker and use storage interface to load words
     * @param storage InputStream of words to load
     */
    public void init(@NonNull StorageInterface storage) {
        storageInterface = storage;
        currentScore = storageInterface.getPreference(SCORE);
        currentLevel = storageInterface.getPreference(LEVEL);
        EventBus.getDefault().register(this);
        try {
            WordList.init(storageInterface.getAssetInputStream("words_9k.db"));
            reset();
        } catch (IOException ignored) {
        }
    }

    public void destroy() {
        EventBus.getDefault().unregister(this);
    }

    /**
     * Get current level number
     */
    public int getCurrentLevel() {
        return currentLevel;
    }

    /**
     * User has finished current level
     * TODO: Do not go past max level
     */
    public void incrementLevel() {
        currentLevel++;
        storageInterface.storePreference(LEVEL, currentLevel);
        storageInterface.storePreference(SCORE, currentScore);
        reset();
    }

    /**
     * Get current score
     */
    public int getCurrentScore() {
        return currentScore;
    }

    /**
     * Score ranges from 5 to 10 points, and bigger words score less
     */
    @Override
    public int computeScore(@NonNull String word) {
        return 11 - word.length()/2;
    }

    /**
     * Add score to current user score
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void addScore(Answer answer) {
        if (answer.event == Event.SCORE_AWARDED) {
            currentScore += answer.score;
        }
    }

    private void reset() {
        if (currentLevel < MAX_LEVEL) {
            config = new Config(currentLevel + 5,
                                currentLevel + 5,
                                WordList.dictionary,
                                MAX_DIFFICULTY * currentLevel / MAX_LEVEL);
            game = new Game(config,
                            this,
                            new RandomSequencer(config, (int) System.currentTimeMillis()));
        }
    }
}
