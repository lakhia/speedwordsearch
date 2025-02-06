package com.creationsahead.speedwordsearch;

/**
 * Configuration for a game
 */
public class Config {
    private static final int MAX_DIFFICULTY = 100;
    public final int sizeX, sizeY;
    /** Difficulty of game, 0 means very easy and 100 is very hard */
    public float difficulty;
    public final int letterLimit;
    public boolean isWordListSorted;
    public int smallerWords;

    public Config(int sizeX, int sizeY, int letterLimit) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.letterLimit = letterLimit;
        difficulty = 0;
        isWordListSorted = true;
        smallerWords = 0;
    }

    public Config(int subLevelNumber, int levelNumber) {
        sizeX = 4 + levelNumber/2 + subLevelNumber/3;
        sizeY = 4 + (1 + levelNumber) / 2 + subLevelNumber/3;
        difficulty = 25f * (subLevelNumber/4f + levelNumber/10f);
        isWordListSorted = subLevelNumber < 6;

        double letterRatio = difficulty / MAX_DIFFICULTY * 6.0;
        letterRatio = sizeX * Math.sqrt(sizeY) * letterRatio;
        if (letterRatio < 5) {
            letterLimit = 5;
        } else {
            letterLimit = (int) letterRatio;
        }
        smallerWords = Math.min(subLevelNumber / 3, sizeY - 4);
    }

    public int getFreqBasedOnSizeDifficulty(boolean flipped) {
        double letterCount = sizeX * sizeY;
        if (flipped) {
            return (int) (Math.sqrt(101 - difficulty) * letterCount / 10d);
        } else {
            return (int) (Math.sqrt(difficulty + 1) * letterCount / 10d);
        }
    }
}
