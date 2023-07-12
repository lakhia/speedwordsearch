package creationsahead.speedwordsearch

import org.junit.Test
import org.junit.Assert.assertEquals

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class TrieTest {
    @Test
    fun test_01_no_wildcards() {
        val dictionary: Trie = Trie()
        dictionary.insert("abac")
        dictionary.insert("bbac")
        dictionary.insert("baac")
        dictionary.insert("aaac")

        val word = dictionary.searchWithWildcards("abac", Sequencer(1), null)
        assertEquals("abac", word)
    }

    @Test
    fun test_02_one_wildcard() {
        val dictionary: Trie = Trie()
        dictionary.insert("abac")
        dictionary.insert("bbac")
        dictionary.insert("baac")
        dictionary.insert("aaac")

        val word = dictionary.searchWithWildcards("ab.c", Sequencer(1), null)
        assertEquals("abac", word)
    }

    @Test
    fun test_03_two_wildcard() {
        val dictionary: Trie = Trie()
        dictionary.insert("bbac")
        dictionary.insert("baac")
        dictionary.insert("aaac")

        val word = dictionary.searchWithWildcards("bb..", Sequencer(1), null)
        assertEquals("bbac", word)
    }

    @Test
    fun test_04_wildcard_choices() {
        val dictionary: Trie = Trie()
        dictionary.insert("abac")
        dictionary.insert("bbac")
        dictionary.insert("baac")
        dictionary.insert("aaac")

        var word = dictionary.searchWithWildcards(".bac", Sequencer(1), null)
        assertEquals("abac", word)
        word = dictionary.searchWithWildcards(".bac", Sequencer(3), null)
        assertEquals("bbac", word)
    }

    @Test
    fun test_05_two_wildcard_choices() {
        val dictionary: Trie = Trie()
        dictionary.insert("abac")
        dictionary.insert("bbac")
        dictionary.insert("baac")
        dictionary.insert("aaac")

        var word = dictionary.searchWithWildcards("..ac", Sequencer(1), null)
        assertEquals("aaac", word)
        word = dictionary.searchWithWildcards("..ac", Sequencer(3), null)
        assertEquals("baac", word)
        word = dictionary.searchWithWildcards("..ac", Sequencer(33), null)
        assertEquals("abac", word)
    }

    @Test
    fun test_06_substrings() {
        val dictionary: Trie = Trie()
        dictionary.insert("a")
        dictionary.insert("ab")
        dictionary.insert("aba")
        dictionary.insert("abac")
        dictionary.insert("abaca")
        dictionary.insert("abacab")

        var word = dictionary.searchWithWildcards("c", Sequencer(1), null)
        assertEquals(null, word)
        word = dictionary.searchWithWildcards("a", Sequencer(3), null)
        assertEquals("a", word)
        word = dictionary.searchWithWildcards("ab", Sequencer(33), null)
        assertEquals("ab", word)
        word = dictionary.searchWithWildcards("aba", Sequencer(33), null)
        assertEquals("aba", word)
        word = dictionary.searchWithWildcards("abac", Sequencer(33), null)
        assertEquals("abac", word)
        word = dictionary.searchWithWildcards("abaca", Sequencer(33), null)
        assertEquals("abaca", word)
        word = dictionary.searchWithWildcards("abacab", Sequencer(33), null)
        assertEquals("abacab", word)
    }

    @Test
    fun test_07_validation() {
        val dictionary: Trie = Trie()
        dictionary.insert("abba")
        dictionary.insert("acca")
        dictionary.insert("adda")
        dictionary.insert("aeec")
        dictionary.insert("affa")
        dictionary.insert("agga")
        dictionary.insert("bgga")
        dictionary.insert("cgga")

        var counter = 0
        var word = dictionary.searchWithWildcards("....", Sequencer(1), { counter++; false; })
        assertEquals(null, word)
        assertEquals(8, counter)

        counter = 0
        word = dictionary.searchWithWildcards(".g..", Sequencer(1), { counter++; false; })
        assertEquals(null, word)
        assertEquals(3, counter)

        counter = 0
        word = dictionary.searchWithWildcards("a...", Sequencer(1), { counter++; false; })
        assertEquals(null, word)
        assertEquals(6, counter)

        counter = 0
        word = dictionary.searchWithWildcards("a...", Sequencer(1), { counter++; counter==3; })
        assertEquals("adda", word)
        assertEquals(3, counter)

        counter = 0
        word = dictionary.searchWithWildcards("...a", Sequencer(1), { counter++; counter==4; })
        assertEquals("affa", word)
        assertEquals(4, counter)

        word = dictionary.searchWithWildcards(".bb.", Sequencer(1), { true; })
        assertEquals("abba", word)

        word = dictionary.searchWithWildcards("c...", Sequencer(1), { true; })
        assertEquals("cgga", word)
    }
}
