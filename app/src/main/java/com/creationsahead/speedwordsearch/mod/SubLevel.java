package com.creationsahead.speedwordsearch.mod;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import static com.creationsahead.speedwordsearch.LevelTracker.MAX_LEVEL;

/**
 * A single level
 */
public final class SubLevel extends Level {
    @NonNull
    public final ArrayList<Level> levels;

    public SubLevel() {
        this("", 0, 0);
    }

    public SubLevel(@NonNull String name, int levelNumber, int numberOfSubLevels) {
        super(name, levelNumber);
        levels = new ArrayList<>(numberOfSubLevels);
        for (int i = 0; i < numberOfSubLevels; ++i) {
            addLevel(i);
        }
    }

    public void addLevel(int levelNumber) {
        levels.add(levelNumber, new Level(name + "\nLevel " + (levelNumber + 1),
                levelNumber));
    }

    /**
     * Score level based on average of leave levels
     */
    public void score() {
        stars = 0.0f;
        score = 0;
        timeUsed = 0;
        bonus = 0;
        for (Level level : levels) {
            stars += level.stars;
            score += level.score;
            bonus += level.bonus;
            timeUsed += level.timeUsed;
        }
        stars = stars / MAX_LEVEL;
        stars = Math.max(Math.min(stars, 2.95f), 0f);
        won = levels.size() >= MAX_LEVEL;
    }
}
