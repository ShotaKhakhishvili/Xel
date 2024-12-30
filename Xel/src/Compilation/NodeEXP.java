package Compilation;

import static Compilation.CompType.*;

public class NodeEXP extends TreeNode{
    CompType expType;
    public NodeEXP(String[] data, CompType expType) {
        super(CompType.EXP, data);
        this.expType = expType;
    }
    public CompType getExpType(){
        return expType;
    }
}
