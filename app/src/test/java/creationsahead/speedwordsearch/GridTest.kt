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
            config,
            Scoring(),
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
    fun test_01_duplicates() {
        val config = Config(6, 3, 1)
        val scoring = Scoring()
        val grid = Grid(
            config,
            scoring,
            RandomSequencer(config, 1)
        )
        val empty =
                ". . . . . . \n" +
                ". . . . . . \n" +
                ". . . . . . \n"
        assertEquals(empty, grid.toString())

        // Add word, add again, remove
        grid.addWord(Selection(1, 0, Direction.EAST, 4), "test")
        assertFalse(grid.addWord(Selection(2, 0, Direction.EAST, 4), "test"))
        assertTrue(grid.removeWord("test"))

        // Add again
        assertTrue(grid.addWord(Selection(1, 0, Direction.EAST, 4), "test"))
        assertFalse(grid.addWord(Selection(1, 0, Direction.EAST, 5), "tests"))
        assertTrue(grid.removeWord("test"))

        // Substring tests
        assertFalse(grid.addWord(Selection(1, 0, Direction.EAST, 6), "tester"))
        assertTrue(grid.addWord(Selection(0, 0, Direction.EAST, 6), "tester"))
        assertFalse(grid.addWord(Selection(0, 0, Direction.EAST, 4), "test"))
        assertTrue(grid.removeWord("tester"))
        assertTrue(grid.addWord(Selection(1, 0, Direction.EAST, 5), "tests"))
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

        grid.removeWord("saga")
        grid.removeWord("east")
        grid.removeWord("afar")
        string = grid.findContents(Selection(0, 1, Direction.EAST, 4), true)
        assertEquals("...a", string)
        string = grid.findContents(Selection(0, 1, Direction.EAST, 4), false)
        assertEquals("saga", string)
        string = grid.findContents(Selection(0, 2, Direction.EAST, 4), true)
        assertEquals("...r", string)
        string = grid.findContents(Selection(0, 2, Direction.EAST, 4), false)
        assertEquals("afar", string)
    }

    @Test
    fun test_03_insert_diagonal() {
        val config = Config(4, 4, 1)
        val scoring = Scoring()
        val grid = Grid(
            config,
            scoring,
            RandomSequencer(config, 1)
        )
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
                "k a k t \n", grid.toString())

        // Remove non-existent words
        assertFalse(grid.removeWord("you"))
        assertFalse(grid.removeWord("yot"))
        assertFalse(grid.removeWord("kot"))
        assertFalse(grid.removeWord("tok"))
        assertFalse(grid.removeWord("kakt"))
        assertEquals(string, grid.toString())

        // Remove word
        grid.removeWord("test")
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
            config,
            Scoring(),
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
        grid.removeWord("tart")
        pos = grid.findCells({ obj: Cell -> obj.isUnused }, null)
        assertEquals(null, pos)
        grid.removeWord("east")
        pos = grid.findCells({ obj: Cell -> obj.isUnused }, null)
        assertEquals(null, pos)

        // Remove one word that makes a few cells unused
        grid.removeWord("saga")
        pos = grid.findCells({ obj: Cell -> obj.isUnused }, null)
        assertNotNull(pos)
        assertEquals(3, pos?.x)
        assertEquals(1, pos?.y)

        // Test cell
        var cell = grid.getCell(3, 1)
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

        grid.removeWord("afar")
        content = grid.findContents(Selection(1, 0, Direction.SOUTH, 4), true)
        assertEquals("ea.k", content)
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
