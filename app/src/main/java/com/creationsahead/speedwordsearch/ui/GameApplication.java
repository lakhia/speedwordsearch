package com.creationsahead.speedwordsearch.ui;

import static java.lang.Float.max;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.creationsahead.speedwordsearch.BuildConfig;
import com.creationsahead.speedwordsearch.EventBusIndex;
import com.creationsahead.speedwordsearch.ProgressTracker;
import com.creationsahead.speedwordsearch.StorageInterface;
import com.creationsahead.speedwordsearch.mod.Level;
import com.creationsahead.speedwordsearch.utils.SoundManager;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
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
import org.greenrobot.eventbus.EventBus;

/**
 * The application instance is started on app launch and initializes other
 * singletons and classes
 */
public class GameApplication extends Application implements StorageInterface {
    public final static int ANIMATION_DURATION = 1000;
    @NonNull private final Kryo serializer = new Kryo();
    private SharedPreferences prefs;

    @Override
    public void onCreate() {
        if (BuildConfig.FLAVOR == "Paid") {
            Date todayDate = new Date();
            if (todayDate.after(new GregorianCalendar(2023, 12, 31).getTime())) {
                Toast.makeText(this,
                        "Trial period has expired", Toast.LENGTH_LONG).show();
                return;
            }
        }
        if (BuildConfig.BUILD_TYPE == "debug") {
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false);
        }
        super.onCreate();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        serializer.register(Level.class, 15);
        EventBus.builder().addIndex(new EventBusIndex())
                .throwSubscriberException(BuildConfig.DEBUG)
                .eventInheritance(false)
                .sendNoSubscriberEvent(false).installDefaultEventBus();

        Rect display = new Rect();
        WindowManager manager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getRectSize(display);

        Rect bounds = new Rect();
        TextView textView = new TextView(this);
        textView.getPaint().getTextBounds("W", 0, 1, bounds);
        int max = Math.max(bounds.width(), bounds.height());
        float fontSize = 0.3f * textView.getTextSize() * max(display.width(), display.height()) / max;
        ProgressTracker.getInstance().init(this, display, fontSize);
        SoundManager.getInstance().init(this);
        SoundManager.getInstance().syncSettingsFromStorage(this);
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
        return prefs.getInt(key, 0);
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
