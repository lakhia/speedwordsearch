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
    fun test_01_origin() {
        val pos = Position(0, 0)
        assertTrue(pos.inBounds(1, 1))
        assertFalse(pos.inBounds(0, 0))
        assertFalse(pos.inBounds(1, 0))
        assertFalse(pos.inBounds(0, 1))
    }

    @Test
    fun test_02_corner_east() {
        val pos = Position(0, 3)
        assertTrue(pos.inBounds(1, 4))
        assertFalse(pos.inBounds(1, 3))

        assertTrue(pos.inBounds(5, 5))
        assertFalse(pos.inBounds(0, 4))
    }

    @Test
    fun test_03_corner_south() {
        val pos = Position(3, 3)
        assertTrue(pos.inBounds(4, 4))
        assertTrue(pos.inBounds(5, 4))
        assertTrue(pos.inBounds(4, 5))
        assertFalse(pos.inBounds(3, 3))
        assertFalse(pos.inBounds(3, 4))
        assertFalse(pos.inBounds(4, 3))
    }

    @Test
    fun test_04_distance() {
        var pos = Position(3, 3)
        assertEquals(0, pos.getDistanceToBoundary(Direction.EAST, 3, 3))
        assertEquals(3, pos.getDistanceToBoundary(Direction.EAST, 6, 6))
        assertEquals(0, pos.getDistanceToBoundary(Direction.EAST, 3, 6))
        assertEquals(3, pos.getDistanceToBoundary(Direction.EAST, 6, 3))

        pos = Position(3, 3)
        assertEquals(3, pos.getDistanceToBoundary(Direction.WEST, 3, 3))
        assertEquals(3, pos.getDistanceToBoundary(Direction.WEST, 6, 6))
        assertEquals(3, pos.getDistanceToBoundary(Direction.WEST, 3, 6))
        assertEquals(3, pos.getDistanceToBoundary(Direction.WEST, 6, 3))

        pos = Position(3, 3)
        assertEquals(0, pos.getDistanceToBoundary(Direction.SOUTH, 3, 3))
        assertEquals(3, pos.getDistanceToBoundary(Direction.SOUTH, 6, 6))
        assertEquals(3, pos.getDistanceToBoundary(Direction.SOUTH, 3, 6))
        assertEquals(0, pos.getDistanceToBoundary(Direction.SOUTH, 6, 3))

        pos = Position(3, 3)
        assertEquals(3, pos.getDistanceToBoundary(Direction.NORTH, 3, 3))
        assertEquals(3, pos.getDistanceToBoundary(Direction.NORTH, 6, 6))
        assertEquals(3, pos.getDistanceToBoundary(Direction.NORTH, 3, 6))
        assertEquals(3, pos.getDistanceToBoundary(Direction.NORTH, 6, 3))

        pos = Position(3, 3)
        assertEquals(0, pos.getDistanceToBoundary(Direction.SOUTH_EAST, 3, 3))
        assertEquals(3, pos.getDistanceToBoundary(Direction.SOUTH_EAST, 6, 6))
        assertEquals(0, pos.getDistanceToBoundary(Direction.SOUTH_EAST, 3, 6))
        assertEquals(0, pos.getDistanceToBoundary(Direction.SOUTH_EAST, 6, 3))
        assertEquals(3, pos.getDistanceToBoundary(Direction.SOUTH_EAST, 6, 8))
        assertEquals(3, pos.getDistanceToBoundary(Direction.SOUTH_EAST, 8, 6))

        pos = Position(3, 3)
        assertEquals(3, pos.getDistanceToBoundary(Direction.NORTH_WEST, 3, 3))
        pos = Position(2, 3)
        assertEquals(2, pos.getDistanceToBoundary(Direction.NORTH_WEST, 3, 3))
        pos = Position(3, 2)
        assertEquals(2, pos.getDistanceToBoundary(Direction.NORTH_WEST, 3, 3))
    }
}
