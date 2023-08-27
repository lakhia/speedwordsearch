package creationsahead.speedwordsearch;

/**
 * Configuration for a game
 */
public class Config {
    public int sizeX, sizeY;
    public Sequencer sequencer;
    public Trie dictionary;

    public Config(int sizeX, int sizeY, Trie dictionary, int seed) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.dictionary = dictionary;
        // TODO: sequencer needs a single dimension
        sequencer = new Sequencer(seed, sizeX);
    }
}
