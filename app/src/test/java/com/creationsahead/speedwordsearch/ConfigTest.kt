package com.creationsahead.speedwordsearch

import com.creationsahead.speedwordsearch.LevelTracker.MAX_LEVEL
import com.creationsahead.speedwordsearch.LevelTracker.MAX_SUB_LEVEL
import org.junit.Test

class ConfigTest {

    private fun runTicker(difficulty: Int) {
        var addWord = 0
        var placeholder = 0
        val lettersAtATime = difficulty / 33
        for (i in 0..120) {
            if (i % (difficulty / 10 + 3) == 2) {
                placeholder += 4 - lettersAtATime
            }
            if (i % (11 - difficulty / 10) == 0) {
                addWord += lettersAtATime
            }
        }
        println("Result for $difficulty clearing placeholder: $placeholder,"
                + " adding word: $addWord")
    }

    @Test
    fun test_ticker() {
        for (i in 0..100) {
            runTicker(i)
        }
    }

    @Test
    fun test_config_getFreqBasedOnSizeDifficulty_size() {
        for (i in 4..15) {
            val config = Config(i, i, 0)
            config.difficulty = 30f
            val freq = config.getFreqBasedOnSizeDifficulty(false)
            val freqFlipped = config.getFreqBasedOnSizeDifficulty(true)
            println("For size $i, config freq is $freq, flipped is $freqFlipped")
        }
    }

    @Test
    fun test_config_difficulty() {
        for (i in 0..MAX_SUB_LEVEL) {
            for (j in 0..MAX_LEVEL) {
                val config = Config(i, j)
                val difficulty = config.difficulty
                val letterLimit = config.letterLimit
                val smallerWords = config.smallerWords
                println("For $i $j, config difficulty is $difficulty, limit is $letterLimit, smaller word is $smallerWords")
                println("  Size: ${config.sizeX} and ${config.sizeY}")
            }
        }
    }

    @Test
    fun test_config_getFreqBasedOnSizeDifficulty_level() {
        for (i in 0..MAX_SUB_LEVEL) {
            for (j in 0..MAX_LEVEL) {
                val config = Config(i, j)
                val freq = config.getFreqBasedOnSizeDifficulty(false)
                val freqFlipped = config.getFreqBasedOnSizeDifficulty(true)
                println("For size $i $j, config freq is $freq, flipped is $freqFlipped")
            }
        }
    }

    @Test
    fun test_config_smallerWords() {
        for (i in 0..MAX_SUB_LEVEL) {
            for (j in 0..MAX_LEVEL) {
                val config = Config(i, j)
                val small = config.smallerWords
                println("For size $i $j, config words $small")
            }
        }
    }
}
