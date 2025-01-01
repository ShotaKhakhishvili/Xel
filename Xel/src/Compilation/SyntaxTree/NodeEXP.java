package Compilation.SyntaxTree;

import Compilation.*;
import Exceptions.CompilationError;
import Exceptions.RuntimeError;

import static Compilation.CompType.*;

public class NodeEXP extends TreeNode {
    private final CompType expType;
    private final String value;

    public NodeEXP(String value, CompType expType, TreeNode parentNode) {
        super(parentNode);
        this.value = value;
        this.expType = expType;
    }
    public String getValue(){
        return value;
    }

    @Override
    public Variable evaluate() throws CompilationError {
        CompType type = expType;
        if(Decoder.OP_Types.containsValue(type)){

            NodeEXP left = (NodeEXP) getChildren().get(0);
            NodeEXP right = (NodeEXP) getChildren().get(1);

            Variable evalR = right.evaluate();

            if(evalR.value.toString().equals("0") || evalR.value.toString().equals("0.0"))
            {
                if(expType == MOD)
                    throw new RuntimeError(204);
                else if(expType == DIV)
                    throw new RuntimeError(203);
            }

            Variable evalL = left.evaluate();

            return Decoder.BIOP_Functions.get(type).apply(evalL,evalR);
        }

        String val = getValue();
        if(val.contains("\""))
            return new Variable<>(value.substring(1,val.length()-1));

        if(type == VAR)
            val = getScope().getVariable(value).value.toString();

        try {
            if(val.equals("true"))
                return new Variable<>(true);
            else if(val.equals("false"))
                return new Variable<>(false);
            else if(val.equals("jaybe"))
                return new Variable<>(Math.random() > 0.5);

            Long a = Long.parseLong(val);
            return new Variable<>(a);

        } catch (NumberFormatException e) {
            return new Variable<>(Double.parseDouble(val));
        }
    }
}
