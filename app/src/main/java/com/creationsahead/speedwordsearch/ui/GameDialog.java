package com.creationsahead.speedwordsearch.ui;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.creationsahead.speedwordsearch.R;
import com.creationsahead.speedwordsearch.mod.Level;
import java.util.Random;
import static com.creationsahead.speedwordsearch.ui.GameApplication.ANIMATION_DURATION;

public class GameDialog extends Dialog {

    @NonNull private final GameDialogListener listener;
    @NonNull private final Random random;
    @NonNull private final DialogType type;
    @NonNull private final Level level;

    public enum DialogType {
        NONE,
        PAUSE,
        WIN,
        TIME,
    }

    public interface GameDialogListener {
        void onResumeGame();
        void onNextLevelClicked();
        void onMainMenuClicked();
    }

    public GameDialog(@NonNull Context context, @NonNull Level level, @NonNull GameDialogListener listener,
                      @NonNull DialogType type) {
        super(context);
        this.type = type;
        this.level = level;
        this.listener = listener;
        random = new Random();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_win);

        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // Set up the score display
        SmartRatingBar rating = findViewById(R.id.ratingBar);
        rating.setRatingNum(level.stars);

        // Set up buttons
        Button nextLevelButton = findViewById(R.id.next_level_button);
        Button mainMenuButton = findViewById(R.id.main_menu_button);

        nextLevelButton.setOnClickListener(v -> listener.onNextLevelClicked());
        mainMenuButton.setOnClickListener(v -> listener.onMainMenuClicked());

        String heading;
        TextView headingView = findViewById(R.id.congrats_text);
        TextView scoreTextView = findViewById(R.id.score_text);

        if (type == DialogType.WIN) {
            heading = "Level won!";
            applyCelebrationAnimations(headingView, scoreTextView);
        } else if (type == DialogType.PAUSE) {
            rating.setVisibility(View.GONE);
            scoreTextView.setVisibility(View.INVISIBLE);
            heading = "Game paused!";
            nextLevelButton.setText("Quit");
            mainMenuButton.setText("Resume Game");
            mainMenuButton.setOnClickListener(v -> listener.onResumeGame());
        } else if (level.won) {
            // Time ran out but user scored enough points
            heading = "That was close!";
            applyCelebrationAnimations(headingView, scoreTextView);
        } else {
            heading = "Try again!";
            nextLevelButton.setText("Try Again");
            scoreTextView.setText(getContext().getString(R.string.score, level.score));
        }
        headingView.setText(heading);
    }

    private void applyRandomRotation(View view) {
        float fromDegrees = random.nextInt(360);
        float toDegrees = fromDegrees + random.nextInt(540) + 180; // Random spin between 180°–720°
        long duration = 5000 + random.nextInt(10000); // 5000ms–15000ms

        RotateAnimation rotate = new RotateAnimation(
                fromDegrees,
                toDegrees,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );

        rotate.setDuration(duration);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setRepeatMode(Animation.REVERSE);
        rotate.setInterpolator(new AccelerateDecelerateInterpolator());
        view.startAnimation(rotate);
    }

    private void applyCelebrationAnimations(TextView congratsText, TextView scoreTextView) {
        NumberAnimator anim = new NumberAnimator(congratsText, ANIMATION_DURATION, 3.5f, 0) {
            @Override
            public void setWidget(int n) {}
        };
        anim.start(0);

        // Rotate animation for star bursts
        View starBurstView = findViewById(R.id.star_burst1);
        applyRandomRotation(starBurstView);
        starBurstView = findViewById(R.id.star_burst2);
        applyRandomRotation(starBurstView);
        starBurstView = findViewById(R.id.star_burst3);
        applyRandomRotation(starBurstView);

        // Alpha animation for confetti (fade in)
        ConfettiView confettiView = findViewById(R.id.confetti_view);
        confettiView.startConfetti();

        anim = new NumberAnimator(scoreTextView, ANIMATION_DURATION, 1, 0) {
            @Override
            public void setWidget(int n) {
                scoreTextView.setText(getContext().getString(R.string.score, n));
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                if (level.bonus > 0) {
                    NumberAnimator anim = new NumberAnimator(scoreTextView, ANIMATION_DURATION, 1.2f, 0) {
                        @Override
                        public void setWidget(int n) {
                            scoreTextView.setText(getContext().getString(R.string.score_bonus, level.score, n));
                        }
                    };
                    anim.start(level.bonus);
                }
            }
        };
        anim.start(level.score);
    }
}
