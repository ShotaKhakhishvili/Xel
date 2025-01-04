package Compilation.SyntaxTree;

import Compilation.CompType;
import Compilation.DataTypes.Variable;
import Exceptions.CompilationError;

import java.util.List;
import java.util.function.BinaryOperator;

import static Compilation.CompType.*;
import static Compilation.Decoder.*;

public class NodeASGM extends TreeNode {
    private final CompType type;
    private final String varName;
    private final NodeEXP exp;
    private final NodeEXP[] dimensions;

    public NodeASGM(String varName, CompType asgmType, NodeEXP exp, List<NodeEXP> dimensions, TreeNode parentNode) {
        super(parentNode);
        this.varName = varName;
        this.type = asgmType;
        this.exp = exp;
        if(dimensions != null)
            this.dimensions = dimensions.toArray(new NodeEXP[0]);
        else
            this.dimensions = null;
    }

    @Override
    public void execute() throws CompilationError {
        if(type == ASGM) {
            if(dimensions == null)
                getScope().setVariable(varName, exp.evaluate().getValue());
            else {
                getScope().setVariable(varName, exp.evaluate().getValue(), getDimVals());
            }
        }
        else{
            BinaryOperator<Variable<?>> binaryOperator = BIOP_Functions.get(type);
            Variable<?> newValue = binaryOperator.apply(getScope().getVariable(varName), exp.evaluate());
            if(dimensions == null)
                getScope().setVariable(varName, newValue.getValue());
            else
                getScope().setVariable(varName, newValue.getValue(), getDimVals());
        }
    }

    private int[] getDimVals() throws CompilationError {
        int[] dimVals = new int[dimensions.length];
        for(int i = 0; i < dimensions.length; i++) {
            dimVals[i] = Integer.parseInt(dimensions[i].evaluate().getValue().toString());
        }
        return dimVals;
    }
}
