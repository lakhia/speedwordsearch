package com.creationsahead.speedwordsearch.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.creationsahead.speedwordsearch.Game;
import com.creationsahead.speedwordsearch.GridCallback;
import com.creationsahead.speedwordsearch.Guess;
import com.creationsahead.speedwordsearch.ProgressTracker;
import com.creationsahead.speedwordsearch.R;
import com.creationsahead.speedwordsearch.TickerCallback;
import com.creationsahead.speedwordsearch.mod.Level;
import com.creationsahead.speedwordsearch.utils.SoundManager;
import static com.creationsahead.speedwordsearch.mod.Level.TIME_LEFT;

/**
 * Primary activity for game play
 */
public class GameActivity extends Activity implements TickerCallback, GridCallback, GameDialog.GameDialogListener {

    private SoundManager sound_manager;
    private Ticker ticker;
    private ScoreBar scoreBar;
    private GridWidget gridWidget;
    private Game game;
    private GameDialog.DialogType dialog_state;
    private GameDialog gameDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        game = ProgressTracker.getInstance().getGame();
        sound_manager = SoundManager.getInstance();
        setContentView(R.layout.game);
        scoreBar = findViewById(R.id.scoreBar);
        gridWidget = findViewById(R.id.grid);

        int timeLeft = TIME_LEFT;
        dialog_state = GameDialog.DialogType.NONE;
        if (savedInstanceState != null) {
            timeLeft = savedInstanceState.getInt("score");
            int state = savedInstanceState.getInt("state");
            dialog_state = GameDialog.DialogType.values()[state];
        }
        ticker = new Ticker(this, this, timeLeft);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("score", ticker.getTimeLeft());
        outState.putInt("state", dialog_state.ordinal());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dialog_state == GameDialog.DialogType.NONE) {
            sound_manager.resume();
            ticker.resume();
        } else {
            createGameDialog(dialog_state);
        }
        // Hack that ensures that the gridWidget has been measured before we setup the touch handler
        gridWidget.postDelayed(() -> gridWidget.setupTouchHandler(this), 500);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sound_manager.pause();
        ticker.pause();
    }

    @Override
    public void onBackPressed() {
        if (dialog_state == GameDialog.DialogType.NONE) {
            createGameDialog(GameDialog.DialogType.PAUSE);
        }
    }

    @Override
    public void onTick(int tickCount) {
        scoreBar.onTick(tickCount);
        if (tickCount <= 0) {
            createGameDialog(GameDialog.DialogType.TIME);
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

    public void createGameDialog(@NonNull GameDialog.DialogType type) {
        Level level = ProgressTracker.getInstance().currentLevel;
        level.score();
        dialog_state = type;
        gridWidget.setVisibility(View.INVISIBLE);
        ticker.pause();
        sound_manager.pause();
        gameDialog = new GameDialog(this, level, this, type);
        gameDialog.setCancelable(false); // Prevent dismissing by tapping outside
        gameDialog.show();
    }

    @Override
    public void onWin() {
        createGameDialog(GameDialog.DialogType.WIN);
    }

    @Override
    public void onResumeGame() {
        gameDialog.dismiss();
        dialog_state = GameDialog.DialogType.NONE;
        gridWidget.setVisibility(View.VISIBLE);
        ticker.resume();
        sound_manager.resume();
    }

    @Override
    public void onNextLevelClicked() {
        gameDialog.dismiss();
        finish();
        Intent intent = new Intent(GameActivity.this, LevelActivity.class);
        startActivity(intent);
    }

    @Override
    public void onMainMenuClicked() {
        gameDialog.dismiss();
        finish();
        Intent intent = new Intent(GameActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
