package creationsahead.speedwordsearch

import org.junit.Test
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class PositionTest {
    @Test
    fun test_01_corner_south() {
        val pos = Position(0, 0, Direction.SOUTH)
        assertTrue(pos.inBounds(1, 1, 1))
        assertFalse(pos.inBounds(1, 1, 2))

        assertTrue(pos.inBounds(1, 5, 5))
        assertFalse(pos.inBounds(1, 5, 6))
        assertFalse(pos.inBounds(1, 4, 5))
    }

    @Test
    fun test_02_corner_east() {
        val pos = Position(0, 0, Direction.EAST)
        assertTrue(pos.inBounds(1, 1, 1))
        assertFalse(pos.inBounds(1, 1, 2))

        assertTrue(pos.inBounds(5, 1, 5))
        assertFalse(pos.inBounds(5, 1, 6))
        assertFalse(pos.inBounds(4, 1, 5))
    }

    @Test
    fun test_03_middle_west() {
        val pos = Position(3, 3, Direction.WEST)
        assertFalse(pos.inBounds(3, 3, 1))

        assertTrue(pos.inBounds(4, 4, 1))
        assertTrue(pos.inBounds(4, 4, 2))
        assertTrue(pos.inBounds(4, 4, 3))
        assertTrue(pos.inBounds(4, 4, 4))
    }
}
