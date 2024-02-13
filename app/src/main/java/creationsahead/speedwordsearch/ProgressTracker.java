package creationsahead.speedwordsearch;

import android.support.annotation.NonNull;
import creationsahead.speedwordsearch.mod.Level;
import java.io.IOException;
import java.util.ArrayList;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Tracks progress and manages instance of current game
 */
public class ProgressTracker implements ScoreInterface {
    @NonNull public static final String LEVEL = "level";
    @NonNull public static final String LEVEL_VISIBLE = "levelVisible";
    private static final int MAX_LEVEL = 10;
    private static final int MAX_DIFFICULTY = 100;

    /** Current game's configuration */
    public Config config;
    /** Current game */
    public Game game;
    /** Levels */
    @NonNull public ArrayList<Level> levels = new ArrayList<>();

    private int currentLevel = 0;
    private int visibleLevel = 0;
    private static StorageInterface storageInterface;

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
     * TODO: Use background thread
     */
    public void init(@NonNull StorageInterface storage) {
        storageInterface = storage;
        currentLevel = storageInterface.getPreference(LEVEL);
        visibleLevel = storageInterface.getPreference(LEVEL_VISIBLE);

        for (int i = 0; i <= visibleLevel; i++) {
            levels.add(i, storageInterface.getLevel(i));
            createLevelIfNeeded(i);
        }

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
     * @param timeLeft Time left on clock
     */
    public void incrementLevel(int timeLeft) {
        // Store progress
        Level level = levels.get(currentLevel);
        // TODO: Scoring needs work
        level.stars = 4 * level.score / 200.0f;
        level.timeUsed = config.timeLimit - timeLeft;

        // Create new level or post game won event
        if (currentLevel > MAX_LEVEL) {
            EventBus.getDefault().post(Event.GAME_WON);
        } else {
            currentLevel++;
            storageInterface.storeLevel(level);
            reset();
        }

        storageInterface.storePreference(LEVEL, currentLevel);
        storageInterface.storePreference(LEVEL_VISIBLE, visibleLevel);
    }

    /**
     * Get current score
     */
    public int getCurrentScore() {
        return levels.get(currentLevel).score;
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
    public void addScore(@NonNull Answer answer) {
        if (answer.event == Event.SCORE_AWARDED) {
            levels.get(currentLevel).score += answer.score;
        }
    }

    /**
     * Start level
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void startLevel(@NonNull Event event) {
        if (event == Event.LEVEL_STARTED) {
            currentLevel = event.levelNumber;
            reset();
        }
    }

    private void reset() {
        if (currentLevel <= MAX_LEVEL) {
            createLevelIfNeeded(currentLevel);
            config = new Config(currentLevel + 4,
                                currentLevel + 4,
                                WordList.dictionary,
                                MAX_DIFFICULTY * currentLevel / MAX_LEVEL);
            game = new Game(config,
                            this,
                            new RandomSequencer(config, (int) System.currentTimeMillis()));
        }
        if (visibleLevel < currentLevel) {
            visibleLevel = currentLevel;
        }
    }

    private void createLevelIfNeeded(int levelNum) {
        if (levels.size() <= levelNum) {
            levels.add(levelNum, null);
        }
        if (levels.get(levelNum) == null) {
            levels.set(levelNum, new Level("Easy level " + (levelNum + 1),
                                           levelNum));
        }
    }
}
