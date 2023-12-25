package creationsahead.speedwordsearch;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * A word that is stored in puzzle grid
 */
public class Answer {
    @NonNull public final String word;
    @NonNull public final Selection selection;
    @Nullable public Bonus bonus;
    @Nullable public Object tag;
    @Nullable public static AnswerCallback callback;
    public final int score;

    /**
     * When word is added and assigned a score
     */
    public Answer(@NonNull Selection selection, @NonNull String word, int score) {
        this.selection = selection;
        this.word = word;
        this.score = score;
        if (callback != null) {
            callback.onUpdate(this, Event.WORD_ADDED);
        }
    }

    /**
     * Called when word is removed and score claimed
     */
    public void notifyScoreClaimed() {
        if (callback != null) {
            callback.onUpdate(this, Event.SCORE_AWARDED);
        }
    }

    /**
     * Get display string
     */
    public String getDisplay() {
        String ret = word;
        if (bonus != null) {
            ret += " {" + bonus.name + " @color/colorEditTextLight}";
        }
        return ret;
    }

    @NonNull
    @Override
    public String toString() {
        return selection.toString() + ", word: " + word;
    }

    public void setBonus(@NonNull Bonus bonus) {
        this.bonus = bonus;
        if (callback != null) {
            callback.onUpdate(this, Event.BONUS_ADDED);
        }
    }
}
