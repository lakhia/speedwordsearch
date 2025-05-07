package com.creationsahead.speedwordsearch.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import com.creationsahead.speedwordsearch.ProgressTracker;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import androidx.annotation.NonNull;

public class ConfettiView extends View {

    private static final int CONFETTI_COUNT = 100;
    @NonNull private final List<Confetti> confettiList = new ArrayList<>();
    @NonNull private final Random random = new Random();
    private boolean isAnimating = false;
    @NonNull private final Paint paint = new Paint();
    private Rect rect;

    // Confetti colors
    private final int[] colors = {
            Color.parseColor("#FFC107"), // Yellow
            Color.parseColor("#FF5722"), // Orange
            Color.parseColor("#4CAF50"), // Green
            Color.parseColor("#2196F3"), // Blue
            Color.parseColor("#9C27B0")  // Purple
    };

    public ConfettiView(Context context) {
        super(context);
        init();
    }

    public ConfettiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Initialize paint
        paint.setStyle(Paint.Style.FILL);
        rect = ProgressTracker.getInstance().displayRect;
    }

    public void startConfetti() {
        // Create confetti pieces
        confettiList.clear();
        for (int i = 0; i < CONFETTI_COUNT; i++) {
            confettiList.add(createRandomConfetti());
        }

        isAnimating = true;
        postInvalidateOnAnimation();
    }

    private Confetti createRandomConfetti() {
        float x = random.nextFloat() * rect.width();
        float y = -random.nextFloat() * rect.height() / 4; // Start above the view

        // Random size between 10 and 20 pixels
        float size = 10 + random.nextFloat() * 10;

        // Random color from our colors array
        int color = colors[random.nextInt(colors.length)];

        // Random velocity
        float vx = (random.nextFloat() - 0.5f) * 5;
        float vy = 2 + random.nextFloat() * 3;

        // Random rotation
        float rotation = random.nextFloat() * 360;
        float rotationSpeed = (random.nextFloat() - 0.5f) * 10;

        return new Confetti(x, y, size, color, vx, vy, rotation, rotationSpeed);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        if (!isAnimating) return;

        boolean stillAnimating = false;

        for (Confetti confetti : confettiList) {
            // Update position
            confetti.y += confetti.vy;
            confetti.x += confetti.vx;
            confetti.rotation += confetti.rotationSpeed;

            // Draw confetti
            paint.setColor(confetti.color);
            canvas.save();
            canvas.rotate(confetti.rotation, confetti.x, confetti.y);

            // Draw as a square
            canvas.drawRect(
                    confetti.x - confetti.size / 2,
                    confetti.y - confetti.size / 2,
                    confetti.x + confetti.size / 2,
                    confetti.y + confetti.size / 2,
                    paint
            );

            canvas.restore();

            // Check if this confetti is still falling
            if (confetti.y < getHeight() + confetti.size) {
                stillAnimating = true;
            }
        }

        isAnimating = stillAnimating;

        if (isAnimating) {
            postInvalidateOnAnimation();
        }
    }

    // Simple class to hold confetti properties
    private static class Confetti {
        float x, y;
        float size;
        int color;
        float vx, vy;
        float rotation;
        float rotationSpeed;

        Confetti(float x, float y, float size, int color, float vx, float vy, float rotation, float rotationSpeed) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.color = color;
            this.vx = vx;
            this.vy = vy;
            this.rotation = rotation;
            this.rotationSpeed = rotationSpeed;
        }
    }
}
