package Runtime;

import Compilation.*;

import java.util.Base64;

import static Compilation.CompType.*;

public class ArithmeticExpression {
    public static Variable executeExpression(NodeEXP node, Memory memory){
        CompType type = node.getExpType();
        if(type == BIOP){

            NodeEXP left = (NodeEXP) node.getChildren().get(0);
            NodeEXP right = (NodeEXP) node.getChildren().get(1);

            return Decoder.BIOP_Functions.get(node.getData()[0]).apply(executeExpression(left, memory),executeExpression(right, memory));
        }

        String val = node.getData()[0];
        
        if(type == VAR)
            val = memory.getVariable(node.getData()[0]).value.toString();

        try {
            Long a = Long.parseLong(val);
            return new Variable<>(a);
        } catch (NumberFormatException e) {
            System.out.println(val);
            return new Variable<>(Double.parseDouble(val));
        }

    }
}
