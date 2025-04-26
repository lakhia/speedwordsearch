package com.creationsahead.speedwordsearch.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.util.List;
import com.creationsahead.speedwordsearch.R;
import com.creationsahead.speedwordsearch.mod.Level;
import com.creationsahead.speedwordsearch.utils.Utils;

/**
 * Displays all the levels
 */
public class LevelAdapter extends ArrayAdapter<Level> {

    private final int color;

    public LevelAdapter(@NonNull Context context, int resource, @NonNull List<Level> levels) {
        super(context, resource, levels);
        color = context.getResources().getColor(R.color.bright_yellow);
    }

    /// Inflates a level with text, buttons and stars
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.single_level, null);
        }

        Level level = getItem(position);
        if (level == null) {
            return convertView;
        }
        Button button = convertView.findViewById(R.id.toggleButton);
        button.setText(level.name);

        TextView textView = convertView.findViewById(R.id.time);
        textView.setText(Utils.formatTime(level.timeUsed));

        SmartRatingBar bar = convertView.findViewById(R.id.ratingBar);
        bar.setRatingNum(level.stars);
        BitmapDrawable stars = (BitmapDrawable) bar.getRatingDrawable();
        stars.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        stars = (BitmapDrawable) bar.getRatingBackgroundDrawable();
        stars.setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_IN);

        return convertView;
    }
}