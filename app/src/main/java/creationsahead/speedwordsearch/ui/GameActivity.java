package creationsahead.speedwordsearch.ui;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ContextThemeWrapper;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import creationsahead.speedwordsearch.R;

/**
 * Primary activity for game play
 */
public class GameActivity extends Activity {
    private int cellSize = 0;
    private final static float NUM_CELLS = 5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);
        cellSize = (int) (Math.min(displaySize.x, displaySize.y) / NUM_CELLS);
        createUI();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);
    }

    public void createUI() {
        TableLayout layout = findViewById(R.id.layout);
        for (int i = 0; i < NUM_CELLS; i++) {
            TableRow row = new TableRow(this);
            row.setMinimumHeight(cellSize);
            layout.addView(row);

            for (int j = 0; j < NUM_CELLS; j++) {
                ContextThemeWrapper newContext = new ContextThemeWrapper(this, R.style.PuzzleLetter);
                TextView textView = new TextView(newContext, null);
                textView.setText("z" + i + j);
                textView.setMinWidth(cellSize);
                textView.setMinHeight(cellSize);
                row.addView(textView);
            }
        }
    }
}
