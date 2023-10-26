package creationsahead.speedwordsearch;

import android.support.annotation.NonNull;
import java.util.Random;

/**
 * A class that allows permutations of letters, in a pseudo
 * random manner
 */
public class Sequencer {
    private static final int[][] letterCombinations = new int[][] {
        {7,5,6,1,0,18,20,14,19,17,11,12,4,10,13,8,15,3,2},
        {20,12,4,14,7,18,5,1,8,19,13,2,0,6,11,15,17,10,3},
        {14,8,2,4,11,0,17,6,10,20,18,5,15,3,12,13,7,19,1},
        {12,1,11,2,3,0,15,7,17,8,6,13,4,19,18,5,20,10,14},
        {5,7,12,3,11,6,15,0,2,18,4,1,20,19,10,8,17,14,13},
        {12,0,19,4,8,5,1,18,17,2,13,15,10,11,20,7,14,3,6},
        {7,14,10,11,0,4,1,18,20,5,19,8,6,15,13,17,3,12,2},
        {5,2,1,6,3,12,8,7,20,17,11,13,4,0,19,15,18,10,14},
        {4,3,2,14,0,12,10,11,20,5,13,1,19,15,17,6,7,8,18},
        {15,14,6,7,13,11,2,20,4,1,8,10,5,12,17,0,18,3,19},
        {15,12,11,0,6,20,2,13,3,5,8,14,18,17,10,4,7,1,19},
        {10,0,12,2,17,5,6,3,8,14,4,15,20,11,1,13,7,18,19},
        {15,2,12,8,10,13,4,6,11,18,14,3,17,1,5,19,20,0,7},
        {0,6,13,8,19,1,20,4,14,10,17,18,3,5,11,7,2,12,15},
        {4,6,5,20,3,15,14,7,12,19,1,13,11,17,2,10,0,8,18},
        {0,1,13,14,8,2,3,6,10,7,18,17,11,20,19,15,12,5,4},
        {11,18,5,7,8,2,6,10,12,19,15,14,13,3,0,1,4,17,20},
        {10,11,8,7,13,3,14,18,17,1,12,15,19,20,6,0,4,5,2},
    };
    private static final int[][] directionCombinations = new int[][] {
        {7,2,6,1,5,3,0,4},
        {5,2,7,1,6,0,4,3},
        {1,5,2,3,6,7,0,4},
        {7,2,5,6,4,0,3,1},
        {2,1,4,3,7,5,0,6},
        {3,7,0,6,1,2,5,4},
        {3,4,6,2,5,1,7,0},
        {6,5,2,7,0,4,3,1},
    };
    private int[][] coordinateCombinations;

    private Random letterRandomGen;
    private Random coordinateRandomGen;
    private Random directionRandomGen;

    public Sequencer(int seed) {
        letterRandomGen = new Random(seed);
        directionRandomGen = new Random(2+seed);
    }

    public Sequencer(int seed, int maxCoordinate) {
        this(seed);

        coordinateRandomGen = new Random(seed * maxCoordinate);
        coordinateCombinations = new int[maxCoordinate][maxCoordinate];
        for (int i=0; i<maxCoordinate; i++) {
            for (int j=0; j<maxCoordinate; j++) {
                coordinateCombinations[i][j] = j;
            }
            shuffle(coordinateCombinations[i]);
        }
    }

    private void shuffle(@NonNull int[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = coordinateRandomGen.nextInt(i + 1);
            int temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    public int[] getNextLetterSequence() {
        return letterCombinations[letterRandomGen.nextInt(letterCombinations.length)];
    }

    public int[] getNextCoordinateSequence() {
        return coordinateCombinations[coordinateRandomGen.nextInt(coordinateCombinations.length)];
    }

    public int[] getDirectionSequence() {
        return directionCombinations[directionRandomGen.nextInt(directionCombinations.length)];
    }
}
