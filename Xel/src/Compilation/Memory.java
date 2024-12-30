package Compilation;

import java.util.*;
import java.util.function.Supplier;

import static Compilation.CompType.*;

class Functions<T>{
    T value;
}

public class Memory {
    private Set<String> variables = new HashSet<>();

    private final Map<String,Variable<Boolean>> bools = new HashMap<>();
    private final Map<String,Variable<Character>> chars = new HashMap<>();
    private final Map<String,Variable<Byte>> bytes = new HashMap<>();
    private final Map<String,Variable<Short>> shorts = new HashMap<>();
    private final Map<String,Variable<Integer>> ints = new HashMap<>();
    private final Map<String,Variable<Long>> longs = new HashMap<>();
    private final Map<String,Variable<Float>> floats = new HashMap<>();
    private final Map<String,Variable<Double>> doubles = new HashMap<>();
    private final Map<String,Variable<String>> strings = new HashMap<>();

    private Map<String,Functions> functions = new HashMap<>();

    public void declareVariable(String varName, Object varValue){
        if(varValue instanceof  Boolean)
            bools.put(varName, new Variable<>((Boolean) varValue));
        if(varValue instanceof  Byte)
            bytes.put(varName, new Variable<>((Byte) varValue));
        if(varValue instanceof  Short)
            shorts.put(varName, new Variable<>((Short) varValue));
        if(varValue instanceof  Integer)
            ints.put(varName, new Variable<>((Integer) varValue));
        if(varValue instanceof  Long)
            longs.put(varName, new Variable<>((Long) varValue));
        if(varValue instanceof  Float)
            floats.put(varName, new Variable<>((Float) varValue));
        if(varValue instanceof  Double)
            doubles.put(varName, new Variable<>((Double) varValue));
        if(varValue instanceof  String)
            strings.put(varName, new Variable<>((String) varValue));
        variables.add(varName);
    }

    public Variable getVariable(String varName){
        if(!containsVariable(varName))
            return null;

        if(bools.containsKey(varName))
            return bools.get(varName);
        if(chars.containsKey(varName))
            return chars.get(varName);
        if(bytes.containsKey(varName))
            return bytes.get(varName);
        if(shorts.containsKey(varName))
            return shorts.get(varName);
        if(ints.containsKey(varName))
            return ints.get(varName);
        if(longs.containsKey(varName))
            return longs.get(varName);
        if(floats.containsKey(varName))
            return floats.get(varName);
        if(doubles.containsKey(varName))
            return doubles.get(varName);

        return strings.get(varName);
    }
    public boolean containsVariable(String varName){
        return variables.contains(varName);
    }

    public Map<String, Functions> getFunctions() {
        return functions;
    }
}
