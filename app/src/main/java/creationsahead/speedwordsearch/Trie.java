package creationsahead.speedwordsearch;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Trie to store dictionary words for quick searching
 *
 * Loosely based on:
 * http://www.programcreek.com/2014/05/leetcode-implement-trie-prefix-tree-java/
 */
public class Trie {
    private final TrieNode root;

    /**
     * A single node in the trie
     */
    public class TrieNode {
        final TrieNode[] arr;
        boolean isEnd;
        // Initialize your data structure here.
        TrieNode() {
            this.arr = new TrieNode[26];
        }

    }

    public Trie() {
        root = new TrieNode();
    }

    // Inserts a word into the trie.
    public void insert(@NonNull String word) {
        TrieNode p = root;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            int index = c - 'A';
            if (p.arr[index] == null) {
                TrieNode temp = new TrieNode();
                p.arr[index] = temp;
                p = temp;
            } else {
                p = p.arr[index];
            }
        }
        p.isEnd = true;
    }

    // Returns string that searches for string with wildcards
    @Nullable
    public String searchWithWildcards(@NonNull String s, @NonNull Sequencer sequencer,
                                      ValidateCallback callback) {
        try {
            return searchWithWildcards(root, s, "", sequencer, callback);
        } catch (IllegalMonitorStateException ex) {
            return ex.getMessage();
        }
    }

    private String searchWithWildcards(@NonNull TrieNode p, @NonNull String query,
                                       String result, @NonNull Sequencer sequencer,
                                       @Nullable ValidateCallback callback) {
        // Base case
        if (query.isEmpty()) {
            return p.isEnd ? result : null;
        }

        // Recursive case
        char chosenChar = query.charAt(0);
        SequenceIterator<Character> iterator = null;

        if (chosenChar == Cell.EMPTY) {
            iterator = sequencer.getLetterSequence();
        }

        do {
            if (iterator != null) {
                chosenChar = iterator.next();
            }
            TrieNode node = p.arr[chosenChar - 'A'];
            if (node != null) {
                String subString = searchWithWildcards(node, query.substring(1),
                                                       result + chosenChar, sequencer, callback);
                if (subString == null) {
                    if (node.isEnd) {
                        subString = result + chosenChar;
                    } else {
                        continue;
                    }
                }
                if (callback != null) {
                    if (!callback.onValid(subString)) {
                        continue;
                    } else {
                        // Hack to unwind stack
                        throw new IllegalMonitorStateException(subString);
                    }
                }
                return subString;
            }
        } while (iterator != null && iterator.hasNext());

        return null;
    }
}
