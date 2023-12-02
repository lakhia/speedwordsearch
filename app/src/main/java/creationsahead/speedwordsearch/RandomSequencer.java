package creationsahead.speedwordsearch;

import android.support.annotation.NonNull;
import java.util.Random;

import static creationsahead.speedwordsearch.ProgressTracker.MAX_DIFFICULTY;

/**
 * A class that allows permutations of letters, in a pseudo
 * random manner
 */
public class RandomSequencer implements Sequencer {
    private static final int BONUS_SIZE = 50;
    @NonNull private final int[] letterCombinations = new int[] {
        7, 5, 6, 1, 0, 18, 20, 14, 19, 17, 11, 12, 4, 10, 13, 8, 15, 3, 2
    };
    @NonNull private final int[] directionCombinations = new int[] {
        7, 2, 6, 1, 5, 3, 0, 4
    };
    @NonNull private final int[] positiveBonus = new int[BONUS_SIZE];
    @NonNull private final int[] negativeBonus = new int[BONUS_SIZE];
    @NonNull private final int[] coordinateXCombinations;
    @NonNull private final int[] coordinateYCombinations;

    @NonNull private final Random letterRandomGen;
    @NonNull private final Random coordinateXRandomGen;
    @NonNull private final Random coordinateYRandomGen;
    @NonNull private final Random directionRandomGen;
    @NonNull private final Random bonusRandomGen;
    @NonNull private final Config config;

    RandomSequencer(@NonNull Config config) {
        this.config = config;
        int seed = (int) System.currentTimeMillis();

        // Initialize all the random vars
        letterRandomGen = new Random(seed / 3);
        directionRandomGen = new Random(seed / 5);
        coordinateXRandomGen = new Random(seed / 7);
        coordinateYRandomGen = new Random(seed / 11);
        bonusRandomGen = new Random(seed/13);

        // Initialize all the arrays
        coordinateXCombinations = new int[config.sizeX];
        coordinateYCombinations = new int[config.sizeY];
        for (int i=0; i<config.sizeX; i++) {
            coordinateXCombinations[i] = i;
        }
        for (int i=0; i<config.sizeY; i++) {
            coordinateYCombinations[i] = i;
        }
        for (int i=0; i<BONUS_SIZE; i++) {
            positiveBonus[i] = bonusRandomGen.nextInt(MAX_DIFFICULTY);
            negativeBonus[i] = bonusRandomGen.nextInt(MAX_DIFFICULTY);
        }
    }

    private static void shuffle(@NonNull int[] array, @NonNull Random random) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    @NonNull
    @Override
    public int[] getLetterSequence() {
        shuffle(letterCombinations, letterRandomGen);
        return letterCombinations;
    }

    @NonNull
    @Override
    public int[] getXCoordinateSequence() {
        shuffle(coordinateXCombinations, coordinateXRandomGen);
        return coordinateXCombinations;
    }

    @NonNull
    @Override
    public int[] getYCoordinateSequence() {
        shuffle(coordinateYCombinations, coordinateYRandomGen);
        return coordinateYCombinations;
    }

    @NonNull
    @Override
    public int[] getDirectionSequence() {
        shuffle(directionCombinations, directionRandomGen);
        return directionCombinations;
    }

    @Override
    public boolean getPositiveBonus(int index) {
        if (index % BONUS_SIZE == 0) {
            shuffle(positiveBonus, bonusRandomGen);
        }
        return positiveBonus[index % BONUS_SIZE] > config.difficulty;
    }

    @Override
    public boolean getNegativeBonus(int index) {
        if (index % BONUS_SIZE == 0) {
            shuffle(negativeBonus, bonusRandomGen);
        }
        return negativeBonus[index % BONUS_SIZE] < config.difficulty;
    }
}
