package Compilation.SyntaxTree;

import Compilation.*;

import static Compilation.CompType.VAR;

public class NodeEXP extends TreeNode {
    CompType expType;
    String value;

    public NodeEXP(String value, CompType expType, Scope scope) {
        super(scope);
        this.value = value;
        this.expType = expType;
    }
    public CompType getExpType(){
        return expType;
    }
    public String getValue(){
        return value;
    }

    @Override
    public Variable evaluate(){
        CompType type = expType;
        if(Decoder.BIOP_Types.containsValue(type)){

            NodeEXP left = (NodeEXP) getChildren().get(0);
            NodeEXP right = (NodeEXP) getChildren().get(1);

            return Decoder.BIOP_Functions.get(type).apply(left.evaluate(),right.evaluate());
        }

        String val = getValue();

        if(type == VAR)
            val = getScopeMemory().getVariable(value).value.toString();

        try {
            Long a = Long.parseLong(val);
            return new Variable<>(a);
        } catch (NumberFormatException e) {
            return new Variable<>(Double.parseDouble(val));
        }
    }
}
