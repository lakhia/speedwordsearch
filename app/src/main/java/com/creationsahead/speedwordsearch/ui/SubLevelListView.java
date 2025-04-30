package com.creationsahead.speedwordsearch.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.creationsahead.speedwordsearch.LevelTracker;
import com.creationsahead.speedwordsearch.ProgressTracker;
import com.creationsahead.speedwordsearch.R;
import com.creationsahead.speedwordsearch.mod.Level;
import com.creationsahead.speedwordsearch.mod.SubLevel;
import org.greenrobot.eventbus.EventBus;

public class SubLevelListView extends LevelListView {
    private SubLevelAdapter adapter;

    public SubLevelListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void createAdapter(@NonNull Context context) {
        adapter = new SubLevelAdapter(context, R.layout.single_level, LevelTracker.subLevels);
        ListView listView = new ListView(context);
        addView(listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    protected void onAnimationStart(AdapterView<?> adapterView, int position) {
        SubLevel subLevel = adapter.getItem(position);
        EventBus.getDefault().post(subLevel);
    }

    @NonNull
    @Override
    protected Class<?> getOnClickClass() {
        return LevelActivity.class;
    }

    @Override
    protected void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected Level getLevel() {
        return ProgressTracker.getInstance().getCurrentSubLevel();
    }
}
