package Compilation.SyntaxTree;

import Compilation.DataTypes.Variable;
import Exceptions.CompilationError;

public class NodeIF extends TreeNode{

    private final NodeEXP statement;

    public NodeIF(NodeEXP statement, TreeNode parentNode) {
        super(parentNode);
        this.statement = statement;
    }

    @Override
    public Boolean evaluate() throws CompilationError {
        if(!Variable.strToBool(statement.evaluate().getValue().toString()))
            return false;

        getChildren().get(0).execute();
        return true;
    }

    @Override
    public boolean isScopeShortenable(){
        return true;
    }

    @Override
    public boolean isScopeStatement(){
        return true;
    }
}
