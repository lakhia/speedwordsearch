package creationsahead.speedwordsearch;

import android.support.annotation.NonNull;

/**
 * Configuration for a game
 */
public class Config {
    public final int sizeX, sizeY;
    @NonNull public final Trie dictionary;
    /** Difficulty of game, 1 means very easy and 100 is very hard */
    public final int difficulty;
    /** Desired letter ratio, out of 100 */
    public final int letterRatio;

    public Config(int sizeX, int sizeY, @NonNull Trie dictionary, int difficulty) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.dictionary = dictionary;
        this.difficulty = difficulty;
        this.letterRatio = (int) ((100-difficulty) * 1.5f);
    }
}
