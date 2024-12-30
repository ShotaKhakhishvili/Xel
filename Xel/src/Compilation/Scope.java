package Compilation;

public class Scope {

    private Scope parentScope;
    private final Memory memory = new Memory();

    public Scope getParentScope() {
        return parentScope;
    }

    public Memory getMemory() {
        return memory;
    }
}
