package com.creationsahead.speedwordsearch.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import androidx.annotation.NonNull;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.io.IOException;
import java.util.HashMap;
import com.creationsahead.speedwordsearch.Guess;
import com.creationsahead.speedwordsearch.StorageInterface;

public class SoundManager {
    public enum SOUND_TYPE {
        FAIL,
        BONUS,
        LEVEL
    }
    private final MediaPlayer[] soundTracks = new MediaPlayer[3];
    private float sound_effects_volume = 0;
    private float music_volume = 0;
    private int current_music = 0;
    private final HashMap<SOUND_TYPE, MediaPlayer> dict = new HashMap<>();

    @NonNull
    private final static SoundManager instance = new SoundManager();

    private SoundManager() {
    }

    @NonNull
    public static SoundManager getInstance() {
        return instance;
    }

    public void init(@NonNull Context context) {
        dict.put(SOUND_TYPE.FAIL, load(context, "error-2-126514.mp3"));
        dict.put(SOUND_TYPE.BONUS, load(context, "mixkit-bonus-earned-in-video-game-2058.wav"));
        dict.put(SOUND_TYPE.LEVEL, load(context, "mixkit-completion-of-a-level-2063.wav"));
        soundTracks[0] = load(context, "mixkit-eyes-in-the-puddle-257.mp3");
        soundTracks[1] = load(context, "mixkit-infected-vibes-157.mp3");
        soundTracks[2] = load(context, "mixkit-deep-urban-623.mp3");
    }

    public void syncSettingsFromStorage(StorageInterface storageInterface) {
        float volume = storageInterface.getPreference("special_effects");
        sound_effects_volume = (float) (1.0f - Math.log(101 - volume) / Math.log(101));
        volume = storageInterface.getPreference("music");
        music_volume = (float) (1.0f - Math.log(125 - volume) / Math.log(125));
    }

    @NonNull
    private MediaPlayer load(@NonNull Context context, String asset) {
        MediaPlayer mp = new MediaPlayer();
        try {
            AssetFileDescriptor afd = context.getAssets().openFd(asset);
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.prepareAsync();
        return mp;
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onSoundEvents(@NonNull Guess guess) {
        if (guess.answer != null) {
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
            mediaPlayer.setVolume(sound_effects_volume, sound_effects_volume);
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.seekTo(0);
            } else {
                mediaPlayer.start();
            }
        }
    }

    public void resume() {
        EventBus.getDefault().register(this);
        play_soundTrack();
    }

    public void pause() {
        EventBus.getDefault().unregister(this);
        pause_soundTrack();
    }

    private void pause_soundTrack() {
        MediaPlayer mediaPlayer = soundTracks[current_music];
        mediaPlayer.pause();
    }

    private void play_soundTrack() {
        MediaPlayer mediaPlayer = soundTracks[current_music];
        mediaPlayer.setVolume(music_volume, music_volume);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(mediaPlayer1 -> {
            current_music = (current_music + 1) % 3;
            play_soundTrack();
        });
    }
}
