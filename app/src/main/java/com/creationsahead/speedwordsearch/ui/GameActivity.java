package com.creationsahead.speedwordsearch.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.creationsahead.speedwordsearch.Game;
import com.creationsahead.speedwordsearch.ProgressTracker;
import com.creationsahead.speedwordsearch.R;
import com.creationsahead.speedwordsearch.TickerCallback;
import com.creationsahead.speedwordsearch.mod.Level;
import com.creationsahead.speedwordsearch.utils.SoundManager;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import static com.creationsahead.speedwordsearch.mod.Level.TIME_LEFT;

/**
 * Primary activity for game play
 */
public class GameActivity extends Activity implements TickerCallback {

    private SoundManager sound_manager;
    private Ticker ticker;
    private ScoreBar scoreBar;
    protected Game game;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        game = ProgressTracker.getInstance().getGame();
        ticker = new Ticker(this, this, TIME_LEFT);
        sound_manager = SoundManager.getInstance();
        setContentView(R.layout.game);
        scoreBar = findViewById(R.id.scoreBar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sound_manager.resume();
        ticker.resume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sound_manager.pause();
        ticker.pause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        EventBus.getDefault().post(ProgressTracker.getInstance().currentLevel);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWinOrLose(@NonNull Level level) {
        finish();
        Intent intent = new Intent(this, LevelActivity.class);
        startActivity(intent);
    }

    @Override
    public void onTick(int tickCount) {
        scoreBar.onTick(tickCount);
        if (tickCount <= 0) {
            EventBus.getDefault().post(ProgressTracker.getInstance().currentLevel);
        } else {
            game.onTick(tickCount);
        }
    }
}
