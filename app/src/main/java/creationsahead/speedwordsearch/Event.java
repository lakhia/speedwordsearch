package creationsahead.speedwordsearch;

/**
 * Types of events and optional payload.
 */
public enum Event {
    // Answer related events
    SCORE_AWARDED,
    BONUS_UPDATED,
    WORD_ADDED,

    // UI related events
    FONT_SIZE,

    // Cell related events
    CELL_STORED,
    CELL_SELECTION_CORRECT,
    CELL_SELECTION_INCORRECT,

    // Game related events
    LEVEL_STARTED,
    LEVEL_WON,
    GAME_WON;

    // Event specific payload
    public float fontSize;
    public int levelNumber;
    public boolean lastWordGuessed;
}
