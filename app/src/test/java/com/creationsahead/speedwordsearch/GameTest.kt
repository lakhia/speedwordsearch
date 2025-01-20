package com.creationsahead.speedwordsearch

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class GameTest {
    private val dictionary: Trie = Trie()
    private lateinit var displayBuffer: StringBuffer
    private lateinit var answerBuffer: StringBuffer
    private var totalScore: Int = 0

    @Before
    fun init() {
        EventBus.getDefault().register(this)
        displayBuffer = StringBuffer()
        answerBuffer = StringBuffer()
        totalScore = 0

        dictionary.insert("ABAC")
        dictionary.insert("BBAC")
        dictionary.insert("BAAC")
        dictionary.insert("AAAC")
        dictionary.insert("CAAC")
        dictionary.insert("BABC")
        dictionary.insert("ACCC")
        dictionary.insert("ABCC")
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUpdate(answer: Answer) {
        displayBuffer.append(answer.display + " ")
        answerBuffer.append(answer.toString() + "\n")
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUpdate(guess: Guess) {
        totalScore += guess.answer?.score ?: 0
    }

    @After
    fun cleanup() {
        EventBus.getDefault().unregister(this)
    }

    @Test
    fun test_01_game() {
        val config = Config(4, 4, 23)
        val game = Game(config, dictionary, RandomSequencer(config, 1))
        assertEquals(true, game.addOneWord(4, 4))
        assertEquals(
        ". . . C \n" +
                ". . . A \n" +
                ". . . A \n" +
                ". . . C \n", game.toString())
        assertEquals(true, game.addOneWord(4, 4))
        assertEquals(true, game.addOneWord(4, 4))
        assertEquals(true, game.addOneWord(4, 4))
        assertEquals(
        "B C . C \n" +
                ". B A A \n" +
                ". A A A \n" +
                "B B . C \n", game.toString())
        assertEquals(true, game.addOneWord(4, 4))
        assertEquals(false, game.addOneWord(3, 3))
        assertEquals(false, game.addOneWord(1, 1))
        assertEquals(false, game.addOneWord(0, 3))
    }

    @Test
    fun test_02_big_board() {
        val config = Config(8, 8, 23)
        val game = Game(config, dictionary, RandomSequencer(config, 1))

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
                "C . B B . . . . \n" +
                        ". A A B . . . . \n" +
                        ". B A . A . . A \n" +
                        "C C C C A C . B \n" +
                        ". . C A B A . C \n" +
                        ". . A A A C . C \n" +
                        ". . . C A A B . \n" +
                        ". . . . . . . . \n", game.toString())

        assertEquals("CAAC ABAC ACCC BABC AAAC BBAC BAAC ABCC ",
                displayBuffer.toString())

        // Correct direction guessing
        var guess = game.guess(Selection(1, 2, Direction.EAST, 4))
        assertNull(guess.answer)
        assertFalse(guess.last)
        guess = game.guess(Selection(7, 2, Direction.WEST, 4))
        assertNull(guess.answer)
        assertFalse(guess.last)
        guess = game.guess(Selection(7, 2, Direction.SOUTH, 4))
        assertNotNull(guess.answer)
        assertFalse(guess.last)
        guess = game.guess(Selection(2, 5, Direction.EAST, 4))
        assertNotNull(guess.answer)
        assertFalse(guess.last)
        assertEquals(18, totalScore)

        // Opposite direction guessing
        guess = game.guess(Selection(4, 3, Direction.WEST, 4))
        assertNotNull(guess.answer)
        assertFalse(guess.last)
        assertEquals(27, totalScore)

        // Palindrome
        guess = game.guess(Selection(0, 0, Direction.SOUTH_EAST, 4))
        assertNotNull(guess.answer)
        assertFalse(guess.last)
        assertEquals(36, totalScore)

        // Get answers
        var ans = game.answers
        assertEquals("[" +
                "pos: (5, 4), dir: WEST, len: 4, word: ABAC, " +
                "pos: (6, 6), dir: WEST, len: 4, word: BAAC, " +
                "pos: (3, 0), dir: SOUTH_WEST, len: 4, word: BABC, " +
                "pos: (2, 0), dir: SOUTH_EAST, len: 4, word: BBAC]",
                ans.toString())

        config.isWordListSorted = false
        ans = game.answers
        assertEquals("[" +
                "pos: (5, 4), dir: WEST, len: 4, word: ABAC, " +
                "pos: (2, 0), dir: SOUTH_EAST, len: 4, word: BBAC, " +
                "pos: (3, 0), dir: SOUTH_WEST, len: 4, word: BABC, " +
                "pos: (6, 6), dir: WEST, len: 4, word: BAAC]",
                ans.toString())

        // Clear placeholders
        game.clearPlaceholders(30)
        assertEquals(
                ". . B B . . . . \n" +
                        ". . A B . . . . \n" +
                        ". B . . A . . . \n" +
                        "C . . . . C . . \n" +
                        ". . C A B A . . \n" +
                        ". . . . . . . . \n" +
                        ". . . C A A B . \n" +
                        ". . . . . . . . \n", game.toString())
    }

    @Test
    fun test_03_min_max() {
        dictionary.insert("DDD")

        val config = Config(5, 5, 23)
        val game = Game(config, dictionary, RandomSequencer(config, 5))
        assertEquals(true, game.addOneWord(3, 4))

        assertEquals('C', game.getCell(4, 1).letter)
        assertEquals('C', game.getCell(4, 2).letter)
        assertEquals('C', game.getCell(4, 3).letter)
        assertEquals('A', game.getCell(4, 4).letter)

        assertEquals(true, game.addOneWord(3, 3))
        assertEquals(game.getCell(1, 1).letter, 'D')
        assertEquals(game.getCell(2, 2).letter, 'D')
        assertEquals(game.getCell(3, 3).letter, 'D')

        assertEquals(false, game.addOneWord(3, 3))
    }

    @Test
    fun test_04_populate() {
        dictionary.insert("ADDA")
        dictionary.insert("CDDA")
        dictionary.insert("GG")
        dictionary.insert("FF")

        val config = Config(6, 6, 53)
        config.smallerWords = 1
        val game = Game(config, dictionary, RandomSequencer(config, 1))
        game.populatePuzzle()
        assertEquals(
                "H A D D C M \n" +
                        "C A A B A L \n" +
                        "A C D A C C \n" +
                        "B A D U C A \n" +
                        "A B A B C A \n" +
                        "A B C C I C \n", game.toString())
        // TODO: bug because selection is bigger than len of word
        assertEquals(
                "pos: (2, 4), dir: NORTH, len: 5, word: ADDA\n" +
                        "pos: (1, 4), dir: EAST, len: 5, word: BABC\n" +
                        "pos: (1, 0), dir: SOUTH_EAST, len: 5, word: AAAC\n" +
                        "pos: (4, 0), dir: WEST, len: 5, word: CDDA\n" +
                        "pos: (5, 5), dir: NORTH, len: 5, word: CAAC\n" +
                        "pos: (4, 1), dir: SOUTH, len: 5, word: ACCC\n" +
                        "pos: (1, 5), dir: NORTH, len: 5, word: BBAC\n" +
                        "pos: (0, 4), dir: NORTH, len: 5, word: ABAC\n" +
                        "pos: (0, 5), dir: EAST, len: 5, word: ABCC\n" +
                        "pos: (3, 1), dir: WEST, len: 4, word: BAAC\n",
                answerBuffer.toString())
    }

    @Test
    fun test_06_fill() {
        val config = Config(6, 6, 0)
        val game = Game(config, dictionary, RandomSequencer(config, 1))
        game.fillEmptyCells()
        assertEquals(
                "K I P N M H \n" +
                        "M G O E T U \n" +
                        "O H K P F B \n" +
                        "I L R T C D \n" +
                        "S A S R E A \n" +
                        "C N B L D G \n", game.toString())
    }
}