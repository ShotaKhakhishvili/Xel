package Compilation.SyntaxTree;

import Compilation.CompType;

public class NodeCMD extends TreeNode{
    private CompType commandType;

    public NodeCMD(CompType commandType, TreeNode parentNode){
        super(parentNode);
        this.commandType = commandType;
    }

    public CompType getCommandType() {
        return commandType;
    }
}
