package Compilation.SyntaxTree;

import Compilation.DataTypes.Variable;
import Exceptions.CompilationError;

public class NodeFOR extends NodeLOOP{
    NodeDECL declarations;
    NodeASGM[] assignments;

    public NodeFOR(TreeNode parentNode) {
        super(parentNode);
    }

    public void setDeclarations(NodeDECL declarations) {
        this.declarations = declarations;
    }

    public void setAssignments(NodeASGM[] asgms) {
        this.assignments = asgms;
    }

    @Override
    public void execute() throws CompilationError {
        getScopeMemory().delete();
        exit = false;
        declarations.execute();
        while(Variable.strToBool(statement.evaluate().getValue().toString())){
            getChildren().get(0).execute();
            if(exit)break;
            for(NodeASGM asgm : assignments)
                asgm.execute();
        }
    }
}
