package creationsahead.speedwordsearch

import org.junit.Test
import org.junit.Assert.assertEquals
import java.util.Arrays

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class SequencerTest {
    @Test
    fun test_01_sequencer() {
        val sequencer: Sequencer = Sequencer(1, 5)
        var string = Arrays.toString(sequencer.nextCoordinateSequence)
        assertEquals("[0, 3, 4, 1, 2]", string)
        string = Arrays.toString(sequencer.nextCoordinateSequence)
        assertEquals("[3, 1, 4, 0, 2]", string)
        string = Arrays.toString(sequencer.nextCoordinateSequence)
        assertEquals("[0, 3, 4, 1, 2]", string)
        string = Arrays.toString(sequencer.nextCoordinateSequence)
        assertEquals("[0, 2, 4, 3, 1]", string)
    }

}
