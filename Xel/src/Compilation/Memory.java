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

    public void declareVariable(String varName, Object varValue, CompType varType) throws CompilationError {
        if(owner.containsVariable(varName))
            throw new CompilationError(1);//CODE1
        switch (varType){
            case BOOL -> bools.put(varName, null);
            case CHAR -> chars.put(varName, null);
            case BYTE -> bytes.put(varName, null);
            case SHORT -> shorts.put(varName, null);
            case INT -> ints.put(varName, null);
            case LONG -> longs.put(varName, null);
            case FLOAT -> floats.put(varName, null);
            case DOUBLE -> doubles.put(varName, null);
            default -> strings.put(varName, null);
        }
        setVariable(varName, varValue.toString());
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
        double doubleValue = 0;

        if(value.equals("true") || value.equals("false")){
            longValue = value.equals("true") ? 1L : 0L;
            doubleValue = value.equals("true") ? 1.0 : 0.0;
        }else{
            if(value.contains(".") || value.contains("f") || value.contains("F"))
                longValue = Long.parseLong(String.valueOf((long)Double.parseDouble(value)));
            else
                longValue = Long.parseLong(value);
            doubleValue = Double.parseDouble(value);
        }

        if(bools.containsKey(varName))
            bools.put(varName, new Variable<>(longValue != 0));
        else if(chars.containsKey(varName))
            chars.put(varName, new Variable<>((char)longValue));
        else if(bytes.containsKey(varName))
            bytes.put(varName, new Variable<>((byte)longValue));
        else if(shorts.containsKey(varName))
            shorts.put(varName, new Variable<>((short)longValue));
        else if(ints.containsKey(varName))
            ints.put(varName, new Variable<>((int)longValue));
        else if(longs.containsKey(varName))
            longs.put(varName, new Variable<>(longValue));
        else if(floats.containsKey(varName))
            floats.put(varName, new Variable<>((float)doubleValue));
        else if(doubles.containsKey(varName))
            doubles.put(varName, new Variable<>(doubleValue));
        else
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
