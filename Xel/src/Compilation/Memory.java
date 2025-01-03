package Compilation;

import Compilation.DataTypes.Variable;
import Compilation.DataTypes.MultiDimArray;
import Exceptions.CompilationError;

import java.util.*;

public class Memory {
    private final Map<String, Variable<?>> vars = new HashMap<>();
    private final Scope owner;

    public Memory(Scope owner){
        this.owner = owner;
    }

    private final Map<String,Functions> functions = new HashMap<>();

    public <T> void declareVariable(String varName, T varValue, CompType varType) throws CompilationError {
        vars.put(varName,new Variable<>(varValue, varType));
        setVariable(varName, varValue);
    }

    public Variable<?> getVariable(String varName) {
        return vars.get(varName);
    }

    public void setVariable(String varName, Object value) {
        vars.get(varName).setValue(value);
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
