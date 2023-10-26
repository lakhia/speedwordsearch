package creationsahead.speedwordsearch

class Scoring : ScoreInterface {
    var totalScore = 0
    override fun computeScore(word: String): Int {
        return 5
    }
    override fun addScore(score: Int) {
        totalScore += score
    }
}
