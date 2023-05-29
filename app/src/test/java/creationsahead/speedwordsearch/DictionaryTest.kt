package creationsahead.speedwordsearch

import org.junit.Test
import org.junit.Assert.assertEquals

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class DictionaryTest {
    @Test
    fun test_01_no_wildcards() {
        val dictionary: Trie = Trie()
        dictionary.insert("abac")
        dictionary.insert("bbac")
        dictionary.insert("baac")
        dictionary.insert("aaac")

        val word = dictionary.searchWithWildcards("abac", Sequencer(1))
        assertEquals("abac", word)
    }

    @Test
    fun test_02_one_wildcard() {
        val dictionary: Trie = Trie()
        dictionary.insert("abac")
        dictionary.insert("bbac")
        dictionary.insert("baac")
        dictionary.insert("aaac")

        val word = dictionary.searchWithWildcards("ab.c", Sequencer(1))
        assertEquals("abac", word)
    }

    @Test
    fun test_03_two_wildcard() {
        val dictionary: Trie = Trie()
        dictionary.insert("bbac")
        dictionary.insert("baac")
        dictionary.insert("aaac")

        val word = dictionary.searchWithWildcards("bb..", Sequencer(1))
        assertEquals("bbac", word)
    }

    @Test
    fun test_04_wildcard_choices() {
        val dictionary: Trie = Trie()
        dictionary.insert("abac")
        dictionary.insert("bbac")
        dictionary.insert("baac")
        dictionary.insert("aaac")

        var word = dictionary.searchWithWildcards(".bac", Sequencer(1))
        assertEquals("abac", word)
        word = dictionary.searchWithWildcards(".bac", Sequencer(3))
        assertEquals("bbac", word)
    }

    @Test
    fun test_05_two_wildcard_choices() {
        val dictionary: Trie = Trie()
        dictionary.insert("abac")
        dictionary.insert("bbac")
        dictionary.insert("baac")
        dictionary.insert("aaac")

        var word = dictionary.searchWithWildcards("..ac", Sequencer(1))
        assertEquals("aaac", word)
        word = dictionary.searchWithWildcards("..ac", Sequencer(3))
        assertEquals("baac", word)
        word = dictionary.searchWithWildcards("..ac", Sequencer(33))
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

        var word = dictionary.searchWithWildcards("c", Sequencer(1))
        assertEquals(null, word)
        word = dictionary.searchWithWildcards("a", Sequencer(3))
        assertEquals("a", word)
        word = dictionary.searchWithWildcards("ab", Sequencer(33))
        assertEquals("ab", word)
        word = dictionary.searchWithWildcards("aba", Sequencer(33))
        assertEquals("aba", word)
        word = dictionary.searchWithWildcards("abac", Sequencer(33))
        assertEquals("abac", word)
        word = dictionary.searchWithWildcards("abaca", Sequencer(33))
        assertEquals("abaca", word)
        word = dictionary.searchWithWildcards("abacab", Sequencer(33))
        assertEquals("abacab", word)
    }
}
