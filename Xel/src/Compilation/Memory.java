package Compilation;

import Exceptions.CompilationError;

import java.util.*;
import java.util.concurrent.CompletionException;

import static Compilation.CompType.*;

public class Memory {
    private final Set<String> variables = new HashSet<>();
    private final Map<String,Variable<?>> vars = new HashMap<>();
    private final Scope owner;

    public Memory(Scope owner){
        this.owner = owner;
    }

    private final Map<String,Functions> functions = new HashMap<>();

    public void declareVariable(String varName, String varValue, CompType varType) throws CompilationError {
        if(owner.containsVariable(varName))
            throw new CompilationError(1);//CODE1

        vars.put(varName,new Variable<>(varValue, varType));

        setVariable(varName, varValue);
        variables.add(varName);
    }

    public Variable<?> getVariable(String varName) {
        return vars.get(varName);
    }

    public void setVariable(String varName, String value) {
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

    public void delete(){
        vars.clear();
        variables.clear();
    }
}
