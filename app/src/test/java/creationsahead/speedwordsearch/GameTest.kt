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
            dictionary.insert("ABAC")
            dictionary.insert("BBAC")
            dictionary.insert("BAAC")
            dictionary.insert("AAAC")
            dictionary.insert("CAAC")
            dictionary.insert("BABC")
            dictionary.insert("ACCC")
            dictionary.insert("ABCC")
            initialized = true
        }
    }

    @Test
    fun test_01_game() {
        init()

        val game = Game(Config(4, 4, dictionary, 1))
        assertEquals(true, game.populatePuzzle(4, 1))
        assertEquals(
                "C A A A \n" +
                ". . . . \n" +
                ". . . . \n" +
                ". . . . \n", game.toString())
        assertEquals(true, game.populatePuzzle(4, 1))
        assertEquals(true, game.populatePuzzle(4, 1))
        assertEquals(true, game.populatePuzzle(4, 1))
        assertEquals(
                "C A A A \n" +
                "C A A C \n" +
                "B A A C \n" +
                "A C C C \n", game.toString())
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
                ". . . C C C A . \n" +
                "C A A C . . . . \n" +
                ". . C A B B A . \n" +
                ". . . B . . B . \n" +
                ". . . C A A A . \n" +
                ". . . C . B C . \n" +
                ". . . B A A C . \n", game.toString())

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
                ". . C A B B . . \n" +
                ". . . B . . . . \n" +
                ". . . C A A A . \n" +
                ". . . C . B . . \n" +
                ". . . . . . . . \n", game.toString())
    }
}
