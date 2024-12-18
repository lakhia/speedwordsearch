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
        var map: HashMap<String, Int> = HashMap()
        var levels : HashMap<String, SubLevel?> = HashMap()

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
        val subLevel = SubLevel("name", 1, 1)

        progress.init(storage, Rect(), 0.0f)
        progress.currentSubLevel = subLevel
        subLevel.levels[0].totalScore = 150
        subLevel.levels[0].score = 150
        subLevel.levels[0].timeUsed = 90

        progress.stopLevel(subLevel.levels[0])
        assertEquals(1, storage.levels["name"]!!.number)
        assertEquals(2.5f, subLevel.levels[0].stars, 0.005f)
        assertEquals(true, subLevel.levels[0].won)
        assertEquals(0.25f, subLevel.stars)
        assertEquals(15, subLevel.score)
        assertEquals(false, subLevel.won)
    }
}
