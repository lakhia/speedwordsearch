package creationsahead.speedwordsearch.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import android.widget.LinearLayout;
import creationsahead.speedwordsearch.Game;
import creationsahead.speedwordsearch.GameCallback;
import creationsahead.speedwordsearch.ProgressTracker;
import creationsahead.speedwordsearch.R ;
import creationsahead.speedwordsearch.TickerCallback;

/**
 * Primary activity for game play
 */
public class GameActivity extends Activity implements GameCallback, TickerCallback {
    public static final String LOSE = "lose";
    public static final String WIN = "win";
    private static final int TIME_LIMIT = 60 * 5;
    private GridWidget gridWidget;
    private WordListWidget wordListWidget;
    private ScoreBar scoreBar;
    private LinearLayout topLayout;
    private Ticker timer;
    private Game game;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.game);

        game = ProgressTracker.getInstance().game;
        game.populatePuzzle();
        game.callback = this;

        topLayout = findViewById(R.id.topLayout);
        if (gridWidget == null || wordListWidget == null || scoreBar == null) {
            scoreBar = new ScoreBar(this, null);
            wordListWidget = new WordListWidget(this, null);
            gridWidget = new GridWidget(this, null);
        }
        topLayout.addView(scoreBar);
        topLayout.addView(wordListWidget);
        topLayout.addView(gridWidget);

        if (timer != null) {
            timer.pause();
        }
        timer = new Ticker(this, this, TIME_LIMIT);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            topLayout.setOrientation(LinearLayout.HORIZONTAL);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            topLayout.setOrientation(LinearLayout.VERTICAL);
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        timer.startOrResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.pause();
    }

    @Override
    public void onWin(@NonNull Game game) {
        ProgressTracker.getInstance().incrementLevel();
        finishActivity(WIN);
    }

    @Override
    public void onTick(int tickCount) {
        if (tickCount <= 0) {
            finishActivity(LOSE);
        } else {
            if (tickCount % 10 == 1) {
                game.onTick((TIME_LIMIT - tickCount) / 10);
            }
            scoreBar.onTick(tickCount);
        }
    }

    @Override
    public void onBackPressed() {
        finishActivity(LOSE);
    }

    private void finishActivity(String outcome) {
        game.callback = null;

        Intent intent = new Intent(this, LevelActivity.class);
        intent.setAction(outcome);
        startActivity(intent);
        finish();
    }
}
