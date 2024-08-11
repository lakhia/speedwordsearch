package creationsahead.speedwordsearch

import android.graphics.Rect
import creationsahead.speedwordsearch.mod.Level
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.io.Reader
import java.io.StringReader

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

        override fun getLevel(index: Int): Level? {
            if (index < levels.size) {
                return levels[index]
            }
            return null
        }

        override fun storeLevel(level: Level) {
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

        assertNotNull(progress.config)
        assertEquals(4, progress.config.sizeX)
        assertEquals(4, progress.config.sizeY)

        // Make sure exactly one level is visible when game starts
        assertNotNull(progress.game)
        assertNotNull(progress.levels[0])
        assertNull(progress.levels[1])
        assertEquals(10, progress.levels.size)
    }

    @Test
    fun test_02_progress() {
        // Initialize storage
        val level = Level("", 0)
        level.totalScore = 150
        level.timeUsed = 90
        storage.storeLevel(level)
        progress.init(storage, Rect(), 0.0f)

        progress.currentLevel.score = 15
        progress.currentLevel.totalScore = 15
        progress.stopLevel(level)
        assertEquals(0, storage.levels[0]!!.number)
        assertEquals(2.5f, storage.levels[0]!!.stars, 0.005f)

        // Make sure level 1 is now visible after winning level 0
        assertEquals(1, storage.map[ProgressTracker.LEVEL_VISIBLE])
        assertEquals(10, progress.levels.size)
        assertNotNull(progress.levels[0])
        assertNotNull(progress.levels[1])
        assertNull(progress.levels[2])
        assertEquals(1, storage.map[ProgressTracker.LEVEL_VISIBLE])
    }

    @Test
    fun test_03_progress() {
        // Initialize storage
        storage.storePreference(ProgressTracker.LEVEL_VISIBLE, 3)
        storage.storeLevel(Level("", 0))
        storage.storeLevel(Level("", 1))
        storage.storeLevel(Level("", 2))

        progress.init(storage, Rect(), 0.0f)

        // Verify that level[3] gets created because it is visible
        assertNotNull(progress.levels[0])
        assertNotNull(progress.levels[1])
        assertNotNull(progress.levels[2])
        assertNotNull(progress.levels[3])
        assertNull(progress.levels[4])
    }
}
