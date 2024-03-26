package creationsahead.speedwordsearch.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import creationsahead.speedwordsearch.Event;
import creationsahead.speedwordsearch.Game;
import creationsahead.speedwordsearch.ProgressTracker;
import creationsahead.speedwordsearch.R;
import creationsahead.speedwordsearch.TickerCallback;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Primary activity for game play
 */
public class GameActivity extends Activity implements TickerCallback {
    @NonNull public static final String LOSE = "lose";
    @NonNull public static final String WIN = "win";
    private ScoreBar scoreBar;
    private Ticker timer;
    private Game game;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        game = ProgressTracker.getInstance().game;
        game.populatePuzzle();

        setContentView(R.layout.game);

        scoreBar = findViewById(R.id.scoreBar);
        if (timer != null) {
            timer.pause();
        }
        timer = new Ticker(this, this, ProgressTracker.getInstance().config.timeLimit);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        timer.startOrResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        timer.pause();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWin(@NonNull Event event) {
        if (event == Event.LEVEL_WON) {
            ProgressTracker.getInstance().incrementLevel(timer.timeLeft);
            finishActivity(WIN);
        }
    }

    @Override
    public void onTick(int tickCount) {
        if (tickCount <= 0) {
            finishActivity(LOSE);
        } else {
            game.onTick(tickCount);
            scoreBar.onTick(tickCount);
        }
    }

    @Override
    public void onBackPressed() {
        finishActivity(LOSE);
    }

    private void finishActivity(String outcome) {
        Intent intent = new Intent(this, LevelActivity.class);
        intent.setAction(outcome);
        startActivity(intent);
        finish();
    }
}
