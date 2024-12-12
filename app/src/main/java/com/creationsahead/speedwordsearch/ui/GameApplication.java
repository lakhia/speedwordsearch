package com.creationsahead.speedwordsearch.ui;

import android.app.Activity;
import android.app.Application;
import android.os.StrictMode;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.creationsahead.speedwordsearch.BuildConfig;
import com.creationsahead.speedwordsearch.EventBusIndex;
import com.creationsahead.speedwordsearch.ProgressTracker;
import com.creationsahead.speedwordsearch.StorageInterface;
import com.creationsahead.speedwordsearch.mod.Level;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import org.greenrobot.eventbus.EventBus;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.zip.GZIPInputStream;

/**
 * The application instance is started on app launch and initializes other
 * singletons and classes
 */
public class GameApplication extends Application implements StorageInterface {
    public final static int ANIMATION_DURATION = 1000;
    @NonNull private final Kryo serializer = new Kryo();
    public Loader loader;

    @Override
    public void onCreate() {
        if (BuildConfig.FLAVOR.equals("paid")) {
            Date todayDate = new Date();
            if (todayDate.after(new GregorianCalendar(2023, 12, 31).getTime())) {
                Toast.makeText(this,
                        "Trial period has expired", Toast.LENGTH_LONG).show();
                return;
            }
        }
        if (BuildConfig.BUILD_TYPE.equals("debug")) {
            StrictMode.enableDefaults();
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false);
        }
        super.onCreate();
        serializer.register(Level.class, 15);
        EventBus.builder().addIndex(new EventBusIndex())
                .throwSubscriberException(BuildConfig.DEBUG)
                .eventInheritance(false)
                .sendNoSubscriberEvent(false).installDefaultEventBus();
        loader = new Loader(this, this);
        new Thread(loader).start();
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
        return loader.prefs.getInt(key, 0);
    }

    @Nullable
    @Override
    public Level getLevel(int index, @NonNull String name) {
        try {
            FileInputStream fileIn = getApplicationContext().openFileInput("level_" + name + index);
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
    public void storeLevel(@NonNull Level level, @NonNull String name) {
        try {
            FileOutputStream fileOut = openFileOutput("level_" + name + level.number,
                    Activity.MODE_PRIVATE);
            Output output = new Output(fileOut);
            serializer.writeObject(output, level);
            output.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
