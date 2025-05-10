package com.creationsahead.speedwordsearch;

import static java.lang.Thread.sleep;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.creationsahead.speedwordsearch.mod.Level;
import com.creationsahead.speedwordsearch.mod.SubLevel;
import java.io.IOException;
import java.io.Reader;
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

    private SubLevel currentSubLevel;
    public Level currentLevel;
    public Rect displayRect;
    public float normalizedFontSize;

    @NonNull public Game getGame() {
        if (game == null) {
            if (currentLevel != null) {
                createGame(currentLevel);
            } else {
                wait_for_loading_thread();
                currentSubLevel = LevelTracker.subLevels.get(0);
                createGame(currentSubLevel.levels.get(0));
            }
        }
        return game;
    }

    private void wait_for_loading_thread() {
        int timeCounter = 1;
        while (LevelTracker.loaded()) {
            try {
                sleep(100);
                timeCounter++;
                if (timeCounter > 20) {
                    throw new RuntimeException("Could not load levels");
                }
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public SubLevel getCurrentSubLevel() {
        if (currentSubLevel == null) {
            if (LevelTracker.subLevels.isEmpty()) {
                wait_for_loading_thread();
            }
            currentSubLevel = LevelTracker.subLevels.get(0);
        }
        return currentSubLevel;
    }

    @NonNull private final static ProgressTracker instance = new ProgressTracker();
    private ProgressTracker() {}

    /**
     * Get singleton instance
     */
    @NonNull
    public static ProgressTracker getInstance() {
        return instance;
    }

    /**
     * Initialize progress tracker and use storage interface to load words
     * @param storage Interface to interact with storage
     * @param display Size of entire display screen
     * @param fontSize Size of font to use for entire width of screen
     */
    public void init(@NonNull StorageInterface storage, @NonNull Rect display, float fontSize) {
        LevelTracker.init(storage);
        currentSubLevel = LevelTracker.subLevels.get(0);
        displayRect = display;
        normalizedFontSize = fontSize;

        EventBus.getDefault().register(this);

        try {
            Reader stream = storage.getAssetInputStream("words_9k.db");
            WordList.init(stream);
            stream.close();
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
    public void stopLevel(@NonNull Level ignoredLevel) {
        currentSubLevel.score();
        LevelTracker.store(currentSubLevel);
    }

    /**
     * Start Game
     */
    public void createGame(@NonNull Level level) {
        currentLevel = level;
        level.maxScore = 0;
        level.score = 0;
        level.timeUsed = 0;
        config = new Config(currentSubLevel.number, level.number);
        game = new Game(config, WordList.dictionary,
                new RandomSequencer(config, (int) System.currentTimeMillis()));
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void startSubLevel(@NonNull SubLevel subLevel) {
        currentSubLevel = subLevel;
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void startGame(@NonNull Game game) {
        game.populatePuzzle();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void addAnswer(@NonNull Answer answer) {
        currentLevel.maxScore += answer.score;
    }
}
