package creationsahead.speedwordsearch;

import androidx.annotation.Nullable;
import java.util.ArrayList;

public class Guess {
    public final boolean last;
    public final @Nullable Answer answer;
    public final ArrayList<Object> tags;

    public Guess(ArrayList<Object> tags, @Nullable Answer answer, boolean last) {
        this.answer = answer;
        this.tags = tags;
        this.last = last;
    }
}
