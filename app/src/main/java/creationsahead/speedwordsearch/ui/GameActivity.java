package creationsahead.speedwordsearch.ui;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.game);

        ProgressTracker.getInstance().game.populatePuzzle();

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
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            topLayout.setOrientation(LinearLayout.HORIZONTAL);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            topLayout.setOrientation(LinearLayout.VERTICAL);
        }
        super.onConfigurationChanged(newConfig);
    }
}
