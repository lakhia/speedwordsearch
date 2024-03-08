package creationsahead.speedwordsearch.ui;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import creationsahead.speedwordsearch.Event;
import creationsahead.speedwordsearch.R;
import creationsahead.speedwordsearch.mod.Level;
import creationsahead.speedwordsearch.utils.Utils;
import org.greenrobot.eventbus.EventBus;

import static creationsahead.speedwordsearch.ui.GameApplication.ANIMATION_DURATION;

/**
 * Displays all the levels
 */
public class LevelAdapter extends ArrayAdapter<Level> implements View.OnClickListener,
    ValueAnimator.AnimatorUpdateListener {

    private ViewParent clickedLevel;

    public LevelAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    @Override
    public void addAll(Level... items) {
        for (Level level: items) {
            if (level != null) {
                super.add(level);
            }
        }
    }

    /// Inflates a checkbox with text
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.single_level, null);
        }

        Level level = getItem(position);

        Button button = convertView.findViewById(R.id.toggleButton);
        button.setText(level.name);
        button.setOnClickListener(this);
        button.setTag(level);

        TextView textView = convertView.findViewById(R.id.time);
        textView.setText(Utils.formatTime(level.timeUsed));

        RatingBar bar = convertView.findViewById(R.id.ratingBar);
        bar.setRating(level.stars);
        LayerDrawable stars = (LayerDrawable) bar.getProgressDrawable();
        stars.getDrawable(0).setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_IN);
        return convertView;
    }

    @Override
    public void onClick(@NonNull View view) {
        clickedLevel = view.getParent();
        ValueAnimator anim = ValueAnimator.ofFloat(1, 0.05f);
        anim.setDuration(ANIMATION_DURATION/8);
        anim.setInterpolator(new LinearInterpolator());
        anim.addUpdateListener(this);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {}

            @Override
            public void onAnimationEnd(Animator animator) {
                Event event = Event.LEVEL_STARTED;
                event.levelNumber = ((Level)view.getTag()).number;
                EventBus.getDefault().post(event);
                Context context = getContext();
                Intent intent = new Intent(context, GameActivity.class);
                context.startActivity(intent);
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