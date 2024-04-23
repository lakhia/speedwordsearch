package creationsahead.speedwordsearch;

/**
 * Types of events and optional payload.
 */
public enum Event {
    // Answer related events
    SCORE_AWARDED,
    WORD_ADDED,

    // UI related events
    FONT_SIZE,
    PAUSE,
    UN_PAUSE,

    // Cell related events
    /** CELL_UPDATE event sent with Cell object */
    CELL_STORED,
    CELL_SELECTION_CORRECT,
    CELL_SELECTION_INCORRECT,

    // Game related events
    /** LEVEL_STARTED event sent with Level object */
    LEVEL_WON,
    LEVEL_LOST,
    GAME_WON;

    // Event specific payload
    public float fontSize;
    public boolean lastWordGuessed;
    public int timeLeft;
}
