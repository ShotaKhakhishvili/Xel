package Compilation.SyntaxTree;

import Compilation.CompType;
import Compilation.Compiler;
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
    protected boolean exit;

    public TreeNode(){
        line = Compiler.currentLine;
    }

    // For default child/parent inheritance
    public TreeNode(TreeNode parentNode){
        this();
        this.parentNode = parentNode;
        this.scope = parentNode.getScope();
    }

    // Used for new scope starts, but can be used as usual also
    public TreeNode(TreeNode parentNode, Scope scope){
        this();
        this.parentNode = parentNode;
        this.scope = scope;
    }

    // For the main program node
    public TreeNode(Scope scope){
        this();
        this.scope = scope;
    }

    public TreeNode getParentNode() {
        return parentNode;
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

    public int getLine(){
        return this.line;
    }

    public void execute() throws CompilationError {
        exit = false;
        getScopeMemory().delete();
        boolean ifChain = false;
        for(TreeNode child : getChildren()){
            try {
                if(child instanceof NodeIF){
                    if(ifChain)continue;
                    ifChain = ((NodeIF) child).evaluate();
                    continue;
                }
                ifChain = false;
                executeChild(child);
                if(exit)break;
            }catch (RuntimeError e){
                throw new RuntimeError(e.getMessage(), child.getLine());
            }
        }
    }

    private void executeChild(TreeNode child) throws CompilationError {
        child.execute();
    }

    public Object evaluate() throws CompilationError {
        return null;
    }

    public boolean isScopeShortenable(){
        return false;
    }

    public boolean isScopeStatement(){
        return false;
    }
}

