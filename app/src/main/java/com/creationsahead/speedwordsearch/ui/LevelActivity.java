package com.creationsahead.speedwordsearch.ui;

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
        levelListView = createLevelListView();
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addContentView(levelListView, params);
    }

    protected LevelListView createLevelListView() {
        return new LevelListView(this, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        levelListView.unHideList();
    }
}
