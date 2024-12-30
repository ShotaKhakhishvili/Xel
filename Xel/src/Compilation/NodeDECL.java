package Compilation;

public class NodeDECL extends TreeNode{
    CompType varType;

    public NodeDECL(CompType varType, String[] varNames) {
        super(CompType.DECL, varNames);
        this.varType = varType;
    }
}
