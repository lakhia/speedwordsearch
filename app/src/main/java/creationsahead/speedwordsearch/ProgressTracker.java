package creationsahead.speedwordsearch;

import androidx.annotation.NonNull;
import creationsahead.speedwordsearch.mod.Level;
import java.io.IOException;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Tracks progress and manages instance of current game
 */
public class ProgressTracker implements ScoreInterface {
    @NonNull public static final String LEVEL_VISIBLE = "levelVisible";
    private static final int MAX_LEVEL = 10;
    private static final int MAX_DIFFICULTY = 100;

    /** Current game's configuration */
    public Config config;
    /** Current game */
    public Game game;
    /** Levels */
    @NonNull public final Level[] levels = new Level[MAX_LEVEL];

    private int currentLevel;
    private int visibleLevel;
    private StorageInterface storageInterface;

    private static class SingletonHolder {
        @NonNull private final static ProgressTracker instance = new ProgressTracker();
    }
    private ProgressTracker() {}

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
        visibleLevel = storageInterface.getPreference(LEVEL_VISIBLE);

        for (int i = 0; i <= visibleLevel; i++) {
            levels[i] = storageInterface.getLevel(i);
        }

        EventBus.getDefault().register(this);

        try {
            WordList.init(storageInterface.getAssetInputStream("words_9k.db"));
            config = new Config(WordList.dictionary);
            resetConfig();
            // TODO: TEMP workaround
            game = new Game(config, this,
                            new RandomSequencer(config, (int) System.currentTimeMillis()));
        } catch (IOException ignored) {
        }
    }

    public void destroy() {
        EventBus.getDefault().unregister(this);
        game = null;
        for (int i = 0; i <= visibleLevel; i++) {
            levels[i] = null;
        }
    }

    /**
     * Get current level
     */
    @NonNull
    public Level getCurrentLevel() {
        return levels[currentLevel];
    }

    /**
     * User has finished current level
     * @param timeLeft Time left on clock
     */
    public void incrementLevel(int timeLeft) {
        // Store progress
        Level level = getCurrentLevel();
        int levelNum = level.number;

        scoreLevel(level);

        level.timeUsed = config.timeLimit - timeLeft;

        // Create new level or post game won event
        if (levelNum >= MAX_LEVEL - 1) {
            EventBus.getDefault().post(Event.GAME_WON);
        } else {
            levelNum++;
        }

        if (visibleLevel < levelNum) {
            visibleLevel = levelNum;
            resetConfig();
            storageInterface.storePreference(LEVEL_VISIBLE, visibleLevel);
        }

        storageInterface.storeLevel(level);
    }

    /**
     * Score level based on total score and time used
     */
    private void scoreLevel(@NonNull Level level) {
        float stars = 2.0f * level.score / level.totalScore;
        stars += 4.0f * (config.timeLimit - level.timeUsed) / config.timeLimit;
        if (stars > 4f) {
            level.stars = 4f;
        } else {
            level.stars = stars;
        }
    }

    /**
     * Get current score
     */
    public int getCurrentScore() {
        return getCurrentLevel().score;
    }

    /**
     * Score ranges from 5 to 10 points, and bigger words score less
     */
    @Override
    public int computeScore(@NonNull String word) {
        int score = 11 - word.length()/2;
        getCurrentLevel().totalScore += score;
        return score;
    }

    /**
     * Add score to current user score
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void addScore(@NonNull Answer answer) {
        if (answer.event == Event.SCORE_AWARDED) {
            getCurrentLevel().score += answer.score;
        }
    }

    /**
     * Start level
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void startLevel(@NonNull Level level) {
        currentLevel = level.number;
        resetConfig();
        getCurrentLevel().score = 0;
        getCurrentLevel().totalScore = 0;
        game = new Game(config, this,
                        new RandomSequencer(config, (int) System.currentTimeMillis()));
    }

    private void resetConfig() {
        config.difficulty = MAX_DIFFICULTY * currentLevel / MAX_LEVEL;
        config.sizeX = currentLevel + 4;
        config.sizeY = currentLevel + 4;
        config.timeLimit = 60 + (100 - config.difficulty) * 60 / 100;

        float letterRatio = (100 - config.difficulty) * 1.5f;
        letterRatio = config.sizeX * config.sizeY * letterRatio / 100;
        if (BuildConfig.DEBUG) {
            config.letterLimit = 3;
        } else if (letterRatio < 5) {
            config.letterLimit = 5;
        } else {
            config.letterLimit = (int) letterRatio;
        }

        if (levels[visibleLevel] == null) {
            levels[visibleLevel] = new Level("Basic Level " + (1 + visibleLevel),
                                             visibleLevel);
        }
    }
}
