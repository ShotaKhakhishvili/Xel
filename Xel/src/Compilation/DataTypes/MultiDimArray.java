package Compilation.DataTypes;

import Compilation.CompType;
import Exceptions.RuntimeError;

import java.lang.reflect.Array;
import java.util.Arrays;

import static Compilation.CompType.*;

public class MultiDimArray<T> extends Variable<T>{
    private final int[] dimensions;
    private final T[] array;

    public MultiDimArray(T[] array, CompType type, int[] dimensions) {
        super(type);
        this.dimensions = dimensions;
        this.array = array;
        for(int i = 0; i < array.length; i++)
            array[i] = (T)Variable.getDefaultValue(type);
    }

    private int toIndex(int[] indices) {
        int index = 0;
        int multiplier = 1;

        for (int i = dimensions.length - 1; i >= 0; i--) {
            index += indices[i] * multiplier;
            multiplier *= dimensions[i];
        }

        return index;
    }

    public void setValue(Object value, int[] dims) {
        array[toIndex(dims)] = valFromObject(value);
    }

    public T getValue(int[] dims) {
        return array[toIndex(dims)];
    }

    public int[] getDimensions() {
        return dimensions;
    }
}
