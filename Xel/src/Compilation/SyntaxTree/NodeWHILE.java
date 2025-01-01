package Compilation.SyntaxTree;

import Compilation.Variable;
import Exceptions.CompilationError;

public class NodeWHILE extends TreeNode{

    private final NodeEXP statement;

    public NodeWHILE(NodeEXP statement, TreeNode parentNode) {
        super(parentNode);
        this.statement = statement;
    }

    @Override
    public void execute() throws CompilationError {
        while(Variable.strToBool(statement.evaluate().value.toString()) && !exit)
            getChildren().get(0).execute();
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
