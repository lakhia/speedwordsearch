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

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ProgressTrackerTest {

    class Storage : StorageInterface {
        var map: HashMap<String, Int> = HashMap()
        //var levels : Array<Level> = Array(10, { i -> Level("", i) })
        var levels : Array<Level?> = arrayOfNulls(10)

        override fun getLevel(index: Int, name: String): Level? {
            if (index < levels.size) {
                return levels[index]
            }
            return null
        }

        override fun storeLevel(level: Level, name: String) {
            levels[level.number] = level
        }

        override fun getPreference(key: String): Int {
            return map[key] ?: return 0
        }

        override fun storePreference(key: String, score: Int) {
            map[key] = score
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
        val level = Level("", 0)
        level.totalScore = 150
        level.score = 150
        level.timeUsed = 90

        progress.init(storage, Rect(), 0.0f)
        progress.stopLevel(level)
        assertEquals(0, storage.levels[0]!!.number)
        assertEquals(2.5f, storage.levels[0]!!.stars, 0.005f)
    }
}
