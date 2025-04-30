package com.creationsahead.speedwordsearch.ui;

import androidx.annotation.NonNull;

public class SubLevelActivity extends LevelActivity {
    @NonNull
    @Override
    protected LevelListView createLevelListView() {
        return new SubLevelListView(this, null);
    }

    @NonNull
    @Override
    protected Class<?> getPrevActivityClass() {
        return MainActivity.class;
    }
}
