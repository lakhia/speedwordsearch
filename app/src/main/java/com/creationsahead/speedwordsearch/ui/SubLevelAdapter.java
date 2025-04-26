package com.creationsahead.speedwordsearch.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.creationsahead.speedwordsearch.R;
import com.creationsahead.speedwordsearch.mod.SubLevel;
import com.creationsahead.speedwordsearch.utils.Utils;
import java.util.List;

/**
 * Displays all the levels
 */
public class SubLevelAdapter extends ArrayAdapter<SubLevel> {

    private final int color;

    public SubLevelAdapter(@NonNull Context context, int resource, @NonNull List<SubLevel> levels) {
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

        SubLevel subLevel = getItem(position);
        if (subLevel == null) {
            return convertView;
        }
        Button button = convertView.findViewById(R.id.toggleButton);
        button.setText(subLevel.name);

        TextView textView = convertView.findViewById(R.id.time);
        textView.setText(Utils.formatTime(subLevel.timeUsed));

        SmartRatingBar bar = convertView.findViewById(R.id.ratingBar);
        bar.setRatingNum(subLevel.stars);
        BitmapDrawable stars = (BitmapDrawable) bar.getRatingDrawable();
        stars.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        stars = (BitmapDrawable) bar.getRatingBackgroundDrawable();
        stars.setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_IN);

        return convertView;
    }
}