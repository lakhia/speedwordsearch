package creationsahead.speedwordsearch;

import androidx.annotation.NonNull;
import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class AnswerMap {
    @NonNull private final HashMap<String, Answer> answerMap;
    private final static Comparator<Answer> comparator = (a, b) -> a.word.compareTo(b.word);

    public AnswerMap() {
        answerMap = new HashMap<>();
    }

    public boolean validate(@NonNull String word) {
        // Cannot add same word twice
        if (answerMap.containsKey(word)) {
            return false;
        }

        // Reject substring words
        for (String ans : answerMap.keySet()) {
            if (ans.startsWith(word) || word.startsWith(ans)) {
                return false;
            }
        }
        return true;
    }

    public void add(@NonNull Answer answer) {
        answerMap.put(answer.word, answer);
        EventBus.getDefault().post(answer);
    }

    public Answer pop(@NonNull String word) {
        return answerMap.remove(word);
    }

    public boolean isSolved() {
        return answerMap.isEmpty();
    }

    @NonNull
    public ArrayList<Answer> getAnswers(boolean isWordListSorted) {
        Collection<Answer> values = answerMap.values();
        ArrayList<Answer> list = new ArrayList<>(values);
        if (isWordListSorted) {
            Collections.sort(list, comparator);
        }
        return list;
    }
}
