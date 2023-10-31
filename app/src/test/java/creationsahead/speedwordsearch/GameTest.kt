package creationsahead.speedwordsearch

import org.junit.Test
import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class GameTest {

    fun init(): Trie {
        val dictionary: Trie = Trie()
        dictionary.insert("ABAC")
        dictionary.insert("BBAC")
        dictionary.insert("BAAC")
        dictionary.insert("AAAC")
        dictionary.insert("CAAC")
        dictionary.insert("BABC")
        dictionary.insert("ACCC")
        dictionary.insert("ABCC")
        return dictionary
    }

    @Test
    fun test_01_game() {
        val dictionary = init()

        val game = Game(Config(4, 4, dictionary, 1, 1), Scoring())
        assertEquals(true, game.addOneWord(4, 4))
        assertEquals(
                "C A A A \n" +
                ". . . . \n" +
                ". . . . \n" +
                ". . . . \n", game.toString())
        assertEquals(true, game.addOneWord(4, 4))
        assertEquals(true, game.addOneWord(4, 4))
        assertEquals(true, game.addOneWord(4, 4))
        assertEquals(
                "C A A A \n" +
                "C A A C \n" +
                "B A A C \n" +
                "A C C C \n", game.toString())
        assertEquals(false, game.addOneWord(4, 4))
        assertEquals(false, game.addOneWord(3, 3))
        assertEquals(false, game.addOneWord(1, 1))
        assertEquals(false, game.addOneWord(0, 3))
    }

    @Test
    fun test_02_big_board() {
        val dictionary = init()
        val scoring = Scoring()
        val game = Game(Config(8, 8, dictionary, 1, 1), scoring)

        assertEquals(true, game.addOneWord(4, 4))
        assertEquals(true, game.addOneWord(4, 4))
        assertEquals(true, game.addOneWord(4, 4))
        assertEquals(true, game.addOneWord(4, 4))
        assertEquals(true, game.addOneWord(4, 4))
        assertEquals(true, game.addOneWord(4, 4))
        assertEquals(true, game.addOneWord(4, 4))
        assertEquals(true, game.addOneWord(4, 4))
        assertEquals(false, game.addOneWord(4, 4))
        assertEquals(
                ". . . . . . . . \n" +
                ". . . C C C A . \n" +
                "C A A C . . . . \n" +
                ". . C A B B A . \n" +
                ". . . B . . B . \n" +
                ". . . C A A A . \n" +
                ". . . C . B C . \n" +
                ". . . B A A C . \n", game.toString())

        // Correct direction guessing
        assertFalse(game.guess(Selection(1, 2, Direction.EAST, 4)))
        assertFalse(game.guess(Selection(0, 2, Direction.SOUTH, 4)))
        assertTrue(game.guess(Selection(6, 3, Direction.SOUTH, 4)))
        assertTrue(game.guess(Selection(6, 1, Direction.WEST, 4)))
        assertEquals(10, scoring.totalScore)

        // Opposite direction guessing
        assertTrue(game.guess(Selection(3, 7, Direction.EAST, 4)))

        // Palindrome
        assertTrue(game.guess(Selection(0, 2, Direction.EAST, 4)))
        assertEquals(
                ". . . . . . . . \n" +
                ". . . . . . . . \n" +
                ". . . . . . . . \n" +
                ". . C A B B . . \n" +
                ". . . B . . . . \n" +
                ". . . C A A A . \n" +
                ". . . C . B . . \n" +
                ". . . . . . . . \n", game.toString())
        assertEquals(20, scoring.totalScore)
    }

    @Test
    fun test_03_min_max() {
        val dictionary = init()
        dictionary.insert("DDD")

        val game = Game(Config(5, 5, dictionary, 1, 1), Scoring())
        assertEquals(true, game.addOneWord(3, 4))

        assertEquals(game.getCell(0, 0).letter, 'C')
        assertEquals(game.getCell(0, 1).letter, 'A')
        assertEquals(game.getCell(0, 2).letter, 'A')
        assertEquals(game.getCell(0, 3).letter, 'A')

        assertEquals(true, game.addOneWord(3, 3))
        assertEquals(game.getCell(0, 4).letter, 'D')
        assertEquals(game.getCell(1, 4).letter, 'D')
        assertEquals(game.getCell(2, 4).letter, 'D')

        assertEquals(false, game.addOneWord(3, 3))
    }

    @Test
    fun test_04_populate() {
        val dictionary = init()
        val buffer = StringBuffer()
        dictionary.insert("ADDA")
        dictionary.insert("CDDA")
        dictionary.insert("GG")
        dictionary.insert("FF")

        Answer.callback = AnswerCallback { buffer.append(it.toString() + "\n") }

        val game = Game(Config(6, 6, dictionary, 1, 1), Scoring())
        game.populatePuzzle()
        assertEquals(
                "G A D D A B \n" +
                "G H C O A A \n" +
                "C C C A B A \n" +
                "F C A C A C \n" +
                "F B B A C A \n" +
                "C D D A C A \n", game.toString())
        assertEquals(
                "pos: (1, 4), dir: EAST, len: 4, word: BBAC\n" +
                "pos: (5, 4), dir: NORTH_WEST, len: 4, word: AAAC\n" +
                "pos: (4, 5), dir: NORTH_WEST, len: 4, word: CAAC\n" +
                "pos: (5, 5), dir: NORTH_WEST, len: 4, word: ACCC\n" +
                "pos: (3, 5), dir: NORTH_WEST, len: 4, word: ABCC\n" +
                "pos: (4, 1), dir: SOUTH, len: 4, word: ABAC\n" +
                "pos: (5, 0), dir: SOUTH, len: 4, word: BAAC\n" +
                "pos: (1, 0), dir: EAST, len: 4, word: ADDA\n" +
                "pos: (0, 5), dir: EAST, len: 4, word: CDDA\n" +
                "pos: (0, 3), dir: SOUTH, len: 2, word: FF\n" +
                "pos: (0, 0), dir: SOUTH, len: 2, word: GG\n",
                buffer.toString())
    }

    @Test
    fun test_05_populate_difficult() {
        val dictionary = init()
        dictionary.insert("ADDA")
        dictionary.insert("CDDA")
        dictionary.insert("GG")
        dictionary.insert("FF")

        val game = Game(Config(6, 6, dictionary, 1, 80), Scoring())
        game.populatePuzzle()
        assertEquals(
                "G S A N B N \n" +
                "P O C C T I \n" +
                "M C E A I L \n" +
                "U D A A A H \n" +
                "T B B A C A \n" +
                "B R F G C K \n", game.toString())
    }

    @Test
    fun test_06_fill() {
        val game = Game(Config(6, 6, null, 1, 80), Scoring())
        game.fillEmptyCells()
        assertEquals(
                "F K G D S B \n" +
                "L R E N R N \n" +
                "M H A F K O \n" +
                "U D P U H C \n" +
                "P G L C O A \n" +
                "T S M I E I \n", game.toString())
    }
}