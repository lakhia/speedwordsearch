package creationsahead.speedwordsearch.utils;

import java.util.Locale;

public class Utils {

    public static String formatTime(int timeSeconds) {
        if (timeSeconds < 0) {
            return "";
        }
        int minutes = timeSeconds / 60;
        int seconds = timeSeconds % 60;
        return String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds);
    }
}
