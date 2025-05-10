package com.creationsahead.speedwordsearch

import android.graphics.Rect
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.io.Reader
import java.io.StringReader
import com.creationsahead.speedwordsearch.mod.Level
import com.creationsahead.speedwordsearch.mod.SubLevel

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ProgressTrackerTest {

    class Storage : StorageInterface {
        private var map: HashMap<String, Int> = HashMap()
        private var levels : HashMap<String, SubLevel?> = HashMap()

        override fun getSubLevel(name: String): SubLevel? {
            return levels[name]
        }

        override fun storeSubLevel(level: SubLevel) {
            levels[level.name] = level
        }

        override fun getPreference(key: String): Int {
            return map[key] ?: return 0
        }

        override fun getAssetInputStream(name: String): Reader {
            return StringReader("ONE\nTWO\nTHREE")
        }
    }

    private lateinit var storage: Storage
    private val progress = ProgressTracker.getInstance()

    @Before
    fun init() {
        storage = Storage()
    }

    @After
    fun cleanup() {
        progress.destroy()
    }

    @Test
    fun test_01_init() {
        progress.init(storage, Rect(), 0.0f)
        assertNull(progress.config)
        assertNull(progress.game)
        progress.createGame(Level())

        assertNotNull(progress.config)
        assertEquals(4, progress.config.sizeX)
        assertEquals(4, progress.config.sizeY)
        assertNotNull(progress.game)
    }

    @Test
    fun test_02_progress() {
        // Initialize storage
        val subLevel = SubLevel("name", 1, 2)
        subLevel.levels[0].maxScore = 150
        subLevel.levels[0].score = 150
        subLevel.levels[0].timeUsed = 90

        // Act
        subLevel.levels[0].score()
        subLevel.score()

        // Assert
        assertEquals(3.95f, subLevel.levels[0].stars, 0.005f)
        assertEquals(3, subLevel.levels[0].bonus)
        assertEquals(true, subLevel.levels[0].won)
        assertEquals(0.295f, subLevel.stars, 0.005f)
        assertEquals(3, subLevel.bonus)
        assertEquals(150, subLevel.score)
        assertEquals(false, subLevel.won)

        // Act
        subLevel.levels[1].maxScore = 100
        subLevel.levels[1].score = 50
        subLevel.levels[1].timeUsed = 110
        subLevel.levels[1].score()
        subLevel.score()

        // Assert
        assertEquals(2f, subLevel.levels[1].stars, 0.005f)
        assertEquals(1, subLevel.levels[1].bonus)
        assertEquals(false, subLevel.levels[1].won)
        assertEquals(0.595f, subLevel.stars, 0.005f)
        assertEquals(4, subLevel.bonus)
        assertEquals(200, subLevel.score)
        assertEquals(false, subLevel.won)
    }
}
