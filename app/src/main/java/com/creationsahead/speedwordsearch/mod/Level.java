package com.creationsahead.speedwordsearch.mod;

import androidx.annotation.NonNull;

/**
 * A single level
 */
public class Level {
    public static final int TIME_LEFT = 120;
    @NonNull public final String name;
    public final int number;
    public float stars;
    public int score;
    public boolean won;
    public int timeUsed;
    public int totalScore;

    public Level() {
        this("", 0);
    }

    public Level(@NonNull String levelName, int levelNumber) {
        name = levelName;
        number = levelNumber;
        stars = -1;
        timeUsed = 0;
        totalScore = 0;
        won = false;
    }

    /**
     * Score level based on total score and time used
     */
    public void score() {
        stars = 2.1f + 1.9f * (TIME_LEFT - timeUsed) / TIME_LEFT;
        stars *= (float) score / totalScore;
        stars = Math.max(Math.min(stars, 4f), 0f);
        won = stars >= 1.95f;
    }
}
