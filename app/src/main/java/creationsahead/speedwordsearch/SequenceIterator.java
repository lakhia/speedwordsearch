package creationsahead.speedwordsearch;

import android.support.annotation.NonNull;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple iterator class
 */
public class SequenceIterator<T> implements Iterator<T> {
    @NonNull private T[] array;
    @NonNull private final Random random;
    private int index;

    public SequenceIterator(@NonNull T[] array, @NonNull Random random) {
        super();
        this.array = array;
        this.random = random;
        shuffle();
        this.array = Arrays.copyOf(array, array.length);
    }

    @Override
    public boolean hasNext() {
        return index < array.length;
    }

    @Override
    public T next() {
        return array[index++];
    }

    @Override
    public void remove() {}

    public void shuffle() {
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            T temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
        index = 0;
    }
}
