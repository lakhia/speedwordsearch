package creationsahead.speedwordsearch

import java.util.Random

/**
 * A class that allows permutations of letters, in a deterministic manner
 */
class DeterministicSequencer(seed: Int) : Sequencer {

    private val letterRandomGen: Random = Random(seed.toLong())
    private val directionRandomGen: Random = Random((2 + seed).toLong())
    private lateinit var coordinateCombinations: Array<IntArray>
    private lateinit var coordinateRandomGen: Random
    private val miscRandomGen: Random = Random(seed.toLong())
    private val bonusRandomGen: Random = Random(seed.toLong())
    private var difficulty = 1

    constructor(seed: Int, maxCoordinate: Int) : this(seed) {

        coordinateRandomGen = Random((seed * maxCoordinate).toLong())
        coordinateCombinations = Array(maxCoordinate) { IntArray(maxCoordinate) }
        for (i in 0..maxCoordinate - 1) {
            for (j in 0..maxCoordinate - 1) {
                coordinateCombinations[i][j] = j
            }
            shuffle(coordinateCombinations[i])
        }
    }

    constructor(config: Config) : this(1, config.sizeX) {
        difficulty = config.difficulty
    }

    private fun shuffle(array: IntArray) {
        for (i in array.size - 1 downTo 1) {
            val index = coordinateRandomGen.nextInt(i + 1)
            val temp = array[index]
            array[index] = array[i]
            array[i] = temp
        }
    }

    override fun getLetterSequence(): IntArray {
        return letterCombinations[letterRandomGen.nextInt(letterCombinations.size)]
    }

    override fun getXCoordinateSequence(): IntArray {
        return coordinateCombinations[coordinateRandomGen.nextInt(coordinateCombinations.size)]
    }

    override fun getYCoordinateSequence(): IntArray {
        return coordinateCombinations[coordinateRandomGen.nextInt(coordinateCombinations.size)]
    }

    override fun getDirectionSequence(): IntArray {
        return directionCombinations[directionRandomGen.nextInt(directionCombinations.size)]
    }

    override fun getMisc(max: Int): Int {
        return miscRandomGen.nextInt(max)
    }

    override fun getBonus(index: Int): Int {
        return bonusRandomGen.nextInt(20)
    }

    companion object {
        private val letterCombinations = arrayOf(
            intArrayOf(7, 5, 6, 1, 0, 18, 20, 14, 19, 17, 11, 12, 4, 10, 13, 8, 15, 3, 2),
            intArrayOf(20, 12, 4, 14, 7, 18, 5, 1, 8, 19, 13, 2, 0, 6, 11, 15, 17, 10, 3),
            intArrayOf(14, 8, 2, 4, 11, 0, 17, 6, 10, 20, 18, 5, 15, 3, 12, 13, 7, 19, 1),
            intArrayOf(12, 1, 11, 2, 3, 0, 15, 7, 17, 8, 6, 13, 4, 19, 18, 5, 20, 10, 14),
            intArrayOf(5, 7, 12, 3, 11, 6, 15, 0, 2, 18, 4, 1, 20, 19, 10, 8, 17, 14, 13),
            intArrayOf(12, 0, 19, 4, 8, 5, 1, 18, 17, 2, 13, 15, 10, 11, 20, 7, 14, 3, 6),
            intArrayOf(7, 14, 10, 11, 0, 4, 1, 18, 20, 5, 19, 8, 6, 15, 13, 17, 3, 12, 2),
            intArrayOf(5, 2, 1, 6, 3, 12, 8, 7, 20, 17, 11, 13, 4, 0, 19, 15, 18, 10, 14),
            intArrayOf(4, 3, 2, 14, 0, 12, 10, 11, 20, 5, 13, 1, 19, 15, 17, 6, 7, 8, 18),
            intArrayOf(15, 14, 6, 7, 13, 11, 2, 20, 4, 1, 8, 10, 5, 12, 17, 0, 18, 3, 19),
            intArrayOf(15, 12, 11, 0, 6, 20, 2, 13, 3, 5, 8, 14, 18, 17, 10, 4, 7, 1, 19),
            intArrayOf(10, 0, 12, 2, 17, 5, 6, 3, 8, 14, 4, 15, 20, 11, 1, 13, 7, 18, 19),
            intArrayOf(15, 2, 12, 8, 10, 13, 4, 6, 11, 18, 14, 3, 17, 1, 5, 19, 20, 0, 7),
            intArrayOf(0, 6, 13, 8, 19, 1, 20, 4, 14, 10, 17, 18, 3, 5, 11, 7, 2, 12, 15),
            intArrayOf(4, 6, 5, 20, 3, 15, 14, 7, 12, 19, 1, 13, 11, 17, 2, 10, 0, 8, 18),
            intArrayOf(0, 1, 13, 14, 8, 2, 3, 6, 10, 7, 18, 17, 11, 20, 19, 15, 12, 5, 4),
            intArrayOf(11, 18, 5, 7, 8, 2, 6, 10, 12, 19, 15, 14, 13, 3, 0, 1, 4, 17, 20),
            intArrayOf(10, 11, 8, 7, 13, 3, 14, 18, 17, 1, 12, 15, 19, 20, 6, 0, 4, 5, 2))
        private val directionCombinations = arrayOf(
            intArrayOf(7, 2, 6, 1, 5, 3, 0, 4),
            intArrayOf(5, 2, 7, 1, 6, 0, 4, 3),
            intArrayOf(1, 5, 2, 3, 6, 7, 0, 4),
            intArrayOf(7, 2, 5, 6, 4, 0, 3, 1),
            intArrayOf(2, 1, 4, 3, 7, 5, 0, 6),
            intArrayOf(3, 7, 0, 6, 1, 2, 5, 4),
            intArrayOf(3, 4, 6, 2, 5, 1, 7, 0),
            intArrayOf(6, 5, 2, 7, 0, 4, 3, 1))
    }
}
