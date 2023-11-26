package creationsahead.speedwordsearch;

import android.support.annotation.NonNull;
import java.util.Random;

/**
 * A class that allows permutations of letters, in a pseudo
 * random manner
 */
public class RandomSequencer implements Sequencer {
    private static final int[] letterCombinations = new int[] {
        7, 5, 6, 1, 0, 18, 20, 14, 19, 17, 11, 12, 4, 10, 13, 8, 15, 3, 2
    };
    private static final int[] directionCombinations = new int[] {
        7, 2, 6, 1, 5, 3, 0, 4
    };
    private int[] coordinateXCombinations;
    private int[] coordinateYCombinations;

    private Random letterRandomGen;
    private Random coordinateXRandomGen;
    private Random coordinateYRandomGen;
    private Random directionRandomGen;

    RandomSequencer(Config config) {
        int seed = (int) System.currentTimeMillis();
        letterRandomGen = new Random(seed / 3);
        directionRandomGen = new Random(seed / 5);
        coordinateXRandomGen = new Random(seed / 7);
        coordinateYRandomGen = new Random(seed / 11);
        coordinateXCombinations = new int[config.sizeX];
        coordinateYCombinations = new int[config.sizeY];
        for (int i=0; i<config.sizeX; i++) {
            coordinateXCombinations[i] = i;
        }
        for (int i=0; i<config.sizeY; i++) {
            coordinateYCombinations[i] = i;
        }
    }

    private static void shuffle(@NonNull int[] array, Random random) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    @Override
    public int[] getLetterSequence() {
        shuffle(letterCombinations, letterRandomGen);
        return letterCombinations;
    }

    @Override
    public int[] getXCoordinateSequence() {
        shuffle(coordinateXCombinations, coordinateXRandomGen);
        return coordinateXCombinations;
    }

    @Override
    public int[] getYCoordinateSequence() {
        shuffle(coordinateYCombinations, coordinateYRandomGen);
        return coordinateYCombinations;
    }

    @Override
    public int[] getDirectionSequence() {
        shuffle(directionCombinations, directionRandomGen);
        return directionCombinations;
    }
}
