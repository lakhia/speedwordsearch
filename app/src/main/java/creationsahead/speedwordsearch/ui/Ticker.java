package creationsahead.speedwordsearch.ui;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import creationsahead.speedwordsearch.Event;
import creationsahead.speedwordsearch.TickerCallback;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Manages ticker for scoring
 */
public class Ticker implements Runnable {

    @NonNull private final Handler handler;
    @NonNull private final TickerCallback callback;
    private boolean stop;
    private int timeLeft = 0;

    /**
     * Create a ticker
     */
    public Ticker(@NonNull Context context, @NonNull TickerCallback cb, int timeLimit) {
        handler = new Handler(context.getMainLooper());
        timeLeft = timeLimit;
        callback = cb;
        stop = true;
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void startLevel(@NonNull Event event) {
        if (event == Event.PAUSE) {
            pause();
        } else {
            startOrResume();
        }
    }

    /**
     * Start or resume a timer
     */
    private void startOrResume() {
        if (stop) {
            stop = false;
            handler.post(this);
        }
    }

    /**
     * Pause timer
     */
    private void pause() {
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

        callback.onTick(timeLeft);
        timeLeft--;
    }

    /**
     * Stop listening for events
     */
    public void destroy() {
        pause();
        EventBus.getDefault().unregister(this);
    }
}
