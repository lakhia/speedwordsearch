package creationsahead.speedwordsearch

import org.junit.Assert.*
import org.junit.Test
import com.creationsahead.speedwordsearch.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class SelectionTest {
    @Test
    fun test_01_corner_south() {
        val pos = Position(0, 0)
        assertTrue(Selection.inBounds(pos, Direction.SOUTH, 1, 1, 1))
        assertFalse(Selection.inBounds(pos, Direction.SOUTH, 1, 1, 2))

        assertTrue(Selection.inBounds(pos, Direction.SOUTH, 1, 5, 5))
        assertFalse(Selection.inBounds(pos, Direction.SOUTH, 1, 5, 6))
        assertFalse(Selection.inBounds(pos, Direction.SOUTH, 1, 4, 5))
    }

    @Test
    fun test_02_corner_east() {
        val pos = Position(0, 0)
        assertTrue(Selection.inBounds(pos, Direction.EAST, 1, 1, 1))
        assertFalse(Selection.inBounds(pos, Direction.EAST, 1, 1, 2))

        assertTrue(Selection.inBounds(pos, Direction.EAST, 5, 1, 5))
        assertFalse(Selection.inBounds(pos, Direction.EAST, 5, 1, 6))
        assertFalse(Selection.inBounds(pos, Direction.EAST, 4, 1, 5))
    }

    @Test
    fun test_03_equals() {
        val selection = Selection(0, 0, Direction.EAST, 5)
        val same = Selection(0, 0, Direction.EAST, 5)
        val different1 = Selection(0, 0, Direction.SOUTH, 5)
        val different2 = Selection(0, 0, Direction.EAST, 3)
        val different3 = Selection(1, 0, Direction.EAST, 5)
        val different4 = Selection(0, 1, Direction.EAST, 5)

        assertEquals(same, same)
        assertEquals(selection, same)
        assertEquals(same, selection)

        assertNotEquals(different1, selection)
        assertNotEquals(different2, selection)
        assertNotEquals(different3, selection)
        assertNotEquals(different4, selection)

        assertNotEquals(selection, different1)
        assertNotEquals(selection, different2)
        assertNotEquals(selection, different3)
        assertNotEquals(selection, different4)
    }

    @Test
    fun test_04_equals_flipped() {
        val selection = Selection(0, 0, Direction.EAST, 5)
        val flipped = Selection(4, 0, Direction.WEST, 5)
        val notFlipped = Selection(4, 0, Direction.SOUTH, 5)

        assertEquals(selection, flipped)
        assertNotEquals(selection, notFlipped)
    }

    @Test
    fun test_05_is_valid_full() {
        var selection = Selection.isValid(0, 0, 5, 5)
        assertNotNull(selection)
        assertEquals(6, selection!!.length)
        assertEquals(Direction.SOUTH_EAST, selection.direction)

        selection = Selection.isValid(4, 4, 0, 0)
        assertNotNull(selection)
        assertEquals(5, selection!!.length)
        assertEquals(Direction.NORTH_WEST, selection.direction)

        selection = Selection.isValid(0, 4, 3, 4)
        assertNotNull(selection)
        assertEquals(4, selection!!.length)
        assertEquals(Direction.EAST, selection.direction)

        selection = Selection.isValid(0, 5, 0, 0)
        assertNotNull(selection)
        assertEquals(6, selection!!.length)
        assertEquals(Direction.NORTH, selection.direction)

        selection = Selection.isValid(5, 0, 5, 5)
        assertNotNull(selection)
        assertEquals(6, selection!!.length)
        assertEquals(Direction.SOUTH, selection.direction)
    }

    @Test
    fun test_06_is_valid() {
        assertNotNull(Selection.isValid(5, 0, 0, 0))
        assertNotNull(Selection.isValid(1, 0, 6, 5))
        assertNotNull(Selection.isValid(6, 5, 1, 0))
        assertNotNull(Selection.isValid(0, 6, 5, 6))
        assertNotNull(Selection.isValid(1, 5, 1, 0))
        assertNotNull(Selection.isValid(5, 1, 5, 6))
        assertNotNull(Selection.isValid(5, 0, 0, 0))
    }

    @Test
    fun test_07_is_not_valid() {
        assertNull(Selection.isValid(0,0, 5, 4))
        assertNull(Selection.isValid(0,0, 4, 5))
        assertNull(Selection.isValid(1,0, 6, 4))
        assertNull(Selection.isValid(0,1, 4, 6))
    }
}
