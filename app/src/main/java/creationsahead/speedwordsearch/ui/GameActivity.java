package creationsahead.speedwordsearch.ui;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
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
import java.io.IOException;
import java.io.InputStream;

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
        try {
            InputStream inputStream = getAssets().open("words_124k.db");
            ProgressTracker.init(inputStream);
            createGrid();
            createWordList();
        } catch (IOException ignored) {
            Toast.makeText(this, "Unable to load words", Toast.LENGTH_LONG).show();
        }
    }

    private void createWordList() {
        GridLayout gridLayout = findViewById(R.id.wordsListLayout);
        String[] words = game.getWords();
        for (String word: words) {
            ContextThemeWrapper newContext = new ContextThemeWrapper(this, R.style.WordList);
            TextView textView = new TextView(newContext, null);
            textView.setText(word);
            gridLayout.addView(textView);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);
        LinearLayout layout = findViewById(R.id.topLayout);
        if (newConfig.getLayoutDirection() == Configuration.ORIENTATION_LANDSCAPE) {
            layout.setOrientation(LinearLayout.VERTICAL);
        } else {
            layout.setOrientation(LinearLayout.HORIZONTAL);
        }
    }

    public void createGrid() {
        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);
        int numCells = ProgressTracker.getCurrentConfig().sizeX;
        int cellSize = Math.min(displaySize.x, displaySize.y) / numCells;

        game = ProgressTracker.getCurrentGame();
        game.populatePuzzle(5, 20);
        game.populatePuzzle(4, 10);

        TableLayout layout = findViewById(R.id.gridLayout);
        ViewGroup.LayoutParams params = layout.getLayoutParams();
        params.width = cellSize * numCells;
        params.height = cellSize * numCells;

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
        runOnUiThread(() -> {
            TextView textView = (TextView) cell.tag;
            textView.setText(cell.toString());
        });
    }
}
