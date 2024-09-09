package creationsahead.speedwordsearch

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class GridTest {

    private fun fillGrid(): Grid {
        val config = Config(4, 4, 1)
        val grid = Grid(
            4, 4,
            RandomSequencer(config, 1)
        )
        grid.addWord(Selection(0, 0, Direction.EAST, 4), "test")
        grid.addWord(Selection(3, 0, Direction.SOUTH, 4), "tart")
        grid.addWord(Selection(3, 3, Direction.WEST, 4), "take")
        grid.addWord(Selection(0, 3, Direction.NORTH, 4), "east")
        grid.addWord(Selection(0, 1, Direction.EAST, 4), "saga")
        grid.addWord(Selection(0, 2, Direction.EAST, 4), "afar")
        return grid
    }

    @Test
    fun test_02_contents_after_remove() {
        val grid = fillGrid()
        var string = grid.toString()
        assertEquals(
                "t e s t \n" +
                "s a g a \n" +
                "a f a r \n" +
                "e k a t \n", string)

        val selection1st = Selection(0, 1, Direction.EAST, 4)
        val selection2nd = Selection(0, 2, Direction.EAST, 4)
        grid.removeWord(Answer(selection1st, "saga"))
        grid.removeWord(Answer(selection2nd, "afar"))
        string = grid.findContents(selection1st, true)
        assertEquals("s..a", string)
        string = grid.findContents(selection1st, false)
        assertEquals("saga", string)
        string = grid.findContents(selection2nd, true)
        assertEquals("a..r", string)
        string = grid.findContents(selection2nd, false)
        assertEquals("afar", string)
    }

    @Test
    fun test_03_insert_diagonal() {
        val config = Config(4, 4, 1)
        val grid = Grid(
            4, 4,
            RandomSequencer(config, 1)
        )
        val selectionOrigin = Selection(0, 0, Direction.SOUTH_EAST, 4)
        grid.addWord(selectionOrigin, "test")
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
                "k a k t \n", grid.toString())

        // Remove word
        grid.removeWord(Answer(selectionOrigin, "test"))
        string = grid.toString()
        assertEquals(string, grid.toString())

        // Add different word that uses placeholder
        grid.addWord(Selection(0, 0, Direction.SOUTH_EAST, 4), "tess")
        assertEquals(
                "t o y . \n" +
                "a e . k \n" +
                "s . s o \n" +
                "k a k s \n", grid.toString())
    }

    @Test
    fun test_04_no_successful_inserts() {
        val config = Config(4, 4, 1)
        val grid = Grid(
            4, 4,
            RandomSequencer(config, 1)
        )
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
        val grid = fillGrid()

        // No vacant cells
        var pos = grid.findCells({ obj: Cell -> obj.isUnused }, null)
        assertEquals(null, pos)

        // Remove two words that do not free any cells
        grid.removeWord(Answer(Selection(3, 0, Direction.SOUTH, 4), "tart"))
        pos = grid.findCells({ obj: Cell -> obj.isUnused }, null)
        assertEquals(null, pos)

        // Remove one word that makes a few cells unused
        grid.removeWord(Answer(Selection(0, 1, Direction.EAST, 4), "saga"))
        pos = grid.findCells({ obj: Cell -> obj.isUnused }, null)
        assertNotNull(pos)
        assertEquals(1, pos?.x)
        assertEquals(1, pos?.y)

        // Test cell
        var cell = grid.getCell(1, 1)
        assertFalse(cell.isEmpty)
        assertTrue(cell.isUnused)
        assertEquals(Cell.EMPTY, cell.searchValue)
        assertEquals('a', cell.letter)

        cell = grid.getCell(2, 3)
        assertFalse(cell.isEmpty)
        assertFalse(cell.isUnused)
        assertEquals('a', cell.searchValue)

        // Clear unused cell
        assertTrue(grid.clearLetter(Position(3, 1)))
        assertFalse(grid.clearLetter(Position(2, 3)))

        // Test cell again
        cell = grid.getCell(3, 1)
        assertTrue(cell.isEmpty)
        assertTrue(cell.isUnused)
        assertEquals(Cell.EMPTY, cell.searchValue)
        assertEquals(Cell.EMPTY, cell.letter)

        cell = grid.getCell(2, 3)
        assertFalse(cell.isEmpty)
        assertFalse(cell.isUnused)
        assertEquals('a', cell.searchValue)
    }

    @Test
    fun test_06_contents() {
        val grid = fillGrid()

        var content = grid.findContents(Selection(3, 3, Direction.WEST, 4), true)
        assertEquals("take", content)
        content = grid.findContents(Selection(3, 2, Direction.NORTH_WEST, 3), true)
        assertEquals("rge", content)
        content = grid.findContents(Selection(0, 0, Direction.SOUTH_EAST, 4), true)
        assertEquals("taat", content)
        content = grid.findContents(Selection(0, 3, Direction.NORTH, 4), true)
        assertEquals("east", content)
        content = grid.findContents(Selection(1, 0, Direction.SOUTH, 4), true)
        assertEquals("eafk", content)
    }

    @Test
    fun test_07_get_cell() {
        val grid = fillGrid()

        // Get a cell and verify stats
        var cell = grid.getCell(0,0)
        assertEquals('t', cell.letter)
        assertFalse(cell.isUnused)
        assertFalse(cell.isEmpty)
        assertFalse(cell.store('b'))
        assertFalse(cell.storePlaceholder('b'))
        assertEquals('t', cell.letter)

        // Clear cell, then store again
        cell.erase('t')
        assertFalse(cell.isUnused)
        assertFalse(cell.isEmpty)
        cell.erase('t')
        assertTrue(cell.isUnused)
        assertFalse(cell.isEmpty)
        assertEquals('t', cell.letter)
        cell.clear()
        assertTrue(cell.isUnused)
        assertTrue(cell.isEmpty)
        assertEquals(Cell.EMPTY, cell.letter)
        assertTrue(cell.storePlaceholder('r'))
        assertTrue(cell.store('t'))

        // Test another cell
        cell = grid.getCell(3,3)
        assertEquals('t', cell.letter)

        // Add a letter
        assertFalse(grid.addLetter(Position(0, 0), 'b'))
        assertTrue(grid.addLetter(Position(0, 0), 't'))
    }
}
