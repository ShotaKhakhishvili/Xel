package Compilation.SyntaxTree;

import Compilation.CompType;
import Compilation.DataTypes.MultiDimArray;
import Compilation.DataTypes.Variable;
import Exceptions.CompilationError;
import Compilation.CompType.*;

import static Compilation.CompType.AARRACC;

public class NodeARRACC extends NodeEXP {
    public NodeARRACC(String value,TreeNode parentNode){
        super(value,AARRACC,parentNode);
    }



    @Override
    public Variable<?> evaluate() throws CompilationError {
        int[] dimValues = new int[getChildren().size()];
        for(int i = 0; i < getChildren().size(); i++) {
            dimValues[i] = (int) Variable.strToLong(((Variable<?>) getChildren().get(i).evaluate()).getValue().toString());
        }

        return new Variable<>(((MultiDimArray<?>)getScope().getVariable(getValue())).getValue(dimValues),
                                (getScope().getVariable(getValue())).getType());
    }
}
