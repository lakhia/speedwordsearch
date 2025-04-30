package com.creationsahead.speedwordsearch.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.creationsahead.speedwordsearch.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

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
        ConstraintLayout topLayout = findViewById(R.id.topLayout);
        AppBarLayout appBarLayout = findViewById(R.id.appBarLayout2);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT, 0);
        params.topToBottom = appBarLayout.getId();
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        topLayout.addView(levelListView, params);

        MaterialToolbar materialToolbar = findViewById(R.id.toolbar);
        materialToolbar.setTitle(R.string.app_name);
    }

    @NonNull
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

    @NonNull
    protected Class<?> getPrevActivityClass() {
        return SubLevelActivity.class;
    }

}
