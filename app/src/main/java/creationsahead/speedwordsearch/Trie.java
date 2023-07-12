package creationsahead.speedwordsearch;

/**
 * Trie to store dictionary words for quick searching
 *
 * Loosely based on:
 * http://www.programcreek.com/2014/05/leetcode-implement-trie-prefix-tree-java/
 */
public class Trie {
    private TrieNode root;

    /**
     * A single node in the trie
     */
    public class TrieNode {
        TrieNode[] arr;
        boolean isEnd;
        // Initialize your data structure here.
        protected TrieNode() {
            this.arr = new TrieNode[26];
        }

    }

    public Trie() {
        root = new TrieNode();
    }

    // Inserts a word into the trie.
    public void insert(String word) {
        TrieNode p = root;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            int index = c - 'a';
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
    public String searchWithWildcards(String s, Sequencer sequencer, ValidateCallback callback) {
        return searchWithWildcards(root, s, "", sequencer, callback);
    }

    private String searchWithWildcards(TrieNode p, String query, String result, Sequencer sequencer,
                                       ValidateCallback callback) {
        // Base case
        if (query.isEmpty()) {
            boolean isValid = p.isEnd;
            if (isValid && callback != null) {
                isValid = callback.onValid(result);
            }

            return (isValid ? "" : null);
        }

        // Recursive case
        int i = 0;
        char chosenChar = query.charAt(i);
        if (chosenChar == Cell.EMPTY) {
            int sequence[] = sequencer.getNextLetterSequence();
            for (int j : sequence) {
                chosenChar = (char)('a' + j);
                if (p.arr[j] != null) {
                    String subString = searchWithWildcards(p.arr[j], query.substring(i + 1),
                                                           result + chosenChar, sequencer, callback);
                    if (subString != null) {
                        return chosenChar + subString;
                    }
                }
            }
            return null;
        } else {
            int j = chosenChar - 'a';
            if (p.arr[j] != null) {
                String subString = searchWithWildcards(p.arr[j], query.substring(i + 1),
                                                       result + chosenChar, sequencer, callback);
                if (subString != null) {
                    return chosenChar + subString;
                }
            }
        }
        return null;
    }
}
