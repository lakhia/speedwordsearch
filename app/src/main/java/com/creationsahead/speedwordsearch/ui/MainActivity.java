package com.creationsahead.speedwordsearch.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.creationsahead.speedwordsearch.ProgressTracker;
import com.creationsahead.speedwordsearch.R;
import com.google.android.material.appbar.MaterialToolbar;
import static android.util.TypedValue.COMPLEX_UNIT_PX;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @NonNull
    private final Character[][] lettersArray = new Character[][]{
            {'S', 'P', 'E', 'E', 'D'},
            {' ', 'W', 'O', 'R', 'D'},
            {'S', 'E', 'A', 'R', 'C', 'H'}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.play).setOnClickListener(this);
        findViewById(R.id.setting).setOnClickListener(this);
        MaterialToolbar materialToolbar = findViewById(R.id.toolbar);
        materialToolbar.setTitle(R.string.app_name);
    }

    @Override
    protected void onResume() {
        TableLayout table = findViewById(R.id.main_grid);
        super.onResume();
        setAnimation(table);
    }

    private void setAnimation(@NonNull TableLayout table) {
        table.removeAllViews();
        GameApplication app = (GameApplication) getApplicationContext();
        Typeface typeface = app.loader.letterTypeface;
        float fontSize = ProgressTracker.getInstance().normalizedFontSize / 7;

        int rowIndex = 1;
        for (Character[] letters : lettersArray) {
            TableRow row = new TableRow(this);
            table.addView(row);
            rowIndex++;
            int colIndex = 0;

            for (Character letter : letters) {
                colIndex++;
                ContextThemeWrapper newContext = new ContextThemeWrapper(this, R.style.PuzzleLetter);
                TextView textView = new TextView(newContext, null);
                if (letter == ' ') {
                    textView.setVisibility(View.INVISIBLE);
                } else {
                    textView.setText(letter.toString());
                    new TranslateAnimator(textView, (rowIndex + colIndex/5f) * -400,
                            5 - rowIndex + colIndex/4f);
                }
                textView.setTypeface(typeface);
                textView.setTextSize(COMPLEX_UNIT_PX, fontSize);
                textView.setMinimumHeight((int) (fontSize * 1.4f));
                textView.setMinimumWidth((int) (fontSize * 1.4f));
                row.addView(textView);
            }
        }
    }

    @Override
    public void onClick(@NonNull View view) {
        Intent intent;
        if (view.getId() == R.id.play) {
            intent = new Intent(this, SubLevelActivity.class);
        } else {
            intent = new Intent(this, SettingsActivity.class);
        }
        startActivity(intent);
    }
}