package creationsahead.speedwordsearch;

/**
 * Game bonus (both positive and negative)
 */
public enum Bonus {

    // Positive bonus
    /** Hide 10 letters that are placeholders */
    BOMB("fa-bomb", 0),
    /** Add 30 seconds on the clock */
    TIME("fa-clock-o", 2),
    /** Hide 5 letters that are placeholders */
    BOLT("fa-bolt", 4),
    /** Give a score boost */
    SCORE("fa-star", 6),
    /** Add a word */
    PLUS("fa-plus", 8),

    // Negative bonus
    /** Replace some placeholders with other placeholders */
    MIX("fa-random", 12),
    /** Remove one word from the list */
    REMOVE("fa-remove", 14),
    /** Reduce score bonus */
    HALF_SCORE("fa-star-half-o", 16),
    /** Replace 5 used letters with question marks */
    BLUR("fa-low-vision", 18);

    Bonus(String name, int benefit) {
        this.name = name;
        this.benefit = benefit;
    }

    public static Bonus find(int val) {
        for (Bonus bonus : values()) {
            if (bonus.benefit == val) {
                return bonus;
            }
        }
        return null;
    }

    public final String name;
    private final int benefit;
}
