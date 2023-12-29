package creationsahead.speedwordsearch

import org.junit.Assert
import org.junit.Test
import kotlin.collections.HashMap

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class SequencerFreqTest {

    private fun test_bonus(difficulty : Int): Double {
        val times = 1000.0
        val sequencer = RandomSequencer(Config(5, 5, Trie(), difficulty), 1)
        var j : Int = times.toInt()
        var sum = 0.0
        while (j>0) {
            sum += sequencer.getBonus(j)
            j--
        }
        println("bonus " + sum/times)
        return sum / times
    }

    @Test
    fun test_01_bonus_low_difficulty() {
        var ave = test_bonus(0)
        Assert.assertTrue(ave < 5 && ave > 0)

        ave = test_bonus(25)
        Assert.assertTrue(ave < 6 && ave > 4)

        ave = test_bonus(50)
        Assert.assertTrue(ave < 10 && ave > 8)

        ave = test_bonus(75)
        Assert.assertTrue(ave < 14 && ave > 12)

        ave = test_bonus(100)
        Assert.assertTrue(ave < 17 && ave > 15)
    }

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
                    val freq = map.getOrDefault(item, kotlin.IntArray(mSize))
                    map.put(item, freq)
                    freq[i]++
                    i++
                }
                j--
            }
            val array = map.values
            for (a in array) {
                val ave = a.average()
                val min = a.min()!! / ave
                val max = a.max()!! / ave
                Assert.assertTrue(min > .82)
                Assert.assertTrue(max < 1.13)
            }
        }
    }

    @Test
    fun test_02_coordinate_freq() {
        val tester = GenericTester(Config(5, 5, Trie(), 5), 5)
        tester.generic_tester { tester.xCoordinateSequence }
        tester.generic_tester { tester.yCoordinateSequence }
    }

    @Test
    fun test_03_direction_freq() {
        val tester = GenericTester(Config(5, 5, Trie(), 5), 8)
        tester.generic_tester { tester.directionSequence }
    }

    @Test
    fun test_04_letter_freq() {
        val tester = GenericTester(Config(5, 5, Trie(), 5), 19)
        tester.generic_tester { tester.letterSequence }
    }
}