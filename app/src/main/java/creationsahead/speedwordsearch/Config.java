package creationsahead.speedwordsearch;

/**
 * Configuration for a game
 */
public class Config {
    private static final int MAX_DIFFICULTY = 100;
    public final int sizeX, sizeY;
    /** Difficulty of game, 0 means very easy and 100 is very hard */
    public int difficulty;
    public final int letterLimit;
    public boolean isWordListSorted;

    public Config(int sizeX, int sizeY, int letterLimit) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.letterLimit = letterLimit;
        difficulty = 0;
        isWordListSorted = true;
    }

    public Config(int level) {
        sizeX = 4 + level/2;
        sizeY = 4 + (1 + level) / 2;
        difficulty = MAX_DIFFICULTY * level / 10;
        if (BuildConfig.DEBUG) {
            letterLimit = 13;
        } else {
            float letterRatio = (MAX_DIFFICULTY - difficulty) * 1.5f;
            letterRatio = sizeX * sizeY * letterRatio / 100;
            if (letterRatio < 5) {
                letterLimit = 5;
            } else {
                letterLimit = (int) letterRatio;
            }
        }
    }
}
