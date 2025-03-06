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
import com.creationsahead.speedwordsearch.mod.SubLevel;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import org.greenrobot.eventbus.EventBus;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
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
        loader = new Loader(this, this);
        Thread thread = new Thread(loader);
        thread.start();

        if (BuildConfig.FLAVOR.equals("paid")) {
            Date todayDate = new Date();
            if (todayDate.after(new GregorianCalendar(2025, 12, 31).getTime())) {
                Toast.makeText(this,
                        "Trial period has expired", Toast.LENGTH_LONG).show();
                return;
            }
        }
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        if (BuildConfig.BUILD_TYPE.equals("debug")) {
            StrictMode.enableDefaults();
        }
        super.onCreate();
        serializer.register(ArrayList.class, 16);
        serializer.register(SubLevel.class, 15);
        serializer.register(Level.class, 14);
        EventBus.builder().addIndex(new EventBusIndex())
                .throwSubscriberException(BuildConfig.DEBUG)
                .eventInheritance(false)
                .sendNoSubscriberEvent(false).installDefaultEventBus();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
    public SubLevel getSubLevel(@NonNull String name) {
        try {
            FileInputStream fileIn = getApplicationContext().openFileInput(name);
            Input input = new Input(fileIn);
            SubLevel level = serializer.readObject(input, SubLevel.class);
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
    public void storeSubLevel(@NonNull SubLevel subLevel) {
        try {
            FileOutputStream fileOut = openFileOutput(subLevel.name,
                    Activity.MODE_PRIVATE);
            Output output = new Output(fileOut);
            serializer.writeObject(output, subLevel);
            output.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
