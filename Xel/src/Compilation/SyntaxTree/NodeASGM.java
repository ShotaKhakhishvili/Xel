package Compilation.SyntaxTree;

import Compilation.CompType;
import Compilation.Variable;
import Exceptions.CompilationError;

import java.util.function.BinaryOperator;

import static Compilation.CompType.*;
import static Compilation.Decoder.*;

public class NodeASGM extends TreeNode {
    private final CompType type;
    private final String varName;
    private final NodeEXP exp;

    public NodeASGM(String varName, CompType asgmType, NodeEXP exp, TreeNode parentNode) {
        super(parentNode);
        this.varName = varName;
        this.type = asgmType;
        this.exp = exp;
    }

    @Override
    public void execute() throws CompilationError {
        String val = String.valueOf(exp.evaluate().value);
        if(type == ASGM)
            getScope().setVariable(varName, val);
        else{
            BinaryOperator<Variable<?>> binaryOperator = BIOP_Functions.get(type);
            Variable<?> newValue = binaryOperator.apply(getScope().getVariable(varName), exp.evaluate());
            getScope().setVariable(varName, newValue.value.toString());
        }
    }
}
