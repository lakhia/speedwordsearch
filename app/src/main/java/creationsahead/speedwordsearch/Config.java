package creationsahead.speedwordsearch;

/**
 * Configuration for a game
 */
public class Config {
    public int sizeX, sizeY;
    public int seed;
    public Trie dictionary;

    public Config(int sizeX, int sizeY, Trie dictionary, int seed) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.seed = seed;
        this.dictionary = dictionary;
    }
}
