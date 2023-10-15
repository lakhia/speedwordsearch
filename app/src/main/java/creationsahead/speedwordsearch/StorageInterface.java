package creationsahead.speedwordsearch;

import android.support.annotation.NonNull;
import java.io.Reader;

public interface StorageInterface {
    Reader getAssetInputStream(@NonNull String name);
    int getPreference(@NonNull String key);
    void storePreference(@NonNull String key, int score);
}
