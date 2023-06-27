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
            initialized = true
        }
    }

    @Test
    fun test_01_game() {
        init()

        val game: Game = Game(dictionary, 4, 1)
        game.populatePuzzle(1)
        assertEquals(
                "c a a a \n" +
                ". . . . \n" +
                ". . . . \n" +
                ". . . . \n", game.toString())
        game.populatePuzzle(3)
        assertEquals(
                "c a a a \n" +
                "b b a c \n" +
                ". . . . \n" +
                ". . . . \n", game.toString())
    }

}
