package creationsahead.speedwordsearch;

/**
 * Types of events and optional payload.
 */
public enum Event {
    // Answer related events
    ANSWER_CORRECT,
    ANSWER_INCORRECT,
    ANSWER_ADDED,

    // UI related events
    PAUSE,
    UN_PAUSE,

    // Cell selection events
    CELL_SELECTION_CORRECT,
    CELL_SELECTION_INCORRECT,

    // Game related events
    /** LEVEL_STARTED event sent with Level object */
    LEVEL_WON,
    LEVEL_LOST,
    GAME_WON;

    // Event specific payload
    public boolean lastWordGuessed;
    public int timeLeft;
}
