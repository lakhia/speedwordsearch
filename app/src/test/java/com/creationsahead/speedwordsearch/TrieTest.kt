package com.creationsahead.speedwordsearch

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

        val config = Config(1, 1, 0)
        val seq = RandomSequencer(config, 1)

        // No wildcards
        dictionary.searchWithWildcards("ABAC", seq) { word: String ->
            assertEquals("ABAC", word)
            return@searchWithWildcards true
        }

        // 1 wildcard
        dictionary.searchWithWildcards("AB.C", seq) { word: String ->
            assertEquals("ABAC", word)
            return@searchWithWildcards true
        }

        dictionary.searchWithWildcards(".BAC", seq) { word: String ->
            assertEquals("BBAC", word)
            return@searchWithWildcards true
        }
        dictionary.searchWithWildcards(".BAC", seq) { word: String ->
            assertEquals("ABAC", word)
            return@searchWithWildcards true
        }

        // Two wildcards
        dictionary.searchWithWildcards("BB..", RandomSequencer(config, 3)) { word: String ->
            assertEquals("BBAC", word)
            return@searchWithWildcards true
        }
        dictionary.searchWithWildcards("..AC", RandomSequencer(config, 10)) { word: String ->
            assertEquals("ABAC", word)
            return@searchWithWildcards true
        }
        dictionary.searchWithWildcards("..AC", RandomSequencer(config, 30)) { word: String ->
            assertEquals("BBAC", word)
            return@searchWithWildcards true
        }
        dictionary.searchWithWildcards("..AC", RandomSequencer(config, 100)) { word: String ->
            assertEquals("ABAC", word)
            return@searchWithWildcards true
        }
        dictionary.searchWithWildcards("..AC", RandomSequencer(config, 300)) { word: String ->
            assertEquals("BBAC", word)
            return@searchWithWildcards true
        }
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

        val seq = RandomSequencer(Config(1, 1, 0), 1)

        dictionary.searchWithWildcards("C", seq) { word: String ->
            assertEquals(null, word)
            return@searchWithWildcards true
        }
        dictionary.searchWithWildcards("A", seq) { word: String ->
            assertEquals("A", word)
            return@searchWithWildcards true
        }
        dictionary.searchWithWildcards("AB", seq) { word: String ->
            assertEquals("AB", word)
            return@searchWithWildcards true
        }
        dictionary.searchWithWildcards("ABA", seq) { word: String ->
            assertEquals("ABA", word)
            return@searchWithWildcards true
        }
        dictionary.searchWithWildcards("ABAC", seq) { word: String ->
            assertEquals("ABAC", word)
            return@searchWithWildcards true
        }
        dictionary.searchWithWildcards("ABACA", seq) { word: String ->
            assertEquals("ABACA", word)
            return@searchWithWildcards true
        }
        dictionary.searchWithWildcards("ABACAB", seq) { word: String ->
            assertEquals("ABACAB", word)
            return@searchWithWildcards true
        }
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
        val seq = RandomSequencer(Config(1, 1, 0), 1)

        var counter = 0
        dictionary.searchWithWildcards("....", seq) { counter++; false; }
        assertEquals(8, counter)

        counter = 0
        dictionary.searchWithWildcards(".G..", seq) { counter++; false; }
        assertEquals(3, counter)

        counter = 0
        dictionary.searchWithWildcards("A...", seq) { counter++; false; }
        assertEquals(6, counter)

        counter = 0
        dictionary.searchWithWildcards("A...", seq) { word: String ->
            counter++; counter == 3 && word == "ADDA"; }
        assertEquals(3, counter)

        counter = 0
        dictionary.searchWithWildcards("...A", seq) { word: String ->
            counter++; counter == 4 && word == "AFFA"; }
        assertEquals(4, counter)

        counter = 0
        dictionary.searchWithWildcards(".BB.", seq) { word: String ->
            counter++; word == "ABBA"; }
        assertEquals(1, counter)

        counter = 0
        dictionary.searchWithWildcards("C...", seq) { word: String ->
            counter++; word == "CGGA"; }
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
        val seq = RandomSequencer(Config(1, 1, 0), 1)

        var counter = 0
        val buffer = StringBuffer()
        dictionary.searchWithWildcards("......", seq)
        { buffer.append(it); counter++; false; }
        assertEquals(6, counter)
        assertEquals("ABCDEFABCDEABCDABCABA", buffer.toString())


        counter = 0
        buffer.delete(0, buffer.length)
        dictionary.searchWithWildcards("A.....", seq)
        { buffer.append(it); counter++; false; }
        assertEquals(6, counter)
        assertEquals("ABCDEFABCDEABCDABCABA", buffer.toString())

        counter = 0
        buffer.delete(0, buffer.length)
        dictionary.searchWithWildcards(".B....", seq)
        { buffer.append(it); counter++; false; }
        assertEquals(6, counter)
        assertEquals("ABCDEFABCDEABCDABCABA", buffer.toString())
    }
}
