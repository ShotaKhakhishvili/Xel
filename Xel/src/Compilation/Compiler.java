package Compilation;

import Compilation.SyntaxTree.NodeASGM;
import Compilation.SyntaxTree.NodeEXP;
import Compilation.SyntaxTree.TreeNode;
import Exceptions.CompilationError;
import Exceptions.RuntimeError;

import static Compilation.CompType.*;

public class Compiler {
    public static TreeNode compile(String[] lines) throws CompilationError {
        int pc = 1;

        Scope globalScope = new Scope();
        Scope currentScope = globalScope;

        TreeNode program = new TreeNode(currentScope);

        program.setScope(globalScope);

        for(String line : lines){
            if(line.isEmpty())continue;

            String[] parts = line.split(" ");

            TreeNode newNode;
            CompType type =  Decoder.getGeneralType(line, parts, currentScope);
            try {
                newNode = evaluateNode(type, line, parts, currentScope);
            }catch (CompilationError e){
                throw new CompilationError(e.getMessage(), pc);
            }catch (RuntimeError e){
                throw new RuntimeError(e.getMessage(), pc);
            }

            newNode.setScope(currentScope);
            program.addChild(newNode);

            pc++;
        }

        return program;
    }

    private static TreeNode evaluateNode(CompType type, String line, String[] parts, Scope currentScope) throws CompilationError {
        switch (type){
            case DECL:
                return Decoder.DECL_checkValidity(line, parts, currentScope);
            case ASGM:
                return Decoder.ASGM_checkValidity(line, parts, currentScope);
            case EXP :
                return Decoder.EXP_checkValidity(line, parts, currentScope);
            default:
                System.out.println("NOT IMPLEMENTED YET, SORRY");
                return new TreeNode(currentScope);
        }
    }

    public static void test() throws CompilationError{
        String exp = "+((2.5 - ab)) * 1 + 13 % (1 + 2)";
        String asg = "ab = " + exp;
        Scope scope = new Scope();

        Memory memory = scope.getMemory();

        memory.declareVariable("ab", 3);

        NodeEXP node = Decoder.EXP_checkValidity(exp, exp.split(" ") , scope);

        NodeASGM asgm = new NodeASGM("a", INC, node, scope);

        Decoder.ASGM_checkValidity(asg, asg.split(" "), scope);

        asgm.execute();

        System.out.println("Value: " + memory.getVariable("ab").value);
    }
}
