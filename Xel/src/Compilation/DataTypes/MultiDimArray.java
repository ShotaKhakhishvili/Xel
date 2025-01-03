package Compilation.DataTypes;

import Compilation.CompType;

import java.lang.reflect.Array;

import static Compilation.CompType.INT;

public class MultiDimArray<T> extends Variable<T>{
    private final int[] dimensions;
    private final T[] array;

    public MultiDimArray(CompType type, int... dimensions) {
        super(type);
        this.dimensions = dimensions;
        int multiply = 1;
        for(int dimension : dimensions)
            multiply *= dimension;

        switch (type){
            case BOOL -> array = (T[]) Array.newInstance(Boolean.class, multiply);
            case CHAR -> array = (T[]) Array.newInstance(Character.class, multiply);
            case BYTE -> array = (T[]) Array.newInstance(Byte.class, multiply);
            case SHORT -> array = (T[]) Array.newInstance(Short.class, multiply);
            case INT -> array = (T[]) Array.newInstance(Integer.class, multiply);
            case LONG -> array = (T[]) Array.newInstance(Long.class, multiply);
            case FLOAT -> array = (T[]) Array.newInstance(Float.class, multiply);
            case DOUBLE -> array = (T[]) Array.newInstance(Double.class, multiply);
            default -> array = (T[]) Array.newInstance(String.class, multiply);
        }
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

    public int[] getDimensions() {
        return dimensions;
    }
}
