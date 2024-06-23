package creationsahead.speedwordsearch.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import androidx.annotation.NonNull;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.io.IOException;
import java.util.HashMap;
import creationsahead.speedwordsearch.Event;
import creationsahead.speedwordsearch.Guess;

public class SoundManager {
    public enum SOUND_TYPE {
        FAIL,
        BONUS,
        LEVEL
    }
    private final HashMap<SOUND_TYPE, MediaPlayer> dict = new HashMap<>();

    public SoundManager(Context context) {
        dict.put(SOUND_TYPE.FAIL, load(context, "error-2-126514.mp3"));
        dict.put(SOUND_TYPE.BONUS, load(context, "mixkit-bonus-earned-in-video-game-2058.wav"));
        dict.put(SOUND_TYPE.LEVEL, load(context, "mixkit-completion-of-a-level-2063.wav"));
    }

    private MediaPlayer load(Context context, String asset) {
        MediaPlayer mp = new MediaPlayer();
        try {
            AssetFileDescriptor afd = context.getAssets().openFd(asset);
            mp.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.prepareAsync();
        return mp;
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onSoundEvents(@NonNull Guess guess) {
        if (guess.success) {
            if (guess.last) {
                play(SOUND_TYPE.LEVEL);
            } else {
                play(SOUND_TYPE.BONUS);
            }
        } else {
            play(SOUND_TYPE.FAIL);
        }
    }

    public void play(SOUND_TYPE type) {
        MediaPlayer mediaPlayer = dict.get(type);
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.seekTo(0);
            } else {
                mediaPlayer.start();
            }
        }
    }

    public void resume() {
        EventBus.getDefault().register(this);
    }

    public void pause() {
        EventBus.getDefault().unregister(this);
    }
}
