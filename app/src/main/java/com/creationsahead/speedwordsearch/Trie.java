package com.creationsahead.speedwordsearch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Trie to store dictionary words for quick searching
 * Loosely based on:
 * http://www.programcreek.com/2014/05/leetcode-implement-trie-prefix-tree-java/
 */
public class Trie {
    @NonNull private final TrieNode root;

    /**
     * A single node in the trie
     */
    public static class TrieNode {
        @NonNull final TrieNode[] arr;
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
    public void searchWithWildcards(@NonNull String s, @NonNull RandomSequencer sequencer,
                                    @NonNull ValidateCallback callback) {
        try {
            searchWithWildcards(root, s, "", sequencer, callback);
        } catch (RuntimeException ignored) {
        }
    }

    @Nullable
    private String searchWithWildcards(@NonNull TrieNode p, @NonNull String query,
                                       String result, @NonNull RandomSequencer sequencer,
                                       @NonNull ValidateCallback callback) {
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
                if (callback.onValid(subString)) {
                    // Hack to unwind stack
                    throw new RuntimeException(subString);
                }
            }
        } while (iterator != null && iterator.hasNext());

        return null;
    }
}
