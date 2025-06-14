package com.creationsahead.speedwordsearch

import org.junit.Assert.*
import org.junit.Test

class AnswerMapTest {
    @Test
    fun test_validate() {
        val selection = Selection(0, 0, Direction.EAST, 4)
        val answerMap = AnswerMap()
        answerMap.add(Answer(selection, "test"))

        assertFalse(answerMap.validate("test"))
        assertTrue(answerMap.validate("tests"))
        assertTrue(answerMap.validate("testing"))
        assertTrue(answerMap.validate("tes"))
        assertTrue(answerMap.validate("tee"))
    }

    @Test
    fun test_validate_with_hidden() {
        val selection = Selection(0, 0, Direction.EAST, 4)
        val answerMap = AnswerMap()
        answerMap.addHiddenAnswer(Answer(selection, "generic"))
        assertFalse(answerMap.validate("generic"))
        assertTrue(answerMap.validate("gene"))
        assertTrue(answerMap.validate("generics"))
        assertTrue(answerMap.validate("test"))
    }

    @Test
    fun test_validate_with_answered() {
        val selection = Selection(0, 0, Direction.EAST, 4)
        val answerMap = AnswerMap()
        answerMap.add(Answer(selection, "banana"))
        answerMap.pop("banana")
        assertFalse(answerMap.validate("banana"))
    }

    @Test
    fun test_is_solved() {
        val selection = Selection(0, 0, Direction.EAST, 4)
        val answerMap = AnswerMap()
        assertTrue(answerMap.isSolved)
        answerMap.add(Answer(selection, "test"))
        assertFalse(answerMap.isSolved)
        answerMap.pop("test")
        assertTrue(answerMap.isSolved)
    }

    @Test
    fun test_get_answers() {
        val selection = Selection(0, 0, Direction.EAST, 4)
        val answerMap = AnswerMap()
        val testAns = Answer(selection, "test")
        val otherAns = Answer(selection, "other")
        answerMap.add(testAns)
        answerMap.add(otherAns)
        val answers = answerMap.getAnswers(true)
        assertEquals(2, answers.size)
        assertEquals(otherAns, answers[0])
        assertEquals(testAns, answers[1])
    }
}
