package Compilation;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {
    private CompType type;
    private List<TreeNode> children = new ArrayList<>();
    private Scope scope;
    private String[] data;

    public TreeNode(CompType type, String[] data) {
        this.type = type;
        this.data = data;
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

    public CompType getType() {
        return type;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public String[] getData() {
        return data;
    }
}

class Scope {

    private Scope parentScope;
    private final Memory memory = new Memory();

    public Scope getParentScope() {
        return parentScope;
    }

    public Memory getMemory() {
        return memory;
    }
}
