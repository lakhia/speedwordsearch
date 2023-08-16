package creationsahead.speedwordsearch

import org.junit.Test
import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class GameTest {
    var dictionary: Trie = Trie()
    var initialized = false

    fun init() {
        if (!initialized) {
            dictionary.insert("abac")
            dictionary.insert("bbac")
            dictionary.insert("baac")
            dictionary.insert("aaac")
            dictionary.insert("caac")
            dictionary.insert("babc")
            dictionary.insert("accc")
            dictionary.insert("abcc")
            initialized = true
        }
    }

    @Test
    fun test_01_game() {
        init()

        val game = Game(Config(4, 4, dictionary, 1))
        assertEquals(true, game.populatePuzzle(4, 1))
        assertEquals(
                "c a a a \n" +
                ". . . . \n" +
                ". . . . \n" +
                ". . . . \n", game.toString())
        assertEquals(true, game.populatePuzzle(4, 1))
        assertEquals(true, game.populatePuzzle(4, 1))
        assertEquals(true, game.populatePuzzle(4, 1))
        assertEquals(
                "c a a a \n" +
                "c a a c \n" +
                "b a a c \n" +
                "a c c c \n", game.toString())
        assertEquals(false, game.populatePuzzle(4, 1))
        assertEquals(false, game.populatePuzzle(3, 1))
        assertEquals(false, game.populatePuzzle(1, 1))
        assertEquals(false, game.populatePuzzle(0, 1))
    }

    @Test
    fun test_02_big_board() {
        init()

        val game = Game(Config(8, 8, dictionary, 1))
        assertEquals(true, game.populatePuzzle(4, 1))
        assertEquals(true, game.populatePuzzle(4, 1))
        assertEquals(true, game.populatePuzzle(4, 1))
        assertEquals(true, game.populatePuzzle(4, 1))
        assertEquals(true, game.populatePuzzle(4, 1))
        assertEquals(true, game.populatePuzzle(4, 1))
        assertEquals(true, game.populatePuzzle(4, 1))
        assertEquals(true, game.populatePuzzle(4, 1))
        assertEquals(false, game.populatePuzzle(4, 1))
        assertEquals(
                ". . . . . . . . \n" +
                ". . . c c c a . \n" +
                "c a a c . . . . \n" +
                ". . c a b b a . \n" +
                ". . . b . . b . \n" +
                ". . . c a a a . \n" +
                ". . . c . b c . \n" +
                ". . . b a a c . \n", game.toString())

        // Correct direction guessing
        assertFalse(game.guess(Selection(1, 2, Direction.EAST, 4)))
        assertFalse(game.guess(Selection(0, 2, Direction.SOUTH, 4)))
        assertTrue(game.guess(Selection(6, 3, Direction.SOUTH, 4)))
        assertTrue(game.guess(Selection(6, 1, Direction.WEST, 4)))

        // Opposite direction guessing
        assertTrue(game.guess(Selection(3, 7, Direction.EAST, 4)))

        // Palindrome
        assertTrue(game.guess(Selection(0, 2, Direction.EAST, 4)))
        assertEquals(
                ". . . . . . . . \n" +
                ". . . . . . . . \n" +
                ". . . . . . . . \n" +
                ". . c a b b . . \n" +
                ". . . b . . . . \n" +
                ". . . c a a a . \n" +
                ". . . c . b . . \n" +
                ". . . . . . . . \n", game.toString())
    }
}
