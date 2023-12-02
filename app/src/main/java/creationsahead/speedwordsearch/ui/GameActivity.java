package creationsahead.speedwordsearch.ui;

import android.app.Activity;
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
    private static final int TIME_LIMIT = 60 * 5;
    @Nullable private GridWidget gridWidget;
    @Nullable private WordListWidget wordListWidget;
    @Nullable private ScoreBar scoreBar;
    private LinearLayout topLayout;
    private Ticker timer;
    private Game game;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.game);
        reset();
    }

    private void reset() {
        game = ProgressTracker.getInstance().game;
        game.populatePuzzle();
        game.callback = this;

        topLayout = findViewById(R.id.topLayout);
        if (gridWidget == null || wordListWidget == null) {
            scoreBar = new ScoreBar(this, null);
            wordListWidget = new WordListWidget(this, null);
            gridWidget = new GridWidget(this, null);
        }
        topLayout.addView(scoreBar);
        topLayout.addView(wordListWidget);
        topLayout.addView(gridWidget);

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
        game.callback = null;

        // Remove and recreate all views
        topLayout.removeAllViews();
        gridWidget = null;
        wordListWidget = null;
        reset();
    }

    @Override
    public void onTick(int tickCount) {
        game.onTick(TIME_LIMIT - tickCount);
        if (scoreBar != null) {
            scoreBar.onTick(tickCount);
        }
    }
}
