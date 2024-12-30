package Compilation;

import Compilation.SyntaxTree.NodeASGM;
import Compilation.SyntaxTree.NodeEXP;
import Compilation.SyntaxTree.TreeNode;
import Exceptions.CompilationError;
import Exceptions.RuntimeError;
import Extra.Functions;

import java.util.Arrays;
import java.util.List;

import static Compilation.CompType.*;

public class Compiler {
    public static TreeNode compile(String[] lines) throws CompilationError {
        int pc = 1;

//        lines = Functions.tokenizeCurlyBraces(lines).toArray(new String[0]);
//        lines = Functions.tokenizeParentheses(lines).toArray(new String[0]);

        TreeNode currentProgram = new TreeNode(new Scope());

        for(String line : lines){
            line = line.trim();
            if(line.isEmpty())continue;
            if(line.equals("{")){
                currentProgram = new TreeNode(currentProgram);
                currentProgram.getParentNode().addChild(currentProgram);
                pc++;
                continue;
            }
            if(line.equals("}")){
                if(currentProgram.getParentNode() == null)
                    throw new CompilationError(CompilationError.errors.get(13), pc);
                currentProgram.getScopeMemory().delete();
                currentProgram = currentProgram.getParentNode();
                pc++;
                continue;
            }

            String[] parts = line.split(" ");

            TreeNode newNode;
            CompType type =  Decoder.getGeneralType(line, parts, currentProgram.getScope());
            try {
                newNode = evaluateNode(type, line, parts, currentProgram.getScope());
            }catch (CompilationError e){
                throw new CompilationError(e.getMessage(), pc);
            }catch (RuntimeError e){
                throw new RuntimeError(e.getMessage(), pc);
            }

            newNode.setScope(currentProgram.getScope());
            newNode.setLine(pc);
            currentProgram.addChild(newNode);

            pc++;
        }

        currentProgram.getScopeMemory().delete();

        return currentProgram;
    }

    private static TreeNode evaluateNode(CompType type, String line, String[] parts, Scope currentScope) throws CompilationError {
        String[] tokens = Functions.assignmentTokenizer(line).toArray(new String[0]);
        switch (type){
            case DECL:
                return Decoder.DECL_checkValidity(line, parts, currentScope);
            case ASGM:
                return Decoder.ASGM_checkValidity(line, tokens, currentScope);
            case EXP :
                return Decoder.EXP_checkValidity(line, tokens, currentScope);
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
    }
}
