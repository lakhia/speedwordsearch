package com.creationsahead.speedwordsearch.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.creationsahead.speedwordsearch.Game;
import com.creationsahead.speedwordsearch.GridCallback;
import com.creationsahead.speedwordsearch.Guess;
import com.creationsahead.speedwordsearch.ProgressTracker;
import com.creationsahead.speedwordsearch.R;
import com.creationsahead.speedwordsearch.TickerCallback;
import com.creationsahead.speedwordsearch.utils.SoundManager;
import org.greenrobot.eventbus.EventBus;
import static com.creationsahead.speedwordsearch.mod.Level.TIME_LEFT;

/**
 * Primary activity for game play
 */
public class GameActivity extends Activity implements TickerCallback, GridCallback {

    private SoundManager sound_manager;
    private Ticker ticker;
    private ScoreBar scoreBar;
    private GridWidget gridWidget;
    protected Game game;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        game = ProgressTracker.getInstance().getGame();
        sound_manager = SoundManager.getInstance();
        setContentView(R.layout.game);
        scoreBar = findViewById(R.id.scoreBar);
        gridWidget = findViewById(R.id.grid);

        int timeLeft = TIME_LEFT;
        if (savedInstanceState != null) {
            timeLeft = savedInstanceState.getInt("score", TIME_LEFT);
        }
        ticker = new Ticker(this, this, timeLeft);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("score", ticker.getTimeLeft());
    }

    @Override
    protected void onResume() {
        super.onResume();
        sound_manager.resume();
        ticker.resume();
        gridWidget.post(() -> gridWidget.setupTouchHandler(this));
    }

    @Override
    protected void onPause() {
        super.onPause();
        sound_manager.pause();
        ticker.pause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        EventBus.getDefault().post(ProgressTracker.getInstance().currentLevel);
        Intent intent = new Intent(this, LevelActivity.class);
        startActivity(intent);
    }

    @Override
    public void onTick(int tickCount) {
        scoreBar.onTick(tickCount);
        if (tickCount <= 0) {
            onBackPressed();
        } else {
            game.onTick(tickCount);
        }
    }

    @Override
    public void onCellSelected(int x, int y) {
        gridWidget.setCellResource(x, y, R.drawable.cell_selected);
    }

    @Override
    public void onCellDeselected(int x, int y) {
        gridWidget.setCellResource(x, y, R.drawable.cell);
    }

    @Override
    public void onGuess(int x1, int y1, int x2, int y2) {
        Guess guess = game.guess(x1, y1, x2, y2);
        if (guess != null) {
            if (guess.last) {
                ticker.pause();
            }
            gridWidget.animateGuess(guess);
        } else {
            if (x1 != x2 && y1 != y2) {
                Toast.makeText(this, "Invalid Selection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onWin() {
        onBackPressed();
    }
}
