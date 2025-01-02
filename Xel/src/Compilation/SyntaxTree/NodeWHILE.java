package Compilation.SyntaxTree;

import Compilation.Variable;
import Exceptions.CompilationError;

public class NodeWHILE extends NodeLOOP{

    public NodeWHILE(NodeEXP statement, TreeNode parentNode) {
        super(statement, parentNode);
    }

    @Override
    public void execute() throws CompilationError {
        exit = false;
        while(Variable.strToBool(statement.evaluate().value.toString()) && !exit)
            getChildren().get(0).execute();
    }
}
