package creationsahead.speedwordsearch;

/**
 * Callback for word assignment
 */
public interface AssignCallback {
    boolean onUpdate(Selection selection, String contents);
}
