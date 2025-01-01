package Compilation;

import Exceptions.CompilationError;

import java.util.*;
import java.util.concurrent.CompletionException;

import static Compilation.CompType.*;

public class Memory {
    private final Set<String> variables = new HashSet<>();
    private final Scope owner;

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

    private final Map<String,Functions> functions = new HashMap<>();

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

    public Variable<?> getVariable(String varName) {
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

        return new Variable<>("\"" + strings.get(varName).value + "\"");
    }

    public void setVariable(String varName, String value) {
        if(bools.containsKey(varName))
            bools.put(varName, new Variable<>(Variable.strToBool(value)));
        else if(chars.containsKey(varName))
            chars.put(varName, new Variable<>((char)Variable.strToLong(value)));
        else if(bytes.containsKey(varName))
            bytes.put(varName, new Variable<>((byte)Variable.strToLong(value)));
        else if(shorts.containsKey(varName))
            shorts.put(varName, new Variable<>((short)Variable.strToLong(value)));
        else if(ints.containsKey(varName))
            ints.put(varName, new Variable<>((int)Variable.strToLong(value)));
        else if(longs.containsKey(varName))
            longs.put(varName, new Variable<>(Variable.strToLong(value)));
        else if(floats.containsKey(varName))
            floats.put(varName, new Variable<>((float)Variable.strToDouble(value)));
        else if(doubles.containsKey(varName))
            doubles.put(varName, new Variable<>(Variable.strToDouble(value)));
        else
            strings.put(varName, new Variable<>(value));
    }

    private CompType getVarType(String varName){
        if(bools.containsKey(varName))
            return BOOL;
        else if(chars.containsKey(varName))
            return CHAR;
        else if(bytes.containsKey(varName))
            return BYTE;
        else if(shorts.containsKey(varName))
            return SHORT;
        else if(ints.containsKey(varName))
            return INT;
        else if(longs.containsKey(varName))
            return LONG;
        else if(floats.containsKey(varName))
            return FLOAT;
        else if(doubles.containsKey(varName))
            return DOUBLE;
        return STRING;
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
