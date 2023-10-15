package creationsahead.speedwordsearch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class WordList {
    public static Trie dictionary;

    public static void init(Reader inputStream) {
        dictionary = new Trie();
        BufferedReader bufferedReader = new BufferedReader(inputStream);

        try {
            String line;
            while((line = bufferedReader.readLine()) != null) {
                dictionary.insert(line);
            }
        } catch (IOException ignored) {
        }
    }
}
