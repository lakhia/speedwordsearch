package creationsahead.speedwordsearch;

/**
 * Trie to store dictionary words for quick searching
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

    // Returns if the word is in the trie.
    public boolean search(String word) {
        TrieNode p = searchNode(word);
        if (p == null) {
            return false;
        } else {
            if (p.isEnd) return true;
        }

        return false;
    }

    // Returns if there is any word in the trie
    // that starts with the given prefix.
    public boolean startsWith(String prefix) {
        TrieNode p = searchNode(prefix);
        if (p == null) {
            return false;
        } else {
            return true;
        }
    }

    // Returns string that searches for string with wildcards
    public String searchWithWildcards(String s, Sequencer sequencer) {
        return searchWithWildcards(root, s, sequencer);
    }

    private String searchWithWildcards(TrieNode p, String s, Sequencer sequencer) {
        // Base case
        if (s.isEmpty()) {
            return (p.isEnd ? "" : null);
        }

        // Recursive case
        int i = 0;
        char c = s.charAt(i);
        if (c == Cell.EMPTY) {
            int sequence[] = sequencer.getNextSequence();
            for (int j : sequence) {
                if (p.arr[j] != null) {
                    String subString = searchWithWildcards(p.arr[j], s.substring(i + 1), sequencer);
                    if (subString != null) {
                        return (char)('a' + j) + subString;
                    }
                }
            }
            return null;
        } else {
            int j = c - 'a';
            if (p.arr[j] != null) {
                String subString = searchWithWildcards(p.arr[j], s.substring(i + 1), sequencer);
                if (subString != null) {
                    return (char)('a' + j) + subString;
                }
            }
        }
        return null;
    }

    public TrieNode searchNode(String s) {
        TrieNode p = root;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int index = c - 'a';
            if (p.arr[index] != null) {
                p = p.arr[index];
            } else {
                return null;
            }
        }

        if (p == root) return null;

        return p;
    }
}
