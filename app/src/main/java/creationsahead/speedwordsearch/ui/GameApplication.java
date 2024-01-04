package creationsahead.speedwordsearch.ui;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import creationsahead.speedwordsearch.BuildConfig;
import creationsahead.speedwordsearch.ProgressTracker;
import creationsahead.speedwordsearch.StorageInterface;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.zip.GZIPInputStream;
import org.greenrobot.eventbus.EventBus;

/**
 * The application instance is started on app launch and initializes other
 * singletons and classes
 */
public class GameApplication extends Application implements StorageInterface {

    @Override
    public void onCreate() {
        super.onCreate();
        Iconify.with(new FontAwesomeModule());
        EventBus.builder().throwSubscriberException(BuildConfig.DEBUG).
            sendNoSubscriberEvent(false).installDefaultEventBus();
        ProgressTracker.getInstance().init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ProgressTracker.getInstance().destroy();
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
