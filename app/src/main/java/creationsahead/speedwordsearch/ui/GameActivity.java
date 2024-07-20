package creationsahead.speedwordsearch.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import creationsahead.speedwordsearch.Event;
import creationsahead.speedwordsearch.Game;
import creationsahead.speedwordsearch.ProgressTracker;
import creationsahead.speedwordsearch.R;
import creationsahead.speedwordsearch.TickerCallback;
import creationsahead.speedwordsearch.utils.SoundManager;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
        ticker = new Ticker(this, this,
                ProgressTracker.getInstance().config.timeLimit);
        game = ProgressTracker.getInstance().game;
        sound_manager = new SoundManager(this);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.game, null);
        scoreBar = view.findViewById(R.id.scoreBar);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addContentView(view, params);
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

    @Override
    public void onTick(int tickCount) {
        if (tickCount <= 0) {
            EventBus.getDefault().post(Event.LEVEL_LOST);
        } else {
            scoreBar.onTick(tickCount);
        }
    }
}
