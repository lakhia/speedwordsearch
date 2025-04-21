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
        "B C C C \n" +
                ". B A A \n" +
                ". A A A \n" +
                ". B B C \n", game.toString())
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
                "C . B . . . . . \n" +
                        ". A A . . . . C \n" +
                        ". . A . . . B C \n" +
                        ". . C C . A C C \n" +
                        ". . B . B A A C \n" +
                        "C C B A B . A A \n" +
                        ". . A A . . A . \n" +
                        ". . C . . . . . \n", game.toString())

        assertEquals("CAAC ABAC BABC ABCC BBAC BAAC AAAC ACCC ",
                displayBuffer.toString())

        // Correct direction guessing
        var guess = game.guess(1, 2, 4, 2)
        assertNull(guess.answer)
        assertFalse(guess.last)
        guess = game.guess(7, 2, 4, 2)
        assertNull(guess.answer)
        assertFalse(guess.last)
        guess = game.guess(2, 0, 2,3)
        assertNotNull(guess.answer)
        assertFalse(guess.last)
        guess = game.guess(7, 5, 7, 2)
        assertNotNull(guess.answer)
        assertFalse(guess.last)
        assertEquals(18, totalScore)

        // Opposite direction guessing
        guess = game.guess(3, 5, 0, 5)
        assertNotNull(guess.answer)
        assertFalse(guess.last)
        assertEquals(27, totalScore)

        // Palindrome
        guess = game.guess(0, 0, 3, 3)
        assertNotNull(guess.answer)
        assertFalse(guess.last)
        assertEquals(36, totalScore)

        // Get answers
        var ans = game.answers
        assertEquals("[" +
                "pos: (6, 6), dir: NORTH, len: 4, word: AAAC, " +
                "pos: (5, 4), dir: SOUTH_WEST, len: 4, word: ABAC, " +
                "pos: (4, 4), dir: NORTH_EAST, len: 4, word: BABC, " +
                "pos: (2, 4), dir: SOUTH, len: 4, word: BBAC]",
                ans.toString())

        config.isWordListSorted = false
        ans = game.answers
        assertEquals("[" +
                "pos: (6, 6), dir: NORTH, len: 4, word: AAAC, " +
                "pos: (5, 4), dir: SOUTH_WEST, len: 4, word: ABAC, " +
                "pos: (2, 4), dir: SOUTH, len: 4, word: BBAC, " +
                "pos: (4, 4), dir: NORTH_EAST, len: 4, word: BABC]",
                ans.toString())

        // Clear placeholders
        game.clearPlaceholders(30)
        assertEquals(
                ". . . . . . . . \n" +
                        ". . . . . . . C \n" +
                        ". . . . . . B . \n" +
                        ". . . . . A C . \n" +
                        ". . B . B A A . \n" +
                        ". . B . B . A . \n" +
                        ". . A A . . A . \n" +
                        ". . C . . . . . \n", game.toString())
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
                "B A A A C C \n" +
                        "B C B C D A \n" +
                        "A A C F D A \n" +
                        "C B C F A C \n" +
                        "G A C B A B \n" +
                        "G P A D D A \n", game.toString())
        // TODO: bug because selection is bigger than len of word
        assertEquals(
                "pos: (2, 5), dir: NORTH, len: 5, word: ACCC\n" +
                        "pos: (2, 0), dir: SOUTH, len: 5, word: ABCC\n" +
                        "pos: (1, 4), dir: NORTH, len: 5, word: ABAC\n" +
                        "pos: (4, 0), dir: SOUTH, len: 5, word: CDDA\n" +
                        "pos: (5, 0), dir: SOUTH, len: 5, word: CAAC\n" +
                        "pos: (0, 0), dir: SOUTH, len: 5, word: BBAC\n" +
                        "pos: (1, 0), dir: EAST, len: 5, word: AAAC\n" +
                        "pos: (5, 4), dir: WEST, len: 5, word: BABC\n" +
                        "pos: (5, 5), dir: WEST, len: 5, word: ADDA\n" +
                        "pos: (3, 2), dir: SOUTH, len: 3, word: FF\n" +
                        "pos: (0, 5), dir: NORTH, len: 3, word: GG\n",
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

    @Test
    fun test_07_get_selection() {
        val config = Config(6, 6, 0)
        val game = Game(config, dictionary, RandomSequencer(config, 1))
        var selection = game.getSelection(-1, 0, -1, 1)
        assertNull(selection)
        selection = game.getSelection(0, 20, 0, 20)
        assertNull(selection)
        selection = game.getSelection(1, 1, 1, 1)
        assertNull(selection)
    }
}