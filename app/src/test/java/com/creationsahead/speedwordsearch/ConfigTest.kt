package com.creationsahead.speedwordsearch

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
            config.difficulty = 30
            val freq = config.getFreqBasedOnSizeDifficulty(false)
            val freqFlipped = config.getFreqBasedOnSizeDifficulty(true)
            println("For size $i, config freq is $freq, flipped is $freqFlipped")
        }
    }

    @Test
    fun test_config_getFreqBasedOnSizeDifficulty_level() {
        for (i in 0..15) {
            val config = Config(i)
            val freq = config.getFreqBasedOnSizeDifficulty(false)
            val freqFlipped = config.getFreqBasedOnSizeDifficulty(true)
            println("For size $i, config freq is $freq, flipped is $freqFlipped")
        }
    }
}
