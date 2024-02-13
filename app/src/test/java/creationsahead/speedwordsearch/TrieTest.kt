package creationsahead.speedwordsearch

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class TrieTest {
    @Test
    fun test_01_wildcards() {
        val dictionary = Trie()
        dictionary.insert("ABAC")
        dictionary.insert("BBAC")
        dictionary.insert("BAAC")
        dictionary.insert("AAAC")

        val config = Config(1,1, dictionary, 0)
        val seq = RandomSequencer(config, 1)

        // No wildcards
        var word = dictionary.searchWithWildcards("ABAC", seq, null)
        assertEquals("ABAC", word)

        // 1 wildcard
        word = dictionary.searchWithWildcards("AB.C", seq, null)
        assertEquals("ABAC", word)
        word = dictionary.searchWithWildcards(".BAC", seq, null)
        assertEquals("BBAC", word)
        word = dictionary.searchWithWildcards(".BAC", seq, null)
        assertEquals("ABAC", word)

        // Two wildcards
        word = dictionary.searchWithWildcards("BB..", RandomSequencer(config, 3), null)
        assertEquals("BBAC", word)
        word = dictionary.searchWithWildcards("..AC", RandomSequencer(config, 10), null)
        assertEquals("ABAC", word)
        word = dictionary.searchWithWildcards("..AC", RandomSequencer(config, 30), null)
        assertEquals("BBAC", word)
        word = dictionary.searchWithWildcards("..AC", RandomSequencer(config, 100), null)
        assertEquals("ABAC", word)
        word = dictionary.searchWithWildcards("..AC", RandomSequencer(config, 300), null)
        assertEquals("BBAC", word)
    }

    @Test
    fun test_06_substrings() {
        val dictionary = Trie()
        dictionary.insert("A")
        dictionary.insert("AB")
        dictionary.insert("ABA")
        dictionary.insert("ABAC")
        dictionary.insert("ABACA")
        dictionary.insert("ABACAB")

        val seq = RandomSequencer(Config(1,1, dictionary, 0), 1)

        var word = dictionary.searchWithWildcards("C", seq, null)
        assertEquals(null, word)
        word = dictionary.searchWithWildcards("A", seq, null)
        assertEquals("A", word)
        word = dictionary.searchWithWildcards("AB", seq, null)
        assertEquals("AB", word)
        word = dictionary.searchWithWildcards("ABA", seq, null)
        assertEquals("ABA", word)
        word = dictionary.searchWithWildcards("ABAC", seq, null)
        assertEquals("ABAC", word)
        word = dictionary.searchWithWildcards("ABACA", seq, null)
        assertEquals("ABACA", word)
        word = dictionary.searchWithWildcards("ABACAB", seq, null)
        assertEquals("ABACAB", word)
    }

    @Test
    fun test_07_validation() {
        val dictionary = Trie()
        dictionary.insert("ABBA")
        dictionary.insert("ACCA")
        dictionary.insert("ADDA")
        dictionary.insert("AEEC")
        dictionary.insert("AFFA")
        dictionary.insert("AGGA")
        dictionary.insert("BGGA")
        dictionary.insert("CGGA")
        val seq = RandomSequencer(Config(1,1, dictionary, 0), 1)

        var counter = 0
        var word = dictionary.searchWithWildcards("....", seq, { counter++; false; })
        assertEquals(null, word)
        assertEquals(8, counter)

        counter = 0
        word = dictionary.searchWithWildcards(".G..", seq, { counter++; false; })
        assertEquals(null, word)
        assertEquals(3, counter)

        counter = 0
        word = dictionary.searchWithWildcards("A...", seq, { counter++; false; })
        assertEquals(null, word)
        assertEquals(6, counter)

        counter = 0
        word = dictionary.searchWithWildcards("A...", seq, { counter++; counter==3; })
        assertEquals("ADDA", word)
        assertEquals(3, counter)

        counter = 0
        word = dictionary.searchWithWildcards("...A", seq, { counter++; counter==4; })
        assertEquals("AFFA", word)
        assertEquals(4, counter)

        counter = 0
        word = dictionary.searchWithWildcards(".BB.", seq, { counter++; true; })
        assertEquals("ABBA", word)
        assertEquals(1, counter)

        counter = 0
        word = dictionary.searchWithWildcards("C...", seq, { counter++; true; })
        assertEquals("CGGA", word)
        assertEquals(1, counter)
    }

    @Test
    fun test_08_substrings_validation_order() {
        val dictionary = Trie()
        dictionary.insert("A")
        dictionary.insert("AB")
        dictionary.insert("ABC")
        dictionary.insert("ABCD")
        dictionary.insert("ABCDE")
        dictionary.insert("ABCDEF")
        val seq = RandomSequencer(Config(1,1, dictionary, 0), 1)

        var counter = 0
        val buffer = StringBuffer()
        var word = dictionary.searchWithWildcards("......", seq,
                { buffer.append(it); counter++; false; })
        assertEquals(null, word)
        assertEquals(6, counter)
        assertEquals("ABCDEFABCDEABCDABCABA", buffer.toString())


        counter = 0
        buffer.delete(0, buffer.length)
        word = dictionary.searchWithWildcards("A.....", seq,
                { buffer.append(it); counter++; false; })
        assertEquals(null, word)
        assertEquals(6, counter)
        assertEquals("ABCDEFABCDEABCDABCABA", buffer.toString())

        counter = 0
        buffer.delete(0, buffer.length)
        word = dictionary.searchWithWildcards(".B....", seq,
                { buffer.append(it); counter++; false; })
        assertEquals(null, word)
        assertEquals(6, counter)
        assertEquals("ABCDEFABCDEABCDABCABA", buffer.toString())
    }
}
