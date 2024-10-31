package com.creationsahead.speedwordsearch;

import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.creationsahead.speedwordsearch.mod.Level;
import java.io.IOException;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Tracks progress and manages instance of current game
 */
public class ProgressTracker {
    /** Current game's configuration */
    public Config config;
    /** Current game */
    @Nullable public Game game;

    public Level currentLevel;
    public Rect displayRect;
    public float normalizedFontSize;

    @NonNull public Game getGame() {
        if (game == null) {
            Level level = LevelTracker.levels.get(LevelTracker.levels.size() - 1);
            ProgressTracker.getInstance().createGame(level);
        }
        return game;
    }

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
     * @param storage Interface to interact with storage
     * @param display Size of entire display screen
     * @param fontSize Size of font to use for entire width of screen
     */
    public void init(@NonNull StorageInterface storage, @NonNull Rect display, float fontSize) {
        LevelTracker.init(storage, "Beginner");
        displayRect = display;
        normalizedFontSize = fontSize;

        EventBus.getDefault().register(this);

        try {
            WordList.init(storage.getAssetInputStream("words_9k.db"));
        } catch (IOException ignored) {
        }
    }

    public void destroy() {
        EventBus.getDefault().unregister(this);
        game = null;
    }

    /**
     * User has finished current level
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void stopLevel(@NonNull Level level) {
        // Store progress
        LevelTracker.store(level);
    }

    /**
     * Start Game
     */
    public void createGame(@NonNull Level level) {
        currentLevel = level;
        currentLevel.totalScore = 0;
        currentLevel.score = 0;
        currentLevel.timeUsed = 0;
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
        currentLevel.totalScore += answer.score;
    }
}
