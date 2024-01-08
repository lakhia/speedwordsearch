package creationsahead.speedwordsearch;

/**
 * Types of events and optional payload.
 */
public enum Event {
    // Answer related events
    SCORE_AWARDED,
    BONUS_ADDED,
    WORD_ADDED,

    // UI related events
    FONT_SIZE,
    SYNC,

    // Game related events
    LEVEL_STARTED,
    LEVEL_WON,
    GAME_WON;

    // Event specific payload
    public float fontSize;
    public int levelNumber;
}
