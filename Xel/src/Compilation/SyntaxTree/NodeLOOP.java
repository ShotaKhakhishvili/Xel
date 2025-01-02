package Compilation.SyntaxTree;

import Compilation.Scope;

public class NodeLOOP extends TreeNode{
    protected NodeEXP statement;

    public NodeLOOP(NodeEXP statement, TreeNode parentNode) {
        super(parentNode, new Scope(parentNode.getScope()));
        this.statement = statement;
    }

    public NodeLOOP(TreeNode parentNode){
        super(parentNode, new Scope(parentNode.getScope()));
    }

    public void setStatement(NodeEXP statement) {
        this.statement = statement;
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
