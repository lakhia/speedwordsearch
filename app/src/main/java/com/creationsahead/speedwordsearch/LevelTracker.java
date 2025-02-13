package com.creationsahead.speedwordsearch;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import com.creationsahead.speedwordsearch.mod.Level;
import com.creationsahead.speedwordsearch.mod.SubLevel;

public class LevelTracker {

    private static final String[] LEVEL_NAMES = new String[]{
            "Beginner I", "Beginner II", "Beginner III",
            "Intermediate I", "Intermediate II", "Intermediate III",
            "Advanced I", "Advanced II", "Advanced III",
            "Expert I", "Expert II", "Expert III"};
    public static final int MAX_LEVEL = 10;
    public static final int MAX_SUB_LEVEL = LEVEL_NAMES.length;

    private static StorageInterface storageInterface;
    @NonNull public final static ArrayList<SubLevel> subLevels = new ArrayList<>();

    public static void init(@NonNull StorageInterface storage) {
        storageInterface = storage;
        for (int i = 0; i < MAX_SUB_LEVEL; i++) {
            SubLevel subLevel = storageInterface.getSubLevel(LEVEL_NAMES[i]);
            if (subLevel == null) {
                break;
            }
            subLevels.add(subLevel);
        }
        createSubLevel();
     }


    public static void createLevel(SubLevel subLevel) {
        ArrayList<Level> levels = subLevel.levels;
        if (levels.isEmpty() || (levels.get(levels.size() - 1).won)) {
            if (levels.size() < MAX_LEVEL) {
                subLevel.addLevel(levels.size());
            } else if (subLevels.size() < MAX_SUB_LEVEL) {
                createSubLevel();
            }
        }
    }

    private static void createSubLevel() {
        if (subLevels.isEmpty() || (subLevels.get(subLevels.size() - 1).won &&
                subLevels.size() < MAX_SUB_LEVEL)) {
            subLevels.add(new SubLevel(LEVEL_NAMES[subLevels.size()], subLevels.size(), 1));
        }
    }

    public static void store(SubLevel subLevel) {
        storageInterface.storeSubLevel(subLevel);
    }

    public static boolean loaded() {
        return subLevels.isEmpty() || subLevels.get(0).levels.isEmpty();
    }
}
