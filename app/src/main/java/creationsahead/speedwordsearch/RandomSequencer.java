package creationsahead.speedwordsearch;

import android.support.annotation.NonNull;
import java.util.Random;

import static creationsahead.speedwordsearch.ProgressTracker.MAX_DIFFICULTY;

/**
 * A class that allows permutations of letters, in a pseudo
 * random manner
 */
public class RandomSequencer implements Sequencer {
    @NonNull private final int[] letterCombinations = new int[] {
        7, 5, 6, 1, 0, 18, 20, 14, 19, 17, 11, 12, 4, 10, 13, 8, 15, 3, 2
    };
    @NonNull private final int[] directionCombinations = new int[] {
        7, 2, 6, 1, 5, 3, 0, 4
    };
    @NonNull private final int[] coordinateXCombinations;
    @NonNull private final int[] coordinateYCombinations;

    @NonNull private final Random letterRandomGen;
    @NonNull private final Random coordinateXRandomGen;
    @NonNull private final Random coordinateYRandomGen;
    @NonNull private final Random directionRandomGen;
    @NonNull private final Random bonusRandomGen;
    @NonNull private final Config config;
    @NonNull private final Random miscRandomGen;

    RandomSequencer(@NonNull Config config) {
        this.config = config;
        int seed = (int) System.currentTimeMillis();

        // Initialize all the random vars
        letterRandomGen = new Random(seed / 3);
        directionRandomGen = new Random(seed / 5);
        coordinateXRandomGen = new Random(seed / 7);
        coordinateYRandomGen = new Random(seed / 11);
        bonusRandomGen = new Random(seed/13);
        miscRandomGen = new Random(seed/17);

        // Initialize all the arrays
        coordinateXCombinations = new int[config.sizeX];
        coordinateYCombinations = new int[config.sizeY];
        for (int i=0; i<config.sizeX; i++) {
            coordinateXCombinations[i] = i;
        }
        for (int i=0; i<config.sizeY; i++) {
            coordinateYCombinations[i] = i;
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
    public int getMisc(int max) {
        return miscRandomGen.nextInt(max);
    }

    @Override
    public int getBonus(int index) {
        for (int i = 0; i < 5; i++) {
            double val = bonusRandomGen.nextGaussian() * 5;
            int bonus = (int) (val + (config.difficulty * 9.0 / 50.0));
            if (bonus >= -2 && bonus <= 22) {
                return bonus;
            }
        }
        return -1;
    }
}
