package Compilation.SyntaxTree;

import Compilation.CompType;
import Compilation.Scope;
import Compilation.Variable;
import Exceptions.CompilationError;

public class NodeDECL extends TreeNode {
    CompType varType;
    String[] varNames;

    public NodeDECL(CompType varType, String[] varNames, Scope scope) {
        super(scope);
        this.varNames = varNames;
        this.varType = varType;
    }

    @Override
    public void execute() throws CompilationError {
        for(String varName : varNames){
            getScopeMemory().declareVariable(varName, Variable.getDefaultValue(varType));
        }
    }
}
