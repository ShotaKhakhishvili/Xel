package Compilation.SyntaxTree;

import Compilation.CompType;
import Compilation.Variable;
import Exceptions.CompilationError;

public class NodeDECL extends TreeNode {
    CompType varType;
    String[] varNames;

    public NodeDECL(CompType varType, String[] varNames, TreeNode parentNode) {
        super(parentNode);
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
