package creationsahead.speedwordsearch;

import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import creationsahead.speedwordsearch.mod.Level;
import java.io.IOException;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Tracks progress and manages instance of current game
 */
public class ProgressTracker {
    @NonNull public static final String LEVEL_VISIBLE = "levelVisible";
    private static final int MAX_LEVEL = 10;
    private static final int MAX_DIFFICULTY = 100;

    /** Current game's configuration */
    public Config config;
    /** Current game */
    @Nullable public Game game;
    /** Levels */
    @NonNull public final Level[] levels = new Level[MAX_LEVEL];

    private int currentLevel;
    private int visibleLevel;
    private StorageInterface storageInterface;
    public Rect displayRect;
    public float normalizedFontSize;

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
     * @param display Size of entire display screen
     * @param fontSize Size of font to use for entire width of screen
     */
    public void init(@NonNull StorageInterface storage, @NonNull Rect display, float fontSize) {
        storageInterface = storage;
        visibleLevel = storageInterface.getPreference(LEVEL_VISIBLE);
        displayRect = display;
        normalizedFontSize = fontSize;

        for (int i = 0; i <= visibleLevel; i++) {
            levels[i] = storageInterface.getLevel(i);
        }

        EventBus.getDefault().register(this);

        try {
            WordList.init(storageInterface.getAssetInputStream("words_9k.db"));
        } catch (IOException ignored) {
        }
        resetConfig();
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
     * @param event Event that caused win / lose
     */
    public void incrementLevel(@NonNull Event event) {
        // Store progress
        Level level = getCurrentLevel();
        int levelNum = level.number;
        level.score(event.timeLeft);

        if (level.stars > 2.0) {
            // Create new level or post game won event
            if (levelNum >= MAX_LEVEL - 1) {
                EventBus.getDefault().post(Event.GAME_WON);
            } else {
                levelNum++;
            }
            if (visibleLevel < levelNum) {
                visibleLevel = levelNum;
                storageInterface.storePreference(LEVEL_VISIBLE, visibleLevel);
                resetConfig();
            }
        }

        storageInterface.storeLevel(level);
    }

    /**
     * Start level
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void startLevel(@NonNull Level level) {
        level.score = 0;
        level.totalScore = 0;
        currentLevel = level.number;
        resetConfig();
        game.populatePuzzle();
    }
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void addAnswer(@NonNull Answer answer) {
        getCurrentLevel().totalScore += answer.score;
    }

    private void resetConfig() {
        int letterLimit;
        if (BuildConfig.DEBUG) {
            letterLimit = 13;
        } else {
            float letterRatio = (100 - config.difficulty) * 1.5f;
            letterRatio = config.sizeX * config.sizeY * letterRatio / 100;
            if (letterRatio < 5) {
                letterLimit = 5;
            } else {
                letterLimit = (int) letterRatio;
            }
        }
        config = new Config(currentLevel + 4, currentLevel + 4, letterLimit);
        config.difficulty = MAX_DIFFICULTY * currentLevel / MAX_LEVEL;

        if (levels[visibleLevel] == null) {
            levels[visibleLevel] = new Level("Basic Level " + (1 + visibleLevel),
                                             visibleLevel);
        }
        game = new Game(config, WordList.dictionary,
                new RandomSequencer(config, (int) System.currentTimeMillis()));
    }
}
