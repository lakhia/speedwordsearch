package creationsahead.speedwordsearch;

/**
 * A word that is stored in puzzle grid
 */
public class Answer {
    public static AnswerCallback callback;
    public final String word;
    public final Selection selection;
    public final int score;
    public Object tag;

    /**
     * When word is added and assigned a score
     */
    public Answer(Selection selection, String word, int score) {
        this.selection = selection;
        this.word = word;
        this.score = score;
        if (callback != null) {
            callback.onUpdate(this);
        }
    }

    /**
     * Called when word is removed and score claimed
     */
    public void notifyScoreClaimed() {
        if (callback != null) {
            callback.onUpdate(this);
        }
    }
}
