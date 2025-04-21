package com.creationsahead.speedwordsearch.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import com.creationsahead.speedwordsearch.R;
import com.google.android.material.appbar.MaterialToolbar;
import androidx.annotation.Nullable;

/**
 * Level selection activity
 */
public class LevelActivity extends Activity {

    private LevelListView levelListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.level);
        levelListView = createLevelListView();
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        FrameLayout frameLayout = findViewById(R.id.main_grid);
        frameLayout.addView(levelListView, params);
        MaterialToolbar materialToolbar = findViewById(R.id.toolbar);
        materialToolbar.setTitle(R.string.app_name);
    }

    protected LevelListView createLevelListView() {
        return new LevelListView(this, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        levelListView.unHideList();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent intent = new Intent(this, getPrevActivityClass());
        startActivity(intent);
    }

    protected Class<?> getPrevActivityClass() {
        return SubLevelActivity.class;
    }

}
