package creationsahead.speedwordsearch

class Scoring : ScoreInterface {
    override fun computeScore(word: String): Int {
        return 5
    }
}
