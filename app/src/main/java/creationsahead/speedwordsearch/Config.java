package creationsahead.speedwordsearch;

import android.support.annotation.NonNull;

/**
 * Configuration for a game
 */
public class Config {
    public int sizeX, sizeY;
    @NonNull public final Trie dictionary;
    /** Difficulty of game, 0 means very easy and 100 is very hard */
    public int difficulty;
    public int timeLimit;
    public int letterLimit;

    public Config(@NonNull Trie dict) {
        this(0, 0, dict, 0);
    }

    public Config(int sizeX, int sizeY, @NonNull Trie dict, int letterLimit) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.letterLimit = letterLimit;
        dictionary = dict;
        timeLimit = 0;
        difficulty = 0;
    }
}
