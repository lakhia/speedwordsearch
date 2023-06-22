package creationsahead.speedwordsearch

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class DirectionTest {
    @Test
    fun test_01_negate() {
        assertEquals(Direction.NORTH, Direction.SOUTH.negate())
        assertEquals(Direction.SOUTH, Direction.NORTH.negate())
        assertEquals(Direction.EAST, Direction.WEST.negate())
        assertEquals(Direction.WEST, Direction.EAST.negate())
        assertEquals(Direction.NORTH_WEST, Direction.SOUTH_EAST.negate())
        assertEquals(Direction.NORTH_EAST, Direction.SOUTH_WEST.negate())
        assertEquals(Direction.SOUTH_WEST, Direction.NORTH_EAST.negate())
        assertEquals(Direction.SOUTH_EAST, Direction.NORTH_WEST.negate())
    }
}
