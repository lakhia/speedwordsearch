package creationsahead.speedwordsearch;

import java.util.ArrayList;

public class Guess {
    public final boolean last;
    public final boolean success;
    public final ArrayList<Object> tags;

    public Guess(ArrayList<Object> tags, boolean success, boolean last) {
        this.success = success;
        this.tags = tags;
        this.last = last;
    }
}
