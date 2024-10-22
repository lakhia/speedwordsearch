package creationsahead.speedwordsearch

import org.junit.Assert.assertEquals
import org.junit.Test
import com.creationsahead.speedwordsearch.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class AnswerTest {
    @Test
    fun test_01_new_objects() {
        val selection = Selection(0, 0, Direction.NORTH, 5)
        var answer = Answer(selection, "h")
        assertEquals(11, answer.score)
        answer = Answer(selection, "hh")
        assertEquals(10, answer.score)
        answer = Answer(selection, "hhh")
        assertEquals(10, answer.score)
        answer = Answer(selection, "hhhh")
        assertEquals(9, answer.score)
        answer = Answer(selection, "hhhhh")
        assertEquals(9, answer.score)
    }
}
