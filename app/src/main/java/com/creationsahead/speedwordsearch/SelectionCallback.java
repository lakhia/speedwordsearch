package com.creationsahead.speedwordsearch;

/**
 * Callback for word assignment
 */
public interface SelectionCallback {
    boolean onUpdate(Selection selection, String contents);
}
