package creationsahead.speedwordsearch.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import creationsahead.speedwordsearch.R;
import creationsahead.speedwordsearch.mod.Level;
import creationsahead.speedwordsearch.utils.Utils;

/**
 * Displays all the levels
 */
public class LevelAdapter extends ArrayAdapter<Level>  {

    public LevelAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    @Override
    public void addAll(Level... items) {
        for (Level level: items) {
            if (level != null) {
                super.add(level);
            }
        }
    }

    /// Inflates a checkbox with text
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.single_level, null);
        }

        Level level = getItem(position);

        Button button = convertView.findViewById(R.id.toggleButton);
        button.setText(level.name);

        TextView textView = convertView.findViewById(R.id.time);
        textView.setText(Utils.formatTime(level.timeUsed));

        RatingBar bar = convertView.findViewById(R.id.ratingBar);
        bar.setRating(level.stars);
        LayerDrawable stars = (LayerDrawable) bar.getProgressDrawable();
        stars.getDrawable(0).setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_IN);
        return convertView;
    }
}