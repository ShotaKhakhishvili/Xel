package Compilation;

import Exceptions.CompilationError;

import java.util.*;
import java.util.concurrent.CompletionException;

public class Memory {
    private Set<String> variables = new HashSet<>();
    private Scope owner;

    public Memory(Scope owner){
        this.owner = owner;
    }

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

    public void declareVariable(String varName, Object varValue) throws CompilationError {
        if(owner.containsVariable(varName))
            throw new CompilationError(1);//CODE1
        if(varValue instanceof  Boolean)
            bools.put(varName, new Variable<>((Boolean) varValue));
        if(varValue instanceof  Character)
            chars.put(varName, new Variable<>((Character) varValue));
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

    public Variable getVariable(String varName) {
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

    public void setVariable(String varName, String value) {
        long longValue;
        double doubleValue = Double.parseDouble(value);
        if(value.contains("."))
            longValue = Long.parseLong(String.valueOf((long)Double.parseDouble(value)));
        else
            longValue = Long.parseLong(value);
        if(bools.containsKey(varName))
            bools.put(varName, new Variable<>(longValue != 0));
        if(chars.containsKey(varName))
            chars.put(varName, new Variable<>(value.charAt(0)));
        if(bytes.containsKey(varName))
            bytes.put(varName, new Variable<>((byte)longValue));
        if(shorts.containsKey(varName))
            shorts.put(varName, new Variable<>((short)longValue));
        if(ints.containsKey(varName))
            ints.put(varName, new Variable<>((int)longValue));
        if(longs.containsKey(varName))
            longs.put(varName, new Variable<>(longValue));
        if(floats.containsKey(varName))
            floats.put(varName, new Variable<>((float)doubleValue));
        if(doubles.containsKey(varName))
            doubles.put(varName, new Variable<>(doubleValue));
        if(strings.containsKey(varName))
            strings.put(varName, new Variable<>(value));
    }

    public Map<String, Functions> getFunctions() {
        return functions;
    }

    public Set<String> getVariables() {
        return variables;
    }

    public void delete(){
        bools.clear();
        chars.clear();
        bytes.clear();
        shorts.clear();
        ints.clear();
        longs.clear();
        floats.clear();
        doubles.clear();
        strings.clear();
        variables.clear();
    }
}
