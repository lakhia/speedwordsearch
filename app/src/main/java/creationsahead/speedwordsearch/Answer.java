package creationsahead.speedwordsearch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import org.greenrobot.eventbus.EventBus;

/**
 * A word that is stored in puzzle grid
 */
public class Answer {
    @NonNull public final String word;
    @NonNull public final Selection selection;
    @Nullable public Object tag;
    public Event event;
    public final int score;

    /**
     * When word is added and assigned a score
     */
    public Answer(@NonNull Selection selection, @NonNull String word, int score) {
        this.selection = selection;
        this.word = word;
        this.score = score;
        event = Event.ANSWER_ADDED;
        EventBus.getDefault().post(this);
    }

    /**
     * Called when word is removed and score claimed
     */
    public void notifyScoreClaimed() {
        event = Event.ANSWER_CORRECT;
        EventBus.getDefault().post(this);
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
        return selection.toString() + ", word: " + word;
    }
}
