package creationsahead.speedwordsearch

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
        assertEquals(5, progress.config.sizeX)
        assertEquals(5, progress.config.sizeY)

        assertNotNull(progress.game)
    }

    @Test
    fun test_02_progress() {
        val selection = Selection(0, 0, Direction.NORTH, 5)

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

        progress.incrementLevel()

        assertEquals(150, progress.currentScore)
        assertEquals(1, progress.currentLevel)
        assertEquals(150, storage.map[ProgressTracker.SCORE])
        assertEquals(1, storage.map[ProgressTracker.LEVEL])

        assertNotNull(progress.config)
        assertNotNull(progress.config.dictionary)
        assertEquals(6, progress.config.sizeX)
        assertEquals(6, progress.config.sizeY)

        assertNotNull(progress.game)
    }
}
