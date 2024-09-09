package creationsahead.speedwordsearch

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AnswerMapTest {
    @Test
    fun test_validate() {
        val selection = Selection(0, 0, Direction.EAST, 4)
        val answerMap = AnswerMap()
        answerMap.add(Answer(selection, "test"))

        assertFalse(answerMap.validate("test"))
        assertFalse(answerMap.validate("tests"))
        assertFalse(answerMap.validate("testing"))
        assertFalse(answerMap.validate("tes"))
        assertTrue(answerMap.validate("tee"))
    }

    @Test
    fun test_is_solved() {
        val selection = Selection(0, 0, Direction.EAST, 4)
        val answerMap = AnswerMap()
        assertTrue(answerMap.isSolved)
        answerMap.add(Answer(selection, "test"))
        assertFalse(answerMap.isSolved)
    }
}
