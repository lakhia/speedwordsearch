package creationsahead.speedwordsearch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

public class WordList {
    public static Trie dictionary;

    public static void init(InputStream inputStream) {
        dictionary = new Trie();
        try {
            String line;
            BufferedReader input = new BufferedReader(
                new InputStreamReader(new GZIPInputStream(inputStream)));
            while((line = input.readLine()) != null) {
                dictionary.insert(line);
            }
        } catch (IOException ignored) {
        }
    }
}
