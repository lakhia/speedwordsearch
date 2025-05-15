package com.creationsahead.speedwordsearch

import org.junit.Assert.assertEquals
import org.junit.Test
import com.creationsahead.speedwordsearch.mod.Level
import org.junit.Assert.assertTrue

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
        level.maxScore = 2
        level.timeUsed = 60
        level.score()
        assertEquals(2.95f, level.stars)
        assertEquals(6, level.bonus)
        assertTrue(level.won)
    }
}
