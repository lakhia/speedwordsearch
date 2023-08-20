package creationsahead.speedwordsearch.ui;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import creationsahead.speedwordsearch.Cell;
import creationsahead.speedwordsearch.DrawCallback;
import creationsahead.speedwordsearch.Game;
import creationsahead.speedwordsearch.ProgressTracker;
import creationsahead.speedwordsearch.R;
import creationsahead.speedwordsearch.Selection;

/**
 * Primary activity for game play
 */
public class GameActivity extends Activity implements View.OnClickListener, DrawCallback {
    private int lastX = -1, lastY = -1;
    private View lastSelection = null;
    private Game game;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        ProgressTracker.init();
        createUI();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);
    }

    public void createUI() {
        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);
        int numCells = ProgressTracker.getCurrentConfig().sizeX;
        int cellSize = Math.min(displaySize.x, displaySize.y) / numCells;

        game = ProgressTracker.getCurrentGame();
        game.populatePuzzle(5, 20);
        game.populatePuzzle(4, 10);

        TableLayout layout = findViewById(R.id.layout);
        for (int i = 0; i < numCells; i++) {
            TableRow row = new TableRow(this);
            row.setMinimumHeight(cellSize);
            layout.addView(row);

            for (int j = 0; j < numCells; j++) {
                ContextThemeWrapper newContext = new ContextThemeWrapper(this, R.style.PuzzleLetter);
                TextView textView = new TextView(newContext, null);
                Cell cell = game.getCell(i, j);
                textView.setText(cell.toString());
                textView.setMinWidth(cellSize);
                textView.setMinHeight(cellSize);
                textView.setOnClickListener(this);
                textView.setTag(R.string.row, i);
                textView.setTag(R.string.column, j);
                row.addView(textView);
                cell.tag = textView;
            }
        }
        Cell.callback = this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Cell.callback = null;
    }

    @Override
    public void onClick(View view) {
        int currentX = (int) view.getTag(R.string.row);
        int currentY = (int) view.getTag(R.string.column);

        if (lastX == -1 && lastY == -1) {
            lastX = currentX;
            lastY = currentY;
            lastSelection = view;
            view.setBackgroundResource(R.drawable.border_selected);
        } else {
            lastSelection.setBackgroundResource(R.drawable.border);
            Selection selection = Selection.isValid(lastX, lastY, currentX, currentY);
            lastX = -1;
            lastY = -1;
            String error = null;
            if (selection != null) {
                if (!game.guess(selection)) {
                    error = "Did not find word";
                }
            } else {
                error = "Invalid Selection";
            }
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onInvalidated(final Cell cell) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView textView = (TextView) cell.tag;
                textView.setText(cell.toString());
            }
        });
    }
}
