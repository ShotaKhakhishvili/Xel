package Compilation.SyntaxTree;

import Compilation.Memory;
import Compilation.Scope;
import Exceptions.CompilationError;
import Exceptions.RuntimeError;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {
    private List<TreeNode> children = new ArrayList<>();
    private Scope scope;
    private TreeNode parentNode;
    private int line;

    public TreeNode(TreeNode parentNode){
        this.parentNode = parentNode;
        this.scope = new Scope(parentNode.scope);
    }

    public TreeNode getParentNode() {
        return parentNode;
    }

    public TreeNode(Scope scope){
        this.scope = scope;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
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

    public void execute() throws CompilationError {
        for(TreeNode child : getChildren()){
            try {
                child.execute();
            }catch (RuntimeError e){
                throw new RuntimeError(e.getMessage(), child.getLine());
            }
        }
    }
    public Object evaluate() throws CompilationError {
        return null;
    }
}

