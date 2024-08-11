package creationsahead.speedwordsearch.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import androidx.annotation.Nullable;

/**
 * Level selection activity
 */
public class LevelActivity extends Activity {

    private LevelListView levelListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        levelListView = new LevelListView(this, null);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addContentView(levelListView, params);
    }

    @Override
    protected void onResume() {
        super.onResume();
        levelListView.unHideList();
    }
}
