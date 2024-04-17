package creationsahead.speedwordsearch;

import androidx.annotation.NonNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class WordList {
    @NonNull public static final Trie dictionary = new Trie();

    public static void init(@NonNull Reader inputStream) {
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
