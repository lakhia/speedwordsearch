package com.creationsahead.speedwordsearch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class AnswerMap {

    private static class OneLetter {
        public final Position position;
        public final char letter;

        public OneLetter(Position pos, char let) {
            position = pos;
            letter = let;
        }
    }

    @NonNull private final HashMap<String, Answer> answerMap;
    @NonNull private final HashSet<String> previousAnswers;
    @NonNull private final LinkedList<OneLetter> hiddenLetters = new LinkedList<>();
    private Answer lastHiddenAnswer;
    private final static Comparator<Answer> comparator = (a, b) -> a.word.compareTo(b.word);

    public AnswerMap() {
        answerMap = new HashMap<>();
        previousAnswers = new HashSet<>(0);
    }

    public boolean validate(@NonNull String word) {
        // Cannot add same word twice
        if (answerMap.containsKey(word)) {
            return false;
        }

        // Reject previous words that were solved
        if (previousAnswers.contains(word)) {
            return false;
        }
        if (lastHiddenAnswer != null && word.equals(lastHiddenAnswer.word) ) {
            return false;
        }
        return true;
    }

    public void add(@NonNull Answer answer) {
        answerMap.put(answer.word, answer);
        EventBus.getDefault().post(answer);
    }

    @Nullable
    public Answer pop(@NonNull String word) {
        Answer result = answerMap.remove(word);
        if (result != null) {
            previousAnswers.add(word);
        }
        return result;
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

    public boolean hiddenAnswersEmpty() {
        return hiddenLetters.isEmpty();
    }

    public void addHiddenAnswer(@NonNull Answer ans) {
        if (!hiddenAnswersEmpty()) {
            throw new RuntimeException("Adding an answer while another answer is in flight");
        }
        Selection select = ans.selection;
        for (int x = select.position.x, y = select.position.y, i = 0; i < ans.word.length(); i++) {
            hiddenLetters.add(new OneLetter(new Position(x, y), ans.word.charAt(i)));
            x += select.direction.x;
            y += select.direction.y;
        }
        Collections.shuffle(hiddenLetters);
        lastHiddenAnswer = ans;
    }

    public boolean addHiddenLetter(@NonNull PositionUpdateCallback callback) {
        if (hiddenLetters.isEmpty()) {
            return false;
        }
        OneLetter letter = hiddenLetters.pop();
        if (!callback.onUpdate(letter.position, letter.letter)) {
            throw new RuntimeException("Added partial letters");
        }
        if (hiddenLetters.isEmpty()) {
            add(lastHiddenAnswer);
        }
        return true;
    }
}
