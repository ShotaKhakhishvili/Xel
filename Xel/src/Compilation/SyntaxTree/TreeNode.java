package Compilation.SyntaxTree;

import Compilation.Memory;
import Compilation.Scope;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {
    private List<TreeNode> children = new ArrayList<>();
    private Scope scope;

    public TreeNode(Scope scope){
        this.scope = scope;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public Memory getScopeMemory(){
        return scope.getMemory();
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public void addChild(TreeNode newNode){
        children.add(newNode);
    }

    public void execute(){
        for(TreeNode child : getChildren()){
            child.execute();
        }
    }
    public Object evaluate(){
        return null;
    }
}

