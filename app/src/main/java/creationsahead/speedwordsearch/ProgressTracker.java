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

    /** Current game's configuration */
    public Config config;
    /** Current game */
    @Nullable public Game game;
    /** Levels */
    @NonNull public final Level[] levels = new Level[MAX_LEVEL];

    private Level currentLevel;
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
        if (levels[visibleLevel] == null) {
            levels[visibleLevel] = new Level("Basic Level " + (1 + visibleLevel),
                    visibleLevel);
        }

        EventBus.getDefault().register(this);

        try {
            WordList.init(storageInterface.getAssetInputStream("words_9k.db"));
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
        return currentLevel;
    }

    /**
     * User has finished current level
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void stopLevel(@NonNull Level level) {
        // Store progress
        level.score();
        if (level.stars < 0) {
            return;
        }
        int levelNum = level.number;

        if (level.won) {
            // Create new level or post game won event
            if (levelNum >= MAX_LEVEL - 1) {
                // TODO: Game won!
            } else {
                levelNum++;
            }
            if (visibleLevel < levelNum) {
                visibleLevel = levelNum;
                if (levels[visibleLevel] == null) {
                    levels[visibleLevel] = new Level("Basic Level " + (1 + visibleLevel),
                            visibleLevel);
                }
                storageInterface.storePreference(LEVEL_VISIBLE, visibleLevel);
            }
        }

        storageInterface.storeLevel(level);
    }

    /**
     * Start Game
     */
    public void createGame(@NonNull Level level) {
        currentLevel = level;
        currentLevel.totalScore = 0;
        currentLevel.score = 0;
        config = new Config(currentLevel.number);
        game = new Game(config, WordList.dictionary,
                new RandomSequencer(config, (int) System.currentTimeMillis()));
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void startGame(@NonNull Game game) {
        game.populatePuzzle();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void addAnswer(@NonNull Answer answer) {
        getCurrentLevel().totalScore += answer.score;
    }
}
