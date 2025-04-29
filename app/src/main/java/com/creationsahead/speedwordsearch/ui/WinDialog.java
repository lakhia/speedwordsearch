package com.creationsahead.speedwordsearch.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.creationsahead.speedwordsearch.R;

public class WinDialog extends Dialog {

    private final int score;
    private final WinDialogListener listener;

    public interface WinDialogListener {
        void onNextLevelClicked();
        void onMainMenuClicked();
    }

    public WinDialog(@NonNull Context context, int score, WinDialogListener listener) {
        super(context);
        this.score = score;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_win);

        // Make dialog background transparent to show rounded corners
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Set up the score display
        TextView scoreTextView = findViewById(R.id.score_text);
        scoreTextView.setText("Score: " + score);

        // Apply celebratory animations
        applyCelebrationAnimations();

        // Set up buttons
        Button nextLevelButton = findViewById(R.id.next_level_button);
        Button mainMenuButton = findViewById(R.id.main_menu_button);

        nextLevelButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNextLevelClicked();
            }
        });

        mainMenuButton.setOnClickListener(v -> {
            dismiss();
            if (listener != null) {
                listener.onMainMenuClicked();
            }
        });
    }

    private void applyCelebrationAnimations() {
        // Apply different animations to various elements
        View confettiView = findViewById(R.id.confetti_view);
        View starBurstView = findViewById(R.id.star_burst);
        TextView congratsText = findViewById(R.id.congrats_text);

        // Scale animation for congratulation text
        Animation scaleAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.scale_up_down);
        congratsText.startAnimation(scaleAnimation);

        // Rotate animation for star burst
        Animation rotateAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_burst);
        starBurstView.startAnimation(rotateAnimation);

        // Alpha animation for confetti (fade in)
        Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        confettiView.startAnimation(fadeIn);

        // Optional: Start confetti particle animation if you're using a custom view
        if (confettiView instanceof ConfettiView) {
            ((ConfettiView) confettiView).startConfetti();
        }
    }
}
