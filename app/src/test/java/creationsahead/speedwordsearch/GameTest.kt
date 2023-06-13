package creationsahead.speedwordsearch

import org.junit.Test
import org.junit.Assert.assertEquals

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class GameTest {
    @Test
    fun test_01_game() {
        val dictionary: Trie = Trie()
        dictionary.insert("abac")
        dictionary.insert("bbac")
        dictionary.insert("baac")
        dictionary.insert("aaac")

        val game: Game = Game(dictionary)
        game.populatePuzzle()
        println(game.toString())
    }

}
