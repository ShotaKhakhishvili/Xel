package Compilation.SyntaxTree;

import Compilation.CompType;
import Exceptions.CompilationError;

public class NodeDECL extends TreeNode {
    private final CompType varType;
    private final NodeEXP[] varInits;
    private final String[] varNames;

    public NodeDECL(CompType varType, NodeEXP[] varInits, String[] varNames, TreeNode parentNode) {
        super(parentNode);
        this.varInits = varInits;
        this.varType = varType;
        this.varNames = varNames;
    }

    @Override
    public void execute() throws CompilationError {
        for(int i = 0; i < varInits.length; i++){
            getScopeMemory().declareVariable(varNames[i], varInits[i].evaluate().getValue(), varType);
        }
    }
}
