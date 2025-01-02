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
    public Variable<?> evaluate() throws CompilationError {
        CompType type = expType;
        if(Decoder.OP_Types.containsValue(type)){

            NodeEXP left = (NodeEXP) getChildren().get(0);
            NodeEXP right = (NodeEXP) getChildren().get(1);

            Variable<?> evalR = right.evaluate();

            if(evalR.value.toString().equals("0") || evalR.value.toString().equals("0.0"))
            {
                if(expType == MOD)
                    throw new RuntimeError(204);
                else if(expType == DIV)
                    throw new RuntimeError(203);
            }

            Variable<?> evalL = left.evaluate();

            return Decoder.BIOP_Functions.get(type).apply(evalL,evalR);
        }

        if(type != VAR && type != LIT){
            Variable<?> current = getScope().getVariable(value);
            Variable<?> oneVal = new Variable<>("1",INT);
            switch (type){
                case PREDEC -> {
                    Variable<?> val = current.sub(oneVal);
                    getScope().setVariable(value, val.value.toString());
                    return val;
                }
                case PREINC -> {
                    Variable<?> val = current.add(oneVal);
                    getScope().setVariable(value, val.value.toString());
                    return val;
                }
                case POSDEC -> {
                    Variable<?> val = new Variable<>(current.value.toString(), current.getType());
                    getScope().setVariable(value, current.sub(oneVal).value.toString());
                    return val;
                }
                case POSINC -> {
                    Variable<?> val = new Variable<>(current.value.toString(), current.getType());
                    getScope().setVariable(value, current.add(oneVal).value.toString());
                    return val;
                }
            }
        }

        String val = getValue();

        if(type == VAR){
            Variable<?> var = getScope().getVariable(value);
            return new Variable<>(var.value.toString(), var.getType());
        }

        if(val.charAt(0) == '"')
            return new Variable<>(val.substring(1,val.length()-1), STRING);

        long longValue = Variable.strToLong(val);

        if(String.valueOf(longValue).equals(val) || Variable.longKeys.containsKey(val))
            return new Variable<>(Variable.strToLong(val), LONG);

        return new Variable<>(Variable.strToDouble(val), DOUBLE);
    }
}
