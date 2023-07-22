package creationsahead.speedwordsearch

import org.junit.Test
import org.junit.Assert.assertEquals

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

        val game: Game = Game(dictionary, 4, 1)
        assertEquals(true, game.populatePuzzle(4, 1))
        assertEquals(
                "c a a a \n" +
                ". . . . \n" +
                ". . . . \n" +
                ". . . . \n", game.toString())
        assertEquals(true, game.populatePuzzle(4, 1))
        assertEquals(true, game.populatePuzzle(4, 1))
        assertEquals(true, game.populatePuzzle(4, 1))
        assertEquals(true, game.populatePuzzle(4, 1))
        assertEquals(
                "c a a a \n" +
                "a b b c \n" +
                "a c a c \n" +
                "c c c c \n", game.toString())
        assertEquals(false, game.populatePuzzle(4, 1))
        assertEquals(false, game.populatePuzzle(3, 1))
        assertEquals(false, game.populatePuzzle(1, 1))
        assertEquals(false, game.populatePuzzle(0, 1))
    }

    @Test
    fun test_02_big_board() {
        init()

        val game: Game = Game(dictionary, 8, 1)
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
                ". . . c c c a . \n" +
                ". . . . . . . . \n" +
                "c a b b . . . . \n" +
                ". . c a a b a . \n" +
                ". . . b . . b c \n" +
                ". . . c a a a . \n" +
                ". . . c b a c . \n" +
                ". . . c c . . . \n", game.toString())
    }
}
