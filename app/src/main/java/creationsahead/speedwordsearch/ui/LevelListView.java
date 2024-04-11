package creationsahead.speedwordsearch.ui;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import creationsahead.speedwordsearch.ProgressTracker;
import creationsahead.speedwordsearch.R;
import creationsahead.speedwordsearch.mod.Level;
import org.greenrobot.eventbus.EventBus;

import static creationsahead.speedwordsearch.ui.GameApplication.ANIMATION_DURATION;

/**
 * Shows list of levels
 */
public class LevelListView extends FrameLayout implements AdapterView.OnItemClickListener,
    ValueAnimator.AnimatorUpdateListener {

    private View clickedLevel;

    public LevelListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        ListView listView = new ListView(context);
        addView(listView);
        LevelAdapter mAdapter = new LevelAdapter(context, R.layout.single_level);
        mAdapter.addAll(ProgressTracker.getInstance().levels);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);
    }

    //@Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Level level = (Level) adapterView.getItemAtPosition(position);
        EventBus.getDefault().post(level);
    }

    // TODO: Figure out why this doesn't work
    public void oonItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        clickedLevel = view;
        ValueAnimator anim = ValueAnimator.ofFloat(1, 0.05f);
        anim.setDuration(ANIMATION_DURATION/4);
        anim.setInterpolator(new LinearInterpolator());
        anim.addUpdateListener(this);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {}

            @Override
            public void onAnimationEnd(Animator animator) {
                Level level = (Level) adapterView.getItemAtPosition(position);
                EventBus.getDefault().post(level);
            }

            @Override
            public void onAnimationCancel(Animator animator) {}

            @Override
            public void onAnimationRepeat(Animator animator) {}
        });
        anim.start();
    }

    @Override
    public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator) {
        float fraction = (float) valueAnimator.getAnimatedValue();
        ViewGroup row = (ViewGroup) clickedLevel.getParent();
        for (int itemPos = row.getChildCount() - 1; itemPos >= 0; itemPos--) {
            View view = row.getChildAt(itemPos);
            if (view != clickedLevel) {
                if (fraction < 0.1) {
                    view.setVisibility(View.INVISIBLE);
                } else {
                    view.setAlpha(fraction);
                }
            }
        }
    }

}
