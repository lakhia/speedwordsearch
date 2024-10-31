package com.creationsahead.speedwordsearch

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class LevelTrackerTest {

    private lateinit var storage: ProgressTrackerTest.Storage

    @Before
    fun init() {
        storage = ProgressTrackerTest.Storage()
    }

    @Test
    fun test_init() {
        LevelTracker.init(storage, "Basic Level")
        assertNotNull(LevelTracker.levels)
        assertEquals(1, LevelTracker.levels.size)
        assertNotNull(LevelTracker.levels[0])
    }
}
