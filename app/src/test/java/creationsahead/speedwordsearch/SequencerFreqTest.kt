package creationsahead.speedwordsearch

import org.junit.Assert
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class SequencerFreqTest {

    /** Generic class to test array sequence */
    class GenericTester(config: Config, size: Int) : RandomSequencer(config, 1) {
        private val mSize : Int = size

        fun generic_tester(arrayFunc: () -> Iterator<Any>) {
            val map = HashMap<Any, IntArray>()
            var j = 10000
            while (j > 0) {
                val array = arrayFunc()
                var i = 0
                while (array.hasNext()) {
                    val item = array.next()
                    val freq = map.getOrDefault(item, IntArray(mSize))
                    map[item] = freq
                    freq[i]++
                    i++
                }
                j--
            }
            val array = map.values
            for (a in array) {
                val ave = a.average()
                val min = a.min() / ave
                val max = a.max() / ave
                Assert.assertTrue(min > .82)
                Assert.assertTrue(max < 1.13)
            }
        }
    }

    @Test
    fun test_02_coordinate_freq() {
        val tester = GenericTester(Config(5, 5, 5), 5)
        tester.generic_tester { tester.xCoordinateSequence }
        tester.generic_tester { tester.yCoordinateSequence }
    }

    @Test
    fun test_03_direction_freq() {
        val tester = GenericTester(Config(5, 5, 5), 8)
        tester.generic_tester { tester.directionSequence }
    }

    @Test
    fun test_04_letter_freq() {
        val tester = GenericTester(Config(5, 5, 5), 19)
        tester.generic_tester { tester.letterSequence }
    }
}