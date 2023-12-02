package creationsahead.speedwordsearch.ui;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import creationsahead.speedwordsearch.TickerCallback;

/**
 * Manages ticker for scoring
 */
public class Ticker implements Runnable {

    @NonNull private final Handler handler;
    @NonNull public final TickerCallback callback;
    private boolean stop;
    private int timeLimit = 0;

    /**
     * Create a ticker
     */
    public Ticker(@NonNull Context context, @NonNull TickerCallback callback, int timeLimit) {
        handler = new Handler(context.getMainLooper());
        this.timeLimit = timeLimit;
        this.callback = callback;
        this.stop = true;
    }

    /**
     * Start or resume a timer
     */
    public void startOrResume() {
        if (stop) {
            stop = false;
            handler.post(this);
        }
    }

    /**
     * Pause timer
     */
    public void pause() {
        if (!stop) {
            stop = true;
            handler.removeCallbacks(this);
        }
    }

    @Override
    public void run() {
        if (!stop) {
            handler.postDelayed(this, 1000);
        }

        callback.onTick(timeLimit);
        timeLimit--;
    }
}
