package creationsahead.speedwordsearch

import creationsahead.speedwordsearch.mod.Level
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
        var level : Level? = null

        override fun getLevel(index: Int): Level? {
            if (level != null && level!!.number == index) {
                return level;
            }
            return null
        }

        override fun storeLevel(level: Level) {
            this.level = level
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

    private val storage = Storage()
    private val progress = ProgressTracker.getInstance()

    @Before
    fun init() {
        progress.init(storage)
    }

    @After
    fun cleanup() {
        progress.destroy()
    }

    @Test
    fun test_01_init() {
        assertNotNull(progress.config)
        assertNotNull(progress.config.dictionary)
        assertEquals(4, progress.config.sizeX)
        assertEquals(4, progress.config.sizeY)

        assertNotNull(progress.game)
    }

    @Test
    fun test_02_progress() {
        val selection = Selection(0, 0, Direction.NORTH, 5)
        var timeLimit = progress.config.timeLimit

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
        assertEquals(0, storage.level!!.number)
        assertEquals(150, storage.level!!.score)
        assertEquals(3.0f, storage.level!!.stars)
        assertEquals(timeLimit - 5, storage.level!!.timeUsed)

        assertEquals(0, progress.currentScore)
        assertEquals(1, progress.currentLevel)
        assertEquals(1, storage.map[ProgressTracker.LEVEL])
        assertEquals(1, storage.map[ProgressTracker.LEVEL_VISIBLE])

        assertNotNull(progress.config)
        assertNotNull(progress.config.dictionary)
        assertEquals(5, progress.config.sizeX)
        assertEquals(5, progress.config.sizeY)

        assertNotNull(progress.game)
    }
}
