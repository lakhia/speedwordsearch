package creationsahead.speedwordsearch;

/**
 * Types of events and optional payload.
 */
public enum Event {
    // Answer related events
    ANSWER_CORRECT,
    ANSWER_ADDED,

    // Game related events
    /** LEVEL_STARTED event sent with Level object */
    LEVEL_WON,
    LEVEL_LOST,
    GAME_WON;

    // Event specific payload
    public int timeLeft;
}
