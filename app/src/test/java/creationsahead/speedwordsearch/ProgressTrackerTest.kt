package creationsahead.speedwordsearch

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
            return map.get(key) ?: return 0
        }

        override fun storePreference(key: String, score: Int) {
            map.put(key, score)
        }

        override fun getAssetInputStream(name: String): Reader? {
            return StringReader("ONE\nTWO\nTHREE")
        }
    }

    @Test
    fun test_01_init() {
        val storage = Storage()
        ProgressTracker.init(storage, 5)

        assertNotNull(ProgressTracker.config)
        assertNotNull(ProgressTracker.config.dictionary)
        assertNotNull(ProgressTracker.config.sequencer)
        assertEquals(5, ProgressTracker.config.sizeX)
        assertEquals(5, ProgressTracker.config.sizeY)

        assertNotNull(ProgressTracker.game)
    }

    @Test
    fun test_02_progress() {
        val storage = Storage()
        ProgressTracker.init(storage, 5)

        ProgressTracker.addScore(50)
        ProgressTracker.addScore(40)
        ProgressTracker.addScore(30)
        ProgressTracker.addScore(20)
        ProgressTracker.addScore(10)
        ProgressTracker.incrementLevel(10)

        assertEquals(150, ProgressTracker.getCurrentScore())
        assertEquals(1, ProgressTracker.getCurrentLevel())
        assertEquals(150, storage.map[ProgressTracker.SCORE])
        assertEquals(1, storage.map[ProgressTracker.LEVEL])

        assertNotNull(ProgressTracker.config)
        assertNotNull(ProgressTracker.config.dictionary)
        assertNotNull(ProgressTracker.config.sequencer)
        assertEquals(6, ProgressTracker.config.sizeX)
        assertEquals(6, ProgressTracker.config.sizeY)

        assertNotNull(ProgressTracker.game)
    }
}
