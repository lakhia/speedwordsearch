package creationsahead.speedwordsearch.ui;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import creationsahead.speedwordsearch.Answer;
import creationsahead.speedwordsearch.AnswerCallback;
import creationsahead.speedwordsearch.Cell;
import creationsahead.speedwordsearch.DrawCallback;
import creationsahead.speedwordsearch.Game;
import creationsahead.speedwordsearch.ProgressTracker;
import creationsahead.speedwordsearch.R;
import creationsahead.speedwordsearch.Selection;

/**
 * Primary activity for game play
 */
public class GameActivity extends Activity implements View.OnClickListener,
    DrawCallback, AnswerCallback {
    private int lastX = -1, lastY = -1;
    @Nullable private View lastSelection = null;
    private Game game;
    private TableLayout puzzleLayout;
    private GridLayout wordLayout;
    private LinearLayout topLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    private void initUI() {
        setContentView(R.layout.game);
        topLayout = findViewById(R.id.topLayout);
        if (puzzleLayout == null || wordLayout == null) {
            puzzleLayout = findViewById(R.id.puzzleLayout);
            wordLayout = findViewById(R.id.wordsListLayout);
            createGrid();
        } else {
            topLayout.removeAllViews();
            topLayout.addView(puzzleLayout);
            topLayout.addView(wordLayout);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        topLayout.removeAllViews();
        super.onConfigurationChanged(newConfig);
        initUI();
    }

    private void createGrid() {
        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);
        int numCells = ProgressTracker.config.sizeX;
        int cellSize = Math.min(displaySize.x, displaySize.y) / numCells;
        Answer.callback = this;

        game = ProgressTracker.game;
        game.populatePuzzle();

        ViewGroup.LayoutParams params = puzzleLayout.getLayoutParams();
        params.width = cellSize * numCells;
        params.height = cellSize * numCells;

        for (int i = 0; i < numCells; i++) {
            TableRow row = new TableRow(this);
            row.setMinimumHeight(cellSize);
            puzzleLayout.addView(row);

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
        Answer.callback = null;
    }

    @Override
    public void onClick(@NonNull View view) {
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
    public void onInvalidated(@NonNull final Cell cell) {
        TextView textView = (TextView) cell.tag;
        textView.setText(cell.toString());
    }

    @Override
    public void onUpdate(@NonNull Answer answer) {
        if (answer.tag == null) {
            ContextThemeWrapper newContext = new ContextThemeWrapper(this, R.style.WordList);
            TextView textView = new TextView(newContext, null);
            textView.setText(answer.word);
            wordLayout.addView(textView);
            answer.tag = textView;
        } else {
            View view = (View) answer.tag;
            wordLayout.removeView(view);
            if (wordLayout.getChildCount() == 0) {
                // Win
                // TODO: seed and restart new game
                ProgressTracker.incrementLevel(1);
            }
        }
    }
}
