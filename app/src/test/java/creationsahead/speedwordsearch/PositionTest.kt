package creationsahead.speedwordsearch

import org.junit.Assert.*
import org.junit.Test
import com.creationsahead.speedwordsearch.*

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
}
