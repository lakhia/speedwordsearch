package creationsahead.speedwordsearch.ui;

import android.content.Context;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import creationsahead.speedwordsearch.Event;
import creationsahead.speedwordsearch.Game;
import creationsahead.speedwordsearch.ProgressTracker;
import creationsahead.speedwordsearch.R;
import creationsahead.speedwordsearch.TickerCallback;
import org.greenrobot.eventbus.EventBus;

/**
 * Shows entire game widget
 */
public class GameView extends ConstraintLayout implements TickerCallback {

    @NonNull
    private final Ticker timer;
    private final ScoreBar scoreBar;
    protected final Game game;

    public GameView(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        game = ProgressTracker.getInstance().game;
        game.populatePuzzle();
        timer = new Ticker(getContext(), this,
                ProgressTracker.getInstance().config.timeLimit);
        inflate(context, R.layout.game, this);
        scoreBar = findViewById(R.id.scoreBar);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        timer.destroy();
    }

    @Override
    public void onTick(int tickCount) {
        if (tickCount <= 0) {
            EventBus.getDefault().post(Event.LEVEL_LOST);
        } else {
            scoreBar.onTick(tickCount);
        }
    }
}
