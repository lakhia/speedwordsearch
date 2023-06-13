package creationsahead.speedwordsearch

import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class PuzzleGridTest {
    fun fill_grid(): PuzzleGrid {
        val grid = PuzzleGrid(4, 4)
        grid.addWord(Position(0, 0, Direction.EAST), "test")
        grid.addWord(Position(3,0, Direction.SOUTH), "tart")
        grid.addWord(Position(3,3, Direction.WEST), "take")
        grid.addWord(Position(0,3, Direction.NORTH), "east")
        grid.addWord(Position(0,1, Direction.EAST), "saga")
        grid.addWord(Position(0,2, Direction.EAST), "afar")
        return grid
    }

    @Test
    fun test_01_print_grid() {
        val grid = PuzzleGrid(6, 3)
        val string = grid.toString()
        assertEquals(
                ". . . . . . \n" +
                ". . . . . . \n" +
                ". . . . . . \n", string)
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
        val grid = PuzzleGrid(4, 4)
        grid.addWord(Position(0,0, Direction.SOUTH_EAST), "test")
        grid.addWord(Position(2,0, Direction.SOUTH_WEST), "yes")
        grid.addWord(Position(1,3, Direction.NORTH_EAST), "ask")
        grid.addWord(Position(2,2, Direction.NORTH_WEST), "set")
        grid.addWord(Position(3,2, Direction.SOUTH_WEST), "ok")
        grid.addWord(Position(0,0, Direction.EAST), "toy")
        grid.addWord(Position(0,0, Direction.SOUTH), "task")
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
        val grid = PuzzleGrid(4, 4)
        grid.addWord(Position(1,0, Direction.EAST), "test")
        grid.addWord(Position(0,1, Direction.SOUTH), "test")
        grid.addWord(Position(2,0, Direction.WEST), "test")
        grid.addWord(Position(0,2, Direction.NORTH), "test")
        grid.addWord(Position(1,0, Direction.WEST), "bam")
        grid.addWord(Position(0,1, Direction.NORTH), "bam")
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
        val seq = Sequencer(1, 4)

        // No vacant cells
        var pos = grid.findEmptyCell(seq)
        assertEquals(null, pos)

        // Remove one word that does not clear any cells
        grid.removeWord("tart")
        println(grid.toString())
        pos = grid.findEmptyCell(seq)
        assertEquals(null, pos)

        // Remove one word that does not clear any cells
        grid.removeWord("afar")
        println(grid.toString())

        pos = grid.findEmptyCell(seq)
        assertEquals(3, pos.x)
        assertEquals(2, pos.y)
    }

}
