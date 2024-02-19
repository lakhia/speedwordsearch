package creationsahead.speedwordsearch.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ListView;
import creationsahead.speedwordsearch.Event;
import creationsahead.speedwordsearch.ProgressTracker;
import creationsahead.speedwordsearch.R;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class LevelActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.level);
        ListView mListView = findViewById(R.id.level_list);
        LevelAdapter mAdapter = new LevelAdapter(this, R.layout.single_level);
        mAdapter.addAll(ProgressTracker.getInstance().levels);
        mListView.setAdapter(mAdapter);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLevelStart(Event event) {
        if (event == Event.LEVEL_STARTED) {
            finish();
        }
    }
}
