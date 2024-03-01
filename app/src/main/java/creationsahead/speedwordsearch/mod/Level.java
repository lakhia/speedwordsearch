package creationsahead.speedwordsearch.mod;

/**
 * A single level
 */
public class Level {
    public String name;
    public int number;
    public float stars;
    public int score;
    public int timeUsed;

    public Level() {}

    public Level(String levelName, int levelNumber) {
        name = levelName;
        number = levelNumber;
        stars = -1;
        timeUsed = 0;
    }
}
