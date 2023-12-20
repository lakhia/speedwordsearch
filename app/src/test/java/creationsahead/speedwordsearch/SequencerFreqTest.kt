package creationsahead.speedwordsearch

import org.junit.Assert
import org.junit.Test
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class SequencerFreqTest {

    private fun test_bonus(difficulty : Int): Double {
        val times = 1000.0
        val sequencer = RandomSequencer(Config(5, 5, Trie(), difficulty))
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
        Assert.assertTrue(ave < 5 && ave > 0)

        ave = test_bonus(50)
        Assert.assertTrue(ave < 10 && ave > 8)

        ave = test_bonus(75)
        Assert.assertTrue(ave < 14 && ave > 12)

        ave = test_bonus(100)
        Assert.assertTrue(ave < 17 && ave > 15)
    }

    /** Generic class to test array sequence */
    class GenericTester(config: Config) : RandomSequencer(config) {

        fun generic_tester(arrayFunc: () -> IntArray) {
            var array = arrayFunc()
            array = Arrays.copyOf(array, array.size)
            var j = 100000
            while (j > 0) {
                val newArray = arrayFunc()
                for (i in newArray.indices) {
                    array[i] += newArray[i]
                }
                j--
            }
            val min = array.min()!!
            val max = array.max()!!
            val ave = array.average()
            assert(min / ave > .9)
            assert(max / ave < 1.1)
            println(Arrays.toString(array))
            println(min / ave)
            println(max / ave)
        }
    }

    @Test
    fun test_02_coordinate_freq() {
        val tester = GenericTester(Config(5, 5, Trie(), 5))
        tester.generic_tester { tester.xCoordinateSequence }
        tester.generic_tester { tester.yCoordinateSequence }
    }

    @Test
    fun test_03_direction_freq() {
        val tester = GenericTester(Config(5, 5, Trie(), 5))
        tester.generic_tester { tester.directionSequence }
    }

    @Test
    fun test_04_letter_freq() {
        val tester = GenericTester(Config(5, 5, Trie(), 5))
        tester.generic_tester { tester.letterSequence }
    }
}