package creationsahead.speedwordsearch.ui;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import creationsahead.speedwordsearch.BuildConfig;
import creationsahead.speedwordsearch.ProgressTracker;
import creationsahead.speedwordsearch.StorageInterface;
import creationsahead.speedwordsearch.mod.Level;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
    public static int ANIMATION_DURATION = 1000;
    @NonNull private Kryo serializer = new Kryo();

    @Override
    public void onCreate() {
        super.onCreate();
        serializer.register(Level.class, 15);
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

    @Nullable
    @Override
    public Level getLevel(int index) {
        try {
            FileInputStream fileIn = getApplicationContext().openFileInput("level_" + index);
            Input input = new Input(fileIn);
            Level level = serializer.readObject(input, Level.class);
            input.close();
            fileIn.close();
            return level;
        } catch (FileNotFoundException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void storeLevel(@NonNull Level level) {
        try {
            FileOutputStream fileOut = openFileOutput("level_" + level.number, Activity.MODE_PRIVATE);
            Output output = new Output(fileOut);
            serializer.writeObject(output, level);
            output.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
