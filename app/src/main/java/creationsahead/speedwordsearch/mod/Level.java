package creationsahead.speedwordsearch.mod;

/**
 * A single level
 */
public class Level {
    public final String name;
    public int number;
    public float stars;
    public int score;
    public int timeUsed;
    public int totalScore;

    public Level() {
        this("", 0);
    }

    public Level(String levelName, int levelNumber) {
        name = levelName;
        number = levelNumber;
        stars = -1;
        timeUsed = 0;
        totalScore = 0;
    }
}
