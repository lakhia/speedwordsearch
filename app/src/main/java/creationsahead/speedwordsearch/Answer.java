package creationsahead.speedwordsearch;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import org.greenrobot.eventbus.EventBus;

/**
 * A word that is stored in puzzle grid
 */
public class Answer {
    @NonNull public final String word;
    @NonNull public final Selection selection;
    @Nullable public Bonus bonus;
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
        event = Event.WORD_ADDED;
        EventBus.getDefault().post(this);
    }

    /**
     * Called when word is removed and score claimed
     */
    public void notifyScoreClaimed() {
        event = Event.SCORE_AWARDED;
        EventBus.getDefault().post(this);
    }

    /**
     * Get display string
     */
    public String getDisplay() {
        String ret = word;
        if (bonus != null) {
            ret += " {" + bonus.name + " @color/lightBlue}";
        }
        return ret;
    }

    @NonNull
    @Override
    public String toString() {
        return selection.toString() + ", word: " + word;
    }

    public void setBonus(@Nullable Bonus answerBonus) {
        bonus = answerBonus;
        event = Event.BONUS_UPDATED;
        EventBus.getDefault().post(this);
    }
}
