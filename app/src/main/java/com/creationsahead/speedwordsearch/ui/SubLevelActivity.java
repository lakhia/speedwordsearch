package com.creationsahead.speedwordsearch.ui;

public class SubLevelActivity extends LevelActivity {
    @Override
    protected LevelListView createLevelListView() {
        return new SubLevelListView(this, null);
    }
}
