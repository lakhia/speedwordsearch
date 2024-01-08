package creationsahead.speedwordsearch;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.io.IOException;
import java.io.Reader;

public interface StorageInterface {
    @NonNull Reader getAssetInputStream(@NonNull String name) throws IOException;
    int getPreference(@NonNull String key);
    void storePreference(@NonNull String key, int score);
    @Nullable Level getLevel(int index);
    void storeLevel(@NonNull Level level);
}
