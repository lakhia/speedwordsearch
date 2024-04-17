package creationsahead.speedwordsearch.ui;

import android.content.Context;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.util.AttributeSet;
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

    private Ticker timer;
    private ScoreBar scoreBar;
    private Game game;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        game = ProgressTracker.getInstance().game;
        game.populatePuzzle();
        inflate(context, R.layout.game, this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        scoreBar = findViewById(R.id.scoreBar);
        timer = new Ticker(getContext(), this,
                           ProgressTracker.getInstance().config.timeLimit);
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
            game.onTick(tickCount);
            scoreBar.onTick(tickCount);
        }
    }
}
