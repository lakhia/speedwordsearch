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
            return map[key] ?: return 0
        }

        override fun storePreference(key: String, score: Int) {
            map.put(key, score)
        }

        override fun getAssetInputStream(name: String): Reader {
            return StringReader("ONE\nTWO\nTHREE")
        }
    }

    class Score : ScoreCallback {
        var scores = ""
        override fun updateScore(score: Int) {
            scores += "," + score
        }
    }

    @Test
    fun test_01_init() {
        val storage = Storage()
        val progress = ProgressTracker.getInstance()
        progress.init(storage)

        assertNotNull(progress.config)
        assertNotNull(progress.config.dictionary)
        assertEquals(5, progress.config.sizeX)
        assertEquals(5, progress.config.sizeY)

        assertNotNull(progress.game)
    }

    @Test
    fun test_02_progress() {
        val storage = Storage()
        val score = Score()
        val progress = ProgressTracker.getInstance()
        progress.init(storage)
        progress.callback = score

        progress.addScore(50)
        progress.addScore(40)
        progress.addScore(30)
        progress.addScore(20)
        progress.addScore(10)
        assertEquals(",50,90,120,140,150", score.scores)

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
        assertEquals(",50,90,120,140,150", score.scores)
    }
}
