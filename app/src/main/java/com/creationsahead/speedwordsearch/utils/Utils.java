package com.creationsahead.speedwordsearch.utils;

import androidx.annotation.NonNull;
import java.util.Locale;

public class Utils {

    @NonNull
    public static String formatTime(int timeSeconds) {
        int minutes = timeSeconds / 60;
        int seconds = timeSeconds % 60;
        return String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds);
    }
}
