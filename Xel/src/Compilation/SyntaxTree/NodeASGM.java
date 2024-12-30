package Compilation.SyntaxTree;

import Compilation.CompType;
import Compilation.Scope;
import Compilation.Variable;

import java.util.function.BinaryOperator;

import static Compilation.CompType.*;
import static Compilation.Decoder.BIOP_Functions;

public class NodeASGM extends TreeNode {
    CompType type;
    String varName;
    NodeEXP exp;

    public NodeASGM(String varName, CompType asgmType, NodeEXP exp, Scope scope) {
        super(scope);
        this.varName = varName;
        this.type = asgmType;
        this.exp = exp;
    }

    @Override
    public void execute(){
        String val = String.valueOf(exp.evaluate().value);
        if(type == ASGM)
            getScopeMemory().setVariable(varName, val);
        else{
            BinaryOperator<Variable> binaryOperator = BIOP_Functions.get(type);
            Variable newValue = binaryOperator.apply(getScopeMemory().getVariable(varName), exp.evaluate());
            getScopeMemory().setVariable(varName, newValue.value.toString());
        }
    }

    public CompType getType(){
        return type;
    }

    public String getVarName() {
        return varName;
    }

    public NodeEXP getExp() {
        return exp;
    }
}
