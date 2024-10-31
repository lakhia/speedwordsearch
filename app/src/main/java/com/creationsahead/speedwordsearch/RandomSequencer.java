package com.creationsahead.speedwordsearch;

import androidx.annotation.NonNull;
import java.util.Random;

/**
 * A class that allows permutations of letters, in a pseudo
 * random manner
 */
public class RandomSequencer {
    @NonNull private final Character[] letterCombinations = new Character[] {
        'H', 'F', 'G', 'B', 'A', 'S', 'U', 'O', 'T', 'R', 'L', 'M', 'E', 'K', 'N', 'I', 'P', 'D', 'C'
    };
    @NonNull private final Direction[] directionCombinations = new Direction[] {
        Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST,
        Direction.NORTH_EAST, Direction.NORTH_WEST, Direction.SOUTH_WEST,
        Direction.SOUTH_EAST
    };
    @NonNull private final Integer[] coordinateXCombinations;
    @NonNull private final Integer[] coordinateYCombinations;

    @NonNull private final Random letterRandomGen;
    @NonNull private final Random coordinateXRandomGen;
    @NonNull private final Random coordinateYRandomGen;
    @NonNull private final Random directionRandomGen;
    @NonNull private final Random eventRandomGen;

    public RandomSequencer(@NonNull Config config, int seed) {
        // Initialize all the random vars
        letterRandomGen = new Random(seed / 3);
        directionRandomGen = new Random(seed / 5);
        coordinateXRandomGen = new Random(seed / 7);
        coordinateYRandomGen = new Random(seed / 11);
        eventRandomGen = new Random(seed / 13);

        // Initialize all the arrays
        coordinateXCombinations = new Integer[config.sizeX];
        coordinateYCombinations = new Integer[config.sizeY];
        for (int i=0; i<config.sizeX; i++) {
            coordinateXCombinations[i] = i;
        }
        for (int i=0; i<config.sizeY; i++) {
            coordinateYCombinations[i] = i;
        }
    }

    @NonNull
    public SequenceIterator<Character> getLetterSequence() {
        return new SequenceIterator<>(letterCombinations, letterRandomGen);
    }

    @NonNull
    public SequenceIterator<Integer> getXCoordinateSequence() {
        return new SequenceIterator<>(coordinateXCombinations, coordinateXRandomGen);
    }

    @NonNull
    public SequenceIterator<Integer>  getYCoordinateSequence() {
        return new SequenceIterator<>(coordinateYCombinations, coordinateYRandomGen);
    }

    @NonNull
    public SequenceIterator<Direction> getDirectionSequence() {
        return new SequenceIterator<>(directionCombinations, directionRandomGen);
    }

    @NonNull
    public SequenceIterator<Integer> getEventSequence(int freq, int count) {
        int eventPerTick = freq / count;
        int leftOverEvents = freq % count;
        Integer[] events = new Integer[count];
        for (int i=0; i<leftOverEvents; i++) {
            events[i] = eventPerTick + 1;
        }
        for (int i=leftOverEvents; i<count; i++) {
            events[i] = eventPerTick;
        }
        return new SequenceIterator<>(events, eventRandomGen);
    }
}
