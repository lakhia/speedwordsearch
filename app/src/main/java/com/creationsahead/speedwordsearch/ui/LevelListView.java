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
import com.creationsahead.speedwordsearch.LevelTracker;
import com.creationsahead.speedwordsearch.ProgressTracker;
import com.creationsahead.speedwordsearch.R;
import com.creationsahead.speedwordsearch.mod.Level;
import com.creationsahead.speedwordsearch.mod.SubLevel;
import com.creationsahead.speedwordsearch.utils.Utils;
import org.greenrobot.eventbus.EventBus;
import static com.creationsahead.speedwordsearch.ui.GameApplication.ANIMATION_DURATION;

/**
 * Shows list of levels
 */
public class LevelListView extends FrameLayout implements AdapterView.OnItemClickListener {

    private LevelAdapter mAdapter;
    private View clickedLevel;
    private TextView timeTextView;
    private SmartRatingBar ratingView;

    public LevelListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        createAdapter(context);
    }

    @Override
    public void onItemClick(@NonNull AdapterView<?> adapterView, @NonNull View view, int position, long id) {
        clickedLevel = view;
        timeTextView = clickedLevel.findViewById(R.id.time);
        ratingView = clickedLevel.findViewById(R.id.ratingBar);
        bounceAnimation();

        new ListFadeAnimator(view, true) {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {
                LevelListView.this.onAnimationStart(adapterView, position);
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                Intent intent = new Intent(view.getContext(), getOnClickClass());
                view.getContext().startActivity(intent);
            }
        };
    }

    public void unHideList() {
        if (clickedLevel == null) {
            return;
        }
        Level level = getLevel();
        level.score();
        new ListFadeAnimator(clickedLevel, false) {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {}

            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                notifyDataSetChanged();
            }
        };
        NumberAnimator anim = new NumberAnimator(timeTextView,
                ANIMATION_DURATION, 1.75f, 0) {
            @Override
            public void setWidget(int n) {
                timeTextView.setText(Utils.formatTime(n));
            }
        };
        anim.start(level.timeUsed);
        anim = new NumberAnimator(ratingView, ANIMATION_DURATION, 1.75f, 0) {
            @Override
            public void setWidget(int n) {
                ratingView.setRatingNum(n / 10.0f);
            }
        };
        clickedLevel = null;
        anim.start((int) (level.stars * 10));
    }

    protected void createAdapter(@NonNull Context context) {
        mAdapter = new LevelAdapter(context, R.layout.single_level,
                ProgressTracker.getInstance().getCurrentSubLevel().levels);
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

    protected Level getLevel() {
        return ProgressTracker.getInstance().currentLevel;
    }

    protected void notifyDataSetChanged() {
        SubLevel subLevel = ProgressTracker.getInstance().getCurrentSubLevel();
        LevelTracker.createLevel(subLevel);
        EventBus.getDefault().post(getLevel());
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
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
        timeTextView.startAnimation(animationSet);
        ratingView.startAnimation(animationSet);
    }
}
