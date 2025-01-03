package Compilation.DataTypes;

import Compilation.CompType;

import static Compilation.CompType.INT;

public class MultiDimArray<T> extends Variable<T>{
    private final int[] dimensions;
    private final T[] array;

    public MultiDimArray(CompType type, int... dimensions) {
        super(INT);
        this.dimensions = dimensions;
        int multiply = 1;
        for(int dimension : dimensions)
            multiply *= dimension;

        array = (T[]) new Object[multiply];
    }

    private int toIndex(int... indices) {
        int index = 0;
        for(int i = 0; i < indices.length; i++)
            index += dimensions[i] * indices[i];
        return index;
    }

    public void setValue(T value, int... dimensions) {
        array[toIndex(dimensions)] = value;
    }

    public T getValue(int... dimensions) {
        return array[toIndex(dimensions)];
    }
}
