package creationsahead.speedwordsearch.mod;

/**
 * A single level
 */
public class Level {
    public static final int TIME_LEFT = 120;
    public final String name;
    public final int number;
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

    /**
     * Score level based on total score and time used
     */
    public void score(int timeLeft) {
        timeUsed = 120 - timeLeft;
        stars = 2.0f * score / totalScore;
        stars += 2.0f * (TIME_LEFT - timeUsed) / TIME_LEFT;
        stars = Math.min(stars, 4f);
    }
}
