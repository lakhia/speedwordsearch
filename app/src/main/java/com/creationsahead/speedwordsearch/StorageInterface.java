package com.creationsahead.speedwordsearch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.creationsahead.speedwordsearch.mod.SubLevel;
import java.io.IOException;
import java.io.Reader;

public interface StorageInterface {
    @NonNull Reader getAssetInputStream(@NonNull String name) throws IOException;
    int getPreference(@NonNull String key);
    @Nullable SubLevel getSubLevel(@NonNull String name);
    void storeSubLevel(@NonNull SubLevel level);
}
