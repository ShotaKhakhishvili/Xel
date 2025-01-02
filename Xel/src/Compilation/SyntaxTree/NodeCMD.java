package Compilation.SyntaxTree;

import Compilation.CompType;
import Exceptions.CompilationError;

public class NodeCMD extends TreeNode{
    private CompType commandType;

    public NodeCMD(CompType commandType, TreeNode parentNode){
        super(parentNode);
        this.commandType = commandType;
    }

    public CompType getCommandType() {
        return commandType;
    }

    @Override
    public void execute() throws CompilationError {
        switch (commandType){
            case CNT -> {
                TreeNode currNode = getParentNode();
                while(!(currNode instanceof NodeLOOP)){
                    currNode.exit = true;
                    currNode = currNode.getParentNode();
                }
            }
            case BRK -> {
                TreeNode currNode = getParentNode();
                while(!(currNode instanceof NodeLOOP)){
                    currNode.exit = true;
                    currNode = currNode.getParentNode();
                }
                currNode.exit = true;
            }
        }
    }
}
