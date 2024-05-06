package creationsahead.speedwordsearch

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
            map.put(key, score)
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
        progress.init(storage)

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
        storage.storeLevel(level)
        progress.init(storage)

        val selection = Selection(0, 0, Direction.NORTH, 5)
        val timeLimit = progress.config.timeLimit

        var answer = Answer(selection, "h", 50)
        answer.notifyScoreClaimed()
        answer = Answer(selection, "h", 40)
        answer.notifyScoreClaimed()
        answer = Answer(selection, "h", 30)
        answer.notifyScoreClaimed()
        answer = Answer(selection, "h", 20)
        answer.notifyScoreClaimed()
        answer = Answer(selection, "h", 10)
        answer.notifyScoreClaimed()
        assertEquals(150, progress.currentScore)

        progress.incrementLevel(5)
        assertEquals(0, storage.levels[0]!!.number)
        assertEquals(150, storage.levels[0]!!.score)
        assertEquals(4.0f, storage.levels[0]!!.stars)
        assertEquals(timeLimit - 5, storage.levels[0]!!.timeUsed)

        // Make sure level 1 is now visible after winning level 0
        assertEquals(1, storage.map[ProgressTracker.LEVEL_VISIBLE])
        assertEquals(10, progress.levels.size)
        assertNotNull(progress.levels[0])
        assertNotNull(progress.levels[1])
        assertNull(progress.levels[2])
    }

    @Test
    fun test_03_progress() {
        // Initialize storage
        storage.storePreference(ProgressTracker.LEVEL_VISIBLE, 3)
        storage.storeLevel(Level("", 0))
        storage.storeLevel(Level("", 1))
        storage.storeLevel(Level("", 2))

        progress.init(storage)

        // Verify that level[3] gets created because it is visible
        assertNotNull(progress.levels[0])
        assertNotNull(progress.levels[1])
        assertNotNull(progress.levels[2])
        assertNotNull(progress.levels[3])
        assertNull(progress.levels[4])
    }
}
