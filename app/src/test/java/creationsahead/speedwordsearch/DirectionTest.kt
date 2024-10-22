package creationsahead.speedwordsearch

import org.junit.Assert.assertEquals
import org.junit.Test
import com.creationsahead.speedwordsearch.*

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

    @Test
    fun test_02_find() {
        var dir = Direction.find(0, 1)
        assertEquals(Direction.SOUTH, dir)
        dir = Direction.find(-1, 0)
        assertEquals(Direction.WEST, dir)
        dir = Direction.find(-1, -1)
        assertEquals(Direction.NORTH_WEST, dir)
        dir = Direction.find(-1, 4)
        assertEquals(Direction.NONE, dir)
    }
}
