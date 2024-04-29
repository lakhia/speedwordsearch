package creationsahead.speedwordsearch.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import creationsahead.speedwordsearch.Event;
import creationsahead.speedwordsearch.ProgressTracker;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Primary activity for game play
 */
public class GameActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = new GameView(this, null);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addContentView(view, params);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        EventBus.getDefault().post(Event.UN_PAUSE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().post(Event.PAUSE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWin(@NonNull Event event) {
        if (event == Event.LEVEL_WON) {
            ProgressTracker.getInstance().incrementLevel(event.timeLeft);
            finish();
            Intent intent = new Intent(this, LevelActivity.class);
            startActivity(intent);
        }
    }
}
