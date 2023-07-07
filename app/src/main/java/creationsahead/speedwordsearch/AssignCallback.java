package creationsahead.speedwordsearch;

/**
 * Callback for word assignment
 */
public interface AssignCallback {
    boolean onUpdate(Position position, String contents);
}
