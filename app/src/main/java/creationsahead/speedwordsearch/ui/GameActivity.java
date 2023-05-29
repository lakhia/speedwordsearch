package creationsahead.speedwordsearch.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import creationsahead.speedwordsearch.R;

/**
 * Primary activity for game play
 */
public class GameActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
    }
}
