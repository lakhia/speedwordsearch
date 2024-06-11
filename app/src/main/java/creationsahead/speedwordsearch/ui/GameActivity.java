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
import creationsahead.speedwordsearch.utils.SoundManager;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Primary activity for game play
 */
public class GameActivity extends Activity {

    private SoundManager sound_manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sound_manager = new SoundManager(this);
        View view = new GameView(this, null);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addContentView(view, params);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sound_manager.resume();
        EventBus.getDefault().register(this);
        EventBus.getDefault().post(Event.UN_PAUSE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sound_manager.pause();
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().post(Event.PAUSE);
    }

    @Override
    public void onBackPressed() {
        EventBus.getDefault().post(Event.LEVEL_LOST);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWinOrLose(@NonNull Event event) {
        if (event == Event.LEVEL_WON || event == Event.LEVEL_LOST) {
            ProgressTracker.getInstance().incrementLevel(event);
            finish();
            Intent intent = new Intent(this, LevelActivity.class);
            startActivity(intent);
        }
    }
}
