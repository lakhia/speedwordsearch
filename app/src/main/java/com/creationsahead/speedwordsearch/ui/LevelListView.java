package com.creationsahead.speedwordsearch.ui;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.creationsahead.speedwordsearch.ProgressTracker;
import com.creationsahead.speedwordsearch.R;
import com.creationsahead.speedwordsearch.mod.Level;
import static com.creationsahead.speedwordsearch.ui.GameApplication.ANIMATION_DURATION;

/**
 * Shows list of levels
 */
public class LevelListView extends FrameLayout implements AdapterView.OnItemClickListener {

    private LevelAdapter mAdapter;
    private View clickedLevel;

    public LevelListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        createAdapter(context);
    }

    @Override
    public void onItemClick(@NonNull AdapterView<?> adapterView, View view, int position, long id) {
        clickedLevel = view;
        bounceAnimation();

        new ListAnimator(view, true,
            new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(@NonNull Animator animator) {
                    LevelListView.this.onAnimationStart(adapterView, position);
                }

                @Override
                public void onAnimationEnd(@NonNull Animator animator) {
                    Intent intent = new Intent(view.getContext(), getOnClickClass());
                    view.getContext().startActivity(intent);
                }

                @Override
                public void onAnimationCancel(@NonNull Animator animator) {}

                @Override
                public void onAnimationRepeat(@NonNull Animator animator) {}
        });
    }


    protected void createAdapter(Context context) {
        mAdapter = new LevelAdapter(context, R.layout.single_level,
                ProgressTracker.getInstance().currentSubLevel.levels);
        ListView listView = new ListView(context);
        addView(listView);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);
    }

    protected void onAnimationStart(AdapterView<?> adapterView, int position) {
        Level level = (Level) adapterView.getItemAtPosition(position);
        ProgressTracker.getInstance().createGame(level);
    }

    protected Class<?> getOnClickClass() {
        return GameActivity.class;
    }

    public void unHideList() {
        if (clickedLevel != null) {
            notifyDataSetChanged();
            bounceAnimation();
            new ListAnimator(clickedLevel, false, null);
            clickedLevel = null;
        }
    }

    protected void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    private void bounceAnimation() {
        ScaleAnimation scaleAnimation1 = new ScaleAnimation(1.0f, 1.75f,
                1.0f, 1.75f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        ScaleAnimation scaleAnimation2 = new ScaleAnimation(1.0f, 1/1.75f,
                1.0f, 1/1.75f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation1.setDuration(ANIMATION_DURATION/4);
        scaleAnimation2.setDuration(ANIMATION_DURATION/4);
        scaleAnimation2.setStartOffset(ANIMATION_DURATION/4);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(scaleAnimation1);
        animationSet.addAnimation(scaleAnimation2);
        TextView textView = clickedLevel.findViewById(R.id.time);
        textView.startAnimation(animationSet);
        View viewGroup = clickedLevel.findViewById(R.id.ratingBar);
        viewGroup.startAnimation(animationSet);
    }
}
