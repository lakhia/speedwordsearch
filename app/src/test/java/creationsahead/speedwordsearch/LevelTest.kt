package creationsahead.speedwordsearch

import creationsahead.speedwordsearch.mod.Level
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class LevelTest {
    @Test
    fun test_01_score() {
        val level = Level("Name", 1)
        level.score = 2
        level.totalScore = 2
        level.score(60)
        assertEquals(3f, level.stars)
    }
}
