package com.creationsahead.speedwordsearch.ui;

import static java.lang.Float.max;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.preference.PreferenceManager;
import com.creationsahead.speedwordsearch.ProgressTracker;
import com.creationsahead.speedwordsearch.R;
import com.creationsahead.speedwordsearch.StorageInterface;
import com.creationsahead.speedwordsearch.utils.SoundManager;

/**
 * Application loader
 */
public class Loader implements Runnable {
    @NonNull private final Context context;
    @NonNull private final StorageInterface storageInterface;
    public SharedPreferences prefs;
    public Typeface wordListTypeface;
    public Typeface letterTypeface;

    public Loader(@NonNull Context context, @NonNull StorageInterface storageInterface) {
        this.context = context;
        this.storageInterface = storageInterface;
    }

    @Override
    public void run() {
        letterTypeface = ResourcesCompat.getFont(context, R.font.archivo_black);
        wordListTypeface = ResourcesCompat.getFont(context, R.font.artifika);

        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (!prefs.contains("music")) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("music", 50);
            editor.putInt("special_effects", 70);
            editor.apply();
        }

        Rect display = new Rect();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getRectSize(display);

        Rect bounds = new Rect();
        TextView textView = new TextView(context);
        textView.getPaint().getTextBounds("W", 0, 1, bounds);
        int max = Math.max(bounds.width(), bounds.height());
        float fontSize = 0.3f * textView.getTextSize() * max(display.width(), display.height()) / max;
        ProgressTracker.getInstance().init(storageInterface, display, fontSize);
        SoundManager.getInstance().init(context);
        SoundManager.getInstance().syncSettingsFromStorage(storageInterface);
    }
}
