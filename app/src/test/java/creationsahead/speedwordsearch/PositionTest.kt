package creationsahead.speedwordsearch

import org.junit.Assert.*
import org.junit.Test

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

    @Test
    fun test_04_distance() {
        var pos = Position(3, 3, Direction.EAST)
        assertEquals(0, pos.getDistanceToBoundary(3, 3))
        assertEquals(3, pos.getDistanceToBoundary(6, 6))
        assertEquals(0, pos.getDistanceToBoundary(3, 6))
        assertEquals(3, pos.getDistanceToBoundary(6, 3))

        pos = Position(3, 3, Direction.WEST)
        assertEquals(3, pos.getDistanceToBoundary(3, 3))
        assertEquals(3, pos.getDistanceToBoundary(6, 6))
        assertEquals(3, pos.getDistanceToBoundary(3, 6))
        assertEquals(3, pos.getDistanceToBoundary(6, 3))

        pos = Position(3, 3, Direction.SOUTH)
        assertEquals(0, pos.getDistanceToBoundary(3, 3))
        assertEquals(3, pos.getDistanceToBoundary(6, 6))
        assertEquals(3, pos.getDistanceToBoundary(3, 6))
        assertEquals(0, pos.getDistanceToBoundary(6, 3))

        pos = Position(3, 3, Direction.NORTH)
        assertEquals(3, pos.getDistanceToBoundary(3, 3))
        assertEquals(3, pos.getDistanceToBoundary(6, 6))
        assertEquals(3, pos.getDistanceToBoundary(3, 6))
        assertEquals(3, pos.getDistanceToBoundary(6, 3))

        pos = Position(3, 3, Direction.SOUTH_EAST)
        assertEquals(0, pos.getDistanceToBoundary(3, 3))
        assertEquals(3, pos.getDistanceToBoundary(6, 6))
        assertEquals(0, pos.getDistanceToBoundary(3, 6))
        assertEquals(0, pos.getDistanceToBoundary(6, 3))
        assertEquals(3, pos.getDistanceToBoundary(6, 8))
        assertEquals(3, pos.getDistanceToBoundary(8, 6))

        pos = Position(3, 3, Direction.NORTH_WEST)
        assertEquals(3, pos.getDistanceToBoundary(3, 3))
        pos = Position(2, 3, Direction.NORTH_WEST)
        assertEquals(2, pos.getDistanceToBoundary(3, 3))
        pos = Position(3, 2, Direction.NORTH_WEST)
        assertEquals(2, pos.getDistanceToBoundary(3, 3))
    }
}
