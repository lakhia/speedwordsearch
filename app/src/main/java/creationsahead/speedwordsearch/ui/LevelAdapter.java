package creationsahead.speedwordsearch.ui;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import creationsahead.speedwordsearch.Event;
import creationsahead.speedwordsearch.mod.Level;
import creationsahead.speedwordsearch.R;
import org.greenrobot.eventbus.EventBus;

/**
 * Displays all the levels
 */
public class LevelAdapter extends ArrayAdapter<Level> implements View.OnClickListener {
    public LevelAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    /// Inflates a checkbox with text
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.single_level, null);
            convertView.setOnClickListener(this);
        }

        Level level = getItem(position);
        Button button = convertView.findViewById(R.id.toggleButton);
        button.setText(level.name);
        RatingBar bar = convertView.findViewById(R.id.ratingBar);
        bar.setNumStars(level.stars);
        convertView.setTag(level);
        return convertView;
    }

    @Override
    public void onClick(View view) {
        Event event = Event.LEVEL_STARTED;
        event.levelNumber = ((Level)view.getTag()).number;
        EventBus.getDefault().post(event);

        Context context = getContext();
        Intent intent = new Intent(context, GameActivity.class);
        context.startActivity(intent);
    }
}