package creationsahead.speedwordsearch.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ListView;
import creationsahead.speedwordsearch.ProgressTracker;
import creationsahead.speedwordsearch.R;

public class LevelActivity extends Activity {
    private ListView mListView;
    private LevelAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.level);
        mListView = findViewById(R.id.level_list);
        mAdapter = new LevelAdapter(this, R.layout.single_level);
        mAdapter.addAll(ProgressTracker.getInstance().levels);
        mListView.setAdapter(mAdapter);
    }
}
