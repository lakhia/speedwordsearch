package creationsahead.speedwordsearch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * A word that is stored in puzzle grid
 */
public class Answer {
    @NonNull public final String word;
    @NonNull public final Selection selection;
    @Nullable public Object tag;
    public final int score;

    /**
     * When word is added and assigned a score
     */
    public Answer(@NonNull Selection selection, @NonNull String word) {
        this.selection = selection;
        this.word = word;
        this.score = 11 - word.length() / 2;
    }

    /**
     * Get display string
     */
    @NonNull
    public String getDisplay() {
        return word;
    }

    @NonNull
    @Override
    public String toString() {
        return selection + ", word: " + word;
    }
}
