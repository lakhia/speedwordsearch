package creationsahead.speedwordsearch;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import creationsahead.speedwordsearch.mod.Level;

public class LevelTracker {

    private static final int MAX_LEVEL = 10;
    private static StorageInterface storageInterface;
    @NonNull public final static ArrayList<Level> levels = new ArrayList<>();
    @NonNull private static String levelNamePrefix = "";

    public static void init(@NonNull StorageInterface storage, @NonNull String name) {
        storageInterface = storage;
        levelNamePrefix = name;
        for (int i = 0; i < MAX_LEVEL; i++) {
            Level level = storageInterface.getLevel(i, levelNamePrefix);
            if (level == null) {
                break;
            }
            levels.add(level);
        }
        createLevel();
     }

    private static void createLevel() {
        if (levels.size() == 0 || (levels.get(levels.size() - 1).won &&
                levels.size() < MAX_LEVEL)) {
            levels.add(new Level(levelNamePrefix, levels.size()));
        }
    }

    public static void store(Level level) {
        level.score();
        if (level.stars < 0) {
            return;
        }
        createLevel();
        storageInterface.storeLevel(level, levelNamePrefix);
    }
}
