package Compilation.SyntaxTree;

import Compilation.Variable;
import Exceptions.CompilationError;

public class NodePRINT extends TreeNode{
    private final NodeEXP printExp;

    public NodePRINT(NodeEXP printEXP, TreeNode parentNode){
        super(parentNode);
        this.printExp = printEXP;
    }

    @Override
    public void execute() throws CompilationError {
        Variable<?> value = printExp.evaluate();
        System.out.println("OUTPUT: " + value.value);
    }
}
