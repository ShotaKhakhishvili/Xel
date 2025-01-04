package Compilation.SyntaxTree;

import Compilation.CompType;
import Compilation.DataTypes.MultiDimArray;
import Compilation.DataTypes.Variable;
import Exceptions.CompilationError;

import java.security.KeyPair;
import java.util.Arrays;
import java.util.List;

public class NodeDECL extends TreeNode {
    protected final CompType varType;
    protected final NodeEXP[] varInits;
    private final List<NodeEXP>[] dimensions;
    protected String[] varNames;

    public NodeDECL(CompType varType, NodeEXP[] varInits, String[] varNames,List<NodeEXP>[] dimensions, TreeNode parentNode) {
        super(parentNode);
        this.varInits = varInits;
        this.varType = varType;
        this.varNames = varNames;
        this.dimensions = dimensions;
    }
    public void setVarNames(String[] varNames) {
        this.varNames = varNames;
    }

    @Override
    public void execute() throws CompilationError {
        for(int i = 0; i < varInits.length; i++){
            if(dimensions[i] == null)
                getScopeMemory().declareVariable(varNames[i], varInits[i].evaluate().getValue(), varType);
            else {
                int[] calculatedDimensions = new int[dimensions[i].size()];
                for(int j = 0; j < dimensions[i].size(); j++){
                    calculatedDimensions[j] = Integer.parseInt(String.valueOf(dimensions[i].get(j).evaluate().getValue()));
                }
                getScopeMemory().declareArray(varNames[i], varType, calculatedDimensions);
            }
        }
    }
}
