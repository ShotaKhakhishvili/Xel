package Compilation;

import Compilation.DataTypes.Variable;
import Exceptions.CompilationError;

public class Scope {

    private Scope parentScope;
    private final Memory memory = new Memory(this);

    public Scope(Scope parentScope){
        this.parentScope = parentScope;
    }

    public Scope(){

    }

    public boolean containsVariable(String varName){
        Scope scope = this;
        while(!scope.memory.containsVariable(varName)){
            if(scope.parentScope == null)
                return false;
            scope = scope.parentScope;
        }
        return true;
    }

    public void setVariable(String varName, Object value) throws CompilationError {
        getOwnerMemory(varName).setVariable(varName,value);
    }

    public Variable<?> getVariable(String varName) throws CompilationError {
        return getOwnerMemory(varName).getVariable(varName);
    }

    public Memory getOwnerMemory(String varName) throws CompilationError {
        if(!containsVariable(varName))
            throw new CompilationError(10);

        Scope scope = this;
        while(!scope.getMemory().getVariables().containsKey(varName))
            scope = scope.parentScope;


        return scope.getMemory();
    }

    public Memory getMemory() {
        return memory;
    }
}
