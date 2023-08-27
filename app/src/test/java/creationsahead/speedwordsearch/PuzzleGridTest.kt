package creationsahead.speedwordsearch

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class PuzzleGridTest {
    fun fill_grid(): PuzzleGrid {
        val config = Config(4, 4, null, 0)
        val grid = PuzzleGrid(config)
        grid.addWord(Selection(0, 0, Direction.EAST, 4), "test")
        grid.addWord(Selection(3, 0, Direction.SOUTH, 4), "tart")
        grid.addWord(Selection(3, 3, Direction.WEST, 4), "take")
        grid.addWord(Selection(0, 3, Direction.NORTH, 4), "east")
        grid.addWord(Selection(0, 1, Direction.EAST, 4), "saga")
        grid.addWord(Selection(0, 2, Direction.EAST, 4), "afar")
        return grid
    }

    @Test
    fun test_01_duplicates() {
        val config = Config(6, 3, null, 0)
        val grid = PuzzleGrid(config)
        val empty =
                ". . . . . . \n" +
                ". . . . . . \n" +
                ". . . . . . \n"
        assertEquals(empty, grid.toString())

        // Add word, add again, remove
        grid.addWord(Selection(1, 0, Direction.EAST, 4), "test")
        assertFalse(grid.addWord(Selection(2, 0, Direction.EAST, 4), "test"))
        assertTrue(grid.removeWord("test"))
        assertEquals(empty, grid.toString())

        // Add again
        assertTrue(grid.addWord(Selection(1, 0, Direction.EAST, 4), "test"))
        assertTrue(grid.removeWord("test"))
        assertEquals(empty, grid.toString())
    }

    @Test
    fun test_02_insert_straight() {
        val grid = fill_grid()
        var string = grid.toString()
        assertEquals(
                "t e s t \n" +
                "s a g a \n" +
                "a f a r \n" +
                "e k a t \n", string)

        grid.removeWord("saga")
        grid.removeWord("east")
        grid.removeWord("afar")
        string = grid.toString()
        assertEquals(
                "t e s t \n" +
                ". . . a \n" +
                ". . . r \n" +
                "e k a t \n", string)
    }

    @Test
    fun test_03_insert_diagonal() {
        val config = Config(4, 4, null, 0)
        val grid = PuzzleGrid(config)
        grid.addWord(Selection(0, 0, Direction.SOUTH_EAST, 4), "test")
        grid.addWord(Selection(2, 0, Direction.SOUTH_WEST, 3), "yes")
        grid.addWord(Selection(1, 3, Direction.NORTH_EAST, 3), "ask")
        grid.addWord(Selection(2, 2, Direction.NORTH_WEST, 3), "set")
        grid.addWord(Selection(3, 2, Direction.SOUTH_WEST, 2), "ok")
        grid.addWord(Selection(0, 0, Direction.EAST, 3), "toy")
        grid.addWord(Selection(0, 0, Direction.SOUTH, 4), "task")
        var string = grid.toString()
        assertEquals(
                "t o y . \n" +
                "a e . k \n" +
                "s . s o \n" +
                "k a k t \n", string)

        // Remove non-existent words
        assertFalse(grid.removeWord("you"))
        assertFalse(grid.removeWord("yot"))
        assertFalse(grid.removeWord("kot"))
        assertFalse(grid.removeWord("tok"))
        assertFalse(grid.removeWord("kakt"))
        assertEquals(string, grid.toString())

        // Remove words
        grid.removeWord("test")
        string = grid.toString()
        assertEquals(
                "t o y . \n" +
                "a e . k \n" +
                "s . s o \n" +
                "k a k . \n", string)
    }

    @Test
    fun test_04_no_successful_inserts() {
        val config = Config(4, 4, null, 0)
        val grid = PuzzleGrid(config)
        grid.addWord(Selection(1, 0, Direction.EAST, 4), "test")
        grid.addWord(Selection(0, 1, Direction.SOUTH, 4), "test")
        grid.addWord(Selection(2, 0, Direction.WEST, 4), "test")
        grid.addWord(Selection(0, 2, Direction.NORTH, 4), "test")
        grid.addWord(Selection(1, 0, Direction.WEST, 3), "bam")
        grid.addWord(Selection(0, 1, Direction.NORTH, 3), "bam")
        val string = grid.toString()
        assertEquals(
                ". . . . \n" +
                ". . . . \n" +
                ". . . . \n" +
                ". . . . \n", string)
    }

    @Test
    fun test_05_vacant_cell() {
        val grid = fill_grid()

        // No vacant cells
        var pos = grid.findEmptyCell(null)
        assertEquals(null, pos)

        // Remove one word that does not clear any cells
        grid.removeWord("tart")
        pos = grid.findEmptyCell(null)
        assertEquals(null, pos)

        // Remove one word that does not clear any cells
        grid.removeWord("afar")

        pos = grid.findEmptyCell(null)
        assertEquals(3, pos.x)
        assertEquals(2, pos.y)
    }

    @Test
    fun test_06_contents() {
        val grid = fill_grid()

        var content: String
        content = grid.findContents(Selection(3,3, Direction.WEST, 4), true)
        assertEquals("take", content)
        content = grid.findContents(Selection(3,2, Direction.NORTH_WEST, 3), true)
        assertEquals("rge", content)
        content = grid.findContents(Selection(0,0, Direction.SOUTH_EAST, 4), true)
        assertEquals("taat", content)
        content = grid.findContents(Selection(0,3, Direction.NORTH, 4), true)
        assertEquals("east", content)

        grid.removeWord("afar")
        content = grid.findContents(Selection(1,0, Direction.SOUTH, 4), true)
        assertEquals("ea.k", content)
    }
}
