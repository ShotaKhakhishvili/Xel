package Compilation;

import Extra.Functions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import Runtime.*;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import static Compilation.CompType.*;

public class Compiler {
    public static void compile(String[] lines) throws CompilationError {
        int pc = 1;

        TreeNode program = new TreeNode(CompType.PROG, null);
        Scope globalScope = new Scope();
        Scope currentScope = globalScope;

        program.setScope(globalScope);

        for(String line : lines){
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
            pc++;
        }
    }

    private static TreeNode evaluateNode(CompType type, String line, String[] parts, Scope currentScope) throws CompilationError {
        switch (type){
            case DECL:
                return Decoder.DECL_checkValidity(line, parts, currentScope);
            case EXP :
                return Decoder.EXP_checkValidity(line, parts, currentScope.getMemory());
            default:
                System.out.println("NOT IMPLEMENTED YET, SORRY");
                return new TreeNode(null, null);
        }
    }

    public static void test() throws CompilationError{
        String exp = "((2.5 - a)) * 3.5 % (1 + 2)";

        Memory memory = new Memory();

        memory.declareVariable("ab", 3);

        NodeEXP node = Decoder.EXP_checkValidity(exp, exp.split(" ") , memory);

        System.out.println("Answer: " + ArithmeticExpression.executeExpression(node, memory).value);
    }
}
