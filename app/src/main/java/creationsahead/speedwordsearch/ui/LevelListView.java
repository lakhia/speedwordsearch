package creationsahead.speedwordsearch.ui;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import creationsahead.speedwordsearch.LevelTracker;
import creationsahead.speedwordsearch.ProgressTracker;
import creationsahead.speedwordsearch.R;
import creationsahead.speedwordsearch.mod.Level;

/**
 * Shows list of levels
 */
public class LevelListView extends FrameLayout implements AdapterView.OnItemClickListener {

    private final LevelAdapter mAdapter;
    private View clickedLevel;

    public LevelListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        ListView listView = new ListView(context);
        addView(listView);
        mAdapter = new LevelAdapter(context, R.layout.single_level, LevelTracker.levels);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(@NonNull AdapterView<?> adapterView, View view, int position, long id) {
        clickedLevel = view;
        new ListAnimator((ViewGroup) view.getParent(), view, true,
            new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    Level level = (Level) adapterView.getItemAtPosition(position);
                    ProgressTracker.getInstance().createGame(level);
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    Intent intent = new Intent(view.getContext(), GameActivity.class);
                    view.getContext().startActivity(intent);
                }

                @Override
                public void onAnimationCancel(Animator animator) {}

                @Override
                public void onAnimationRepeat(Animator animator) {}
        });
    }

    public void unHideList() {
        if (clickedLevel != null) {
            new ListAnimator((ViewGroup) clickedLevel.getParent(), clickedLevel,
                    false, new Animator.AnimatorListener() {

                @Override
                public void onAnimationStart(@NonNull Animator animator) {}

                @Override
                public void onAnimationEnd(@NonNull Animator animator) {
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onAnimationCancel(@NonNull Animator animator) {}

                @Override
                public void onAnimationRepeat(@NonNull Animator animator) {}
            });
            clickedLevel = null;
        }
    }
}
