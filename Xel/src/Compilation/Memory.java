package Compilation;

import Compilation.DataTypes.Variable;
import Compilation.DataTypes.MultiDimArray;
import Compilation.SyntaxTree.NodeEXP;
import Exceptions.CompilationError;

import java.lang.reflect.Array;
import java.util.*;

import static Compilation.CompType.ARR;

public class Memory {
    private final Map<String, Variable<?>> vars = new HashMap<>();
    private final Scope owner;

    public Memory(Scope owner){
        this.owner = owner;
    }

    private final Map<String,Functions> functions = new HashMap<>();

    public <T> void declareVariable(String varName, T varValue, CompType varType) {
        vars.put(varName,new Variable<>(varValue, varType));
        setVariable(varName, varValue);
    }

    public void declareArray(String varName, CompType varType, int[] dimensions){
        int multiply = 1;
        for(int dimension : dimensions)
            multiply *= dimension;

        switch (varType){
            case BOOL -> vars.put(varName,new MultiDimArray<>((Boolean[]) Array.newInstance(Boolean.class, multiply), varType, dimensions));
            case CHAR -> vars.put(varName,new MultiDimArray<>((Character[]) Array.newInstance(Character.class, multiply), varType, dimensions));
            case BYTE -> vars.put(varName,new MultiDimArray<>((Byte[]) Array.newInstance(Byte.class, multiply), varType, dimensions));
            case SHORT -> vars.put(varName,new MultiDimArray<>((Short[]) Array.newInstance(Short.class, multiply), varType, dimensions));
            case INT -> vars.put(varName,new MultiDimArray<>((Integer[]) Array.newInstance(Integer.class, multiply), varType, dimensions));
            case LONG -> vars.put(varName,new MultiDimArray<>((Long[]) Array.newInstance(Long.class, multiply), varType, dimensions));
            case FLOAT -> vars.put(varName,new MultiDimArray<>((Float[]) Array.newInstance(Float.class, multiply), varType, dimensions));
            case DOUBLE -> vars.put(varName,new MultiDimArray<>((Double[]) Array.newInstance(Double.class, multiply), varType, dimensions));
            default -> vars.put(varName,new MultiDimArray<>((String[]) Array.newInstance(String.class, multiply), varType, dimensions));
        }
    }

    public Variable<?> getVariable(String varName) {
        return vars.get(varName);
    }

    public void setVariable(String varName, Object value) {
        vars.get(varName).setValue(value);
    }
    public void setVariable(String varName, Object value, int[] dimensions) {
        ((MultiDimArray<?>)vars.get(varName)).setValue(value, dimensions);
    }

    private CompType getVarType(String varName){
        return vars.get(varName).getType();
    }

    public Map<String, Functions> getFunctions() {
        return functions;
    }

    public Map<String,Variable<?>> getVariables() {
        return vars;
    }

    public boolean containsVariable(String varName){
        return vars.containsKey(varName);
    }

    public void delete(){
        vars.clear();
    }
}
