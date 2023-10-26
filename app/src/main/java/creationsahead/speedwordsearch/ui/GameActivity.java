package creationsahead.speedwordsearch.ui;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.LinearLayout;
import creationsahead.speedwordsearch.ProgressTracker;
import creationsahead.speedwordsearch.R ;

/**
 * Primary activity for game play
 */
public class GameActivity extends Activity {
    private GridWidget gridWidget;
    private WordListWidget wordListWidget;
    private LinearLayout topLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProgressTracker.getInstance().game.populatePuzzle();
        initUI();
    }

    private void initUI() {
        setContentView(R.layout.game);
        topLayout = findViewById(R.id.topLayout);
        if (gridWidget == null || wordListWidget == null) {
            gridWidget = new GridWidget(this);
            wordListWidget = new WordListWidget(this);
        }
        topLayout.addView(gridWidget);
        topLayout.addView(wordListWidget);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        topLayout.removeAllViews();
        super.onConfigurationChanged(newConfig);
        initUI();
    }

}
