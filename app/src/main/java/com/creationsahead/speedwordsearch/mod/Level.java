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
    public int maxScore;
    public int bonus;

    public Level() {
        this("", 0);
    }

    public Level(@NonNull String levelName, int levelNumber) {
        name = levelName;
        number = levelNumber;
        stars = -1;
        timeUsed = 0;
        bonus = 0;
        maxScore = 0;
        won = false;
    }

    /**
     * Score level based on max score and time used
     */
    public void score() {
        stars = 4f * score / maxScore;
        stars = Math.max(Math.min(stars, 3.95f), 0f);
        won = stars >= 2.5f;
        bonus = (TIME_LEFT - timeUsed) / 10;
    }
}
