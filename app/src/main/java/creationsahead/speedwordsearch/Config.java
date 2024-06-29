package creationsahead.speedwordsearch;

/**
 * Configuration for a game
 */
public class Config {
    public final int sizeX, sizeY;
    /** Difficulty of game, 0 means very easy and 100 is very hard */
    public int difficulty;
    public int timeLimit;
    public final int letterLimit;
    public boolean isWordListSorted;

    public Config(int sizeX, int sizeY, int letterLimit) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.letterLimit = letterLimit;
        timeLimit = 0;
        difficulty = 0;
        isWordListSorted = true;
    }
}
