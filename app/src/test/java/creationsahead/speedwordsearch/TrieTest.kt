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
        dictionary.insert("ABAC")
        dictionary.insert("BBAC")
        dictionary.insert("BAAC")
        dictionary.insert("AAAC")

        val word = dictionary.searchWithWildcards("ABAC", DeterministicSequencer(1), null)
        assertEquals("ABAC", word)
    }

    @Test
    fun test_02_one_wildcard() {
        val dictionary: Trie = Trie()
        dictionary.insert("ABAC")
        dictionary.insert("BBAC")
        dictionary.insert("BAAC")
        dictionary.insert("AAAC")

        val word = dictionary.searchWithWildcards("AB.C", DeterministicSequencer(1), null)
        assertEquals("ABAC", word)
    }

    @Test
    fun test_03_two_wildcard() {
        val dictionary: Trie = Trie()
        dictionary.insert("BBAC")
        dictionary.insert("BAAC")
        dictionary.insert("AAAC")

        val word = dictionary.searchWithWildcards("BB..", DeterministicSequencer(1), null)
        assertEquals("BBAC", word)
    }

    @Test
    fun test_04_wildcard_choices() {
        val dictionary: Trie = Trie()
        dictionary.insert("ABAC")
        dictionary.insert("BBAC")
        dictionary.insert("BAAC")
        dictionary.insert("AAAC")

        var word = dictionary.searchWithWildcards(".BAC", DeterministicSequencer(1), null)
        assertEquals("ABAC", word)
        word = dictionary.searchWithWildcards(".BAC", DeterministicSequencer(3), null)
        assertEquals("BBAC", word)
    }

    @Test
    fun test_05_two_wildcard_choices() {
        val dictionary: Trie = Trie()
        dictionary.insert("ABAC")
        dictionary.insert("BBAC")
        dictionary.insert("BAAC")
        dictionary.insert("AAAC")

        var word = dictionary.searchWithWildcards("..AC", DeterministicSequencer(1), null)
        assertEquals("AAAC", word)
        word = dictionary.searchWithWildcards("..AC", DeterministicSequencer(3), null)
        assertEquals("BAAC", word)
        word = dictionary.searchWithWildcards("..AC", DeterministicSequencer(33), null)
        assertEquals("ABAC", word)
    }

    @Test
    fun test_06_substrings() {
        val dictionary: Trie = Trie()
        dictionary.insert("A")
        dictionary.insert("AB")
        dictionary.insert("ABA")
        dictionary.insert("ABAC")
        dictionary.insert("ABACA")
        dictionary.insert("ABACAB")

        var word = dictionary.searchWithWildcards("C", DeterministicSequencer(1), null)
        assertEquals(null, word)
        word = dictionary.searchWithWildcards("A", DeterministicSequencer(3), null)
        assertEquals("A", word)
        word = dictionary.searchWithWildcards("AB", DeterministicSequencer(33), null)
        assertEquals("AB", word)
        word = dictionary.searchWithWildcards("ABA", DeterministicSequencer(33), null)
        assertEquals("ABA", word)
        word = dictionary.searchWithWildcards("ABAC", DeterministicSequencer(33), null)
        assertEquals("ABAC", word)
        word = dictionary.searchWithWildcards("ABACA", DeterministicSequencer(33), null)
        assertEquals("ABACA", word)
        word = dictionary.searchWithWildcards("ABACAB", DeterministicSequencer(33), null)
        assertEquals("ABACAB", word)
    }

    @Test
    fun test_07_validation() {
        val dictionary: Trie = Trie()
        dictionary.insert("ABBA")
        dictionary.insert("ACCA")
        dictionary.insert("ADDA")
        dictionary.insert("AEEC")
        dictionary.insert("AFFA")
        dictionary.insert("AGGA")
        dictionary.insert("BGGA")
        dictionary.insert("CGGA")

        var counter = 0
        var word = dictionary.searchWithWildcards("....", DeterministicSequencer(1), { counter++; false; })
        assertEquals(null, word)
        assertEquals(8, counter)

        counter = 0
        word = dictionary.searchWithWildcards(".G..", DeterministicSequencer(1), { counter++; false; })
        assertEquals(null, word)
        assertEquals(3, counter)

        counter = 0
        word = dictionary.searchWithWildcards("A...", DeterministicSequencer(1), { counter++; false; })
        assertEquals(null, word)
        assertEquals(6, counter)

        counter = 0
        word = dictionary.searchWithWildcards("A...", DeterministicSequencer(1), { counter++; counter==3; })
        assertEquals("ADDA", word)
        assertEquals(3, counter)

        counter = 0
        word = dictionary.searchWithWildcards("...A", DeterministicSequencer(1), { counter++; counter==4; })
        assertEquals("AFFA", word)
        assertEquals(4, counter)

        word = dictionary.searchWithWildcards(".BB.", DeterministicSequencer(1), { true; })
        assertEquals("ABBA", word)

        word = dictionary.searchWithWildcards("C...", DeterministicSequencer(1), { true; })
        assertEquals("CGGA", word)
    }

    @Test
    fun test_08_substrings_validation_order() {
        val dictionary: Trie = Trie()
        dictionary.insert("A")
        dictionary.insert("AB")
        dictionary.insert("ABC")
        dictionary.insert("ABCD")
        dictionary.insert("ABCDE")
        dictionary.insert("ABCDEF")

        var counter = 0
        val buffer = StringBuffer()
        var word = dictionary.searchWithWildcards("......", DeterministicSequencer(1),
                { buffer.append(it); counter++; false; })
        assertEquals(null, word)
        assertEquals(6, counter)
        assertEquals("ABCDEFABCDEABCDABCABA", buffer.toString())


        counter = 0
        buffer.delete(0, buffer.length)
        word = dictionary.searchWithWildcards("A.....", DeterministicSequencer(1),
                { buffer.append(it); counter++; false; })
        assertEquals(null, word)
        assertEquals(6, counter)
        assertEquals("ABCDEFABCDEABCDABCABA", buffer.toString())

        counter = 0
        buffer.delete(0, buffer.length)
        word = dictionary.searchWithWildcards(".B....", DeterministicSequencer(1),
                { buffer.append(it); counter++; false; })
        assertEquals(null, word)
        assertEquals(6, counter)
        assertEquals("ABCDEFABCDEABCDABCABA", buffer.toString())
    }
}
