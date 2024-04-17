package creationsahead.speedwordsearch.ui;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.transition.Fade;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import creationsahead.speedwordsearch.Event;
import creationsahead.speedwordsearch.ProgressTracker;
import creationsahead.speedwordsearch.R;
import creationsahead.speedwordsearch.mod.Level;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static creationsahead.speedwordsearch.ui.GameApplication.ANIMATION_DURATION;

/**
 * Primary activity for game play
 */
public class GameActivity extends Activity {
    @NonNull public static final String LOSE = "lose";
    @NonNull public static final String WIN = "win";
    private boolean levelMode = false;
    private View view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addContentView(view, params);
    }

    private void init() {
        if (levelMode) {
            view = new LevelListView(this, null);
        } else {
            view = new GameView(this, null);
        }
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
            levelMode = true;
            finishActivity(WIN);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLevelStarted(@NonNull Level level) {
        levelMode = false;
        finishActivity("");
    }

    @Override
    public void onBackPressed() {
        if (levelMode) {
            finish();
        } else {
            levelMode = true;
            finishActivity(LOSE);
        }
    }

    private void finishActivity(String outcome) {
        // TODO: Use outcome
        init();
        ViewGroup group = findViewById(R.id.topLayout);
        Scene scene = new Scene(group, view);
        Fade fade = new Fade();
        fade.setDuration(ANIMATION_DURATION);
        TransitionManager.go(scene, fade);
    }
}
