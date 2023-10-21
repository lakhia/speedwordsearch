package creationsahead.speedwordsearch.ui;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import creationsahead.speedwordsearch.ProgressTracker;
import creationsahead.speedwordsearch.StorageInterface;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.zip.GZIPInputStream;

/**
 * The application instance is started on app launch and initializes other
 * singletons and classes
 */
public class GameApplication extends Application implements StorageInterface {

    @Override
    public void onCreate() {
        super.onCreate();
        ProgressTracker.init(this, 5);
    }

    @NonNull
    @Override
    public Reader getAssetInputStream(@NonNull String fileName) throws IOException {
        InputStream inputStream = getAssets().open(fileName);
        return new InputStreamReader(new GZIPInputStream(inputStream));
    }

    @Override
    public int getPreference(@NonNull String key) {
        SharedPreferences prefs = getSharedPreferences(getClass().getName(), Context.MODE_PRIVATE);
        return prefs.getInt(key, 0);
    }

    @Override
    public void storePreference(@NonNull String key, int val) {
        SharedPreferences prefs = getSharedPreferences(getClass().getName(), Context.MODE_PRIVATE);
        prefs.edit().putInt(key, val).apply();
    }
}
