package Runtime;

import Compilation.DataTypes.MultiDimArray;
import Compilation.DataTypes.Variable;
import Compilation.LinesToInstructions;
import Compilation.Scope;
import Compilation.SyntaxTree.TreeNode;
import Exceptions.CompilationError;
import Exceptions.RuntimeError;
import Compilation.Compiler;
import Extra.Pair;
import Extra.Parser;

import java.util.Arrays;

import static Compilation.CompType.*;

public class Main {
    public static void main(String[] args) throws CompilationError {
        Parser parser = new Parser(args[0]);
        parser.readFile();
        Pair<String[],Integer>[] instructions = LinesToInstructions.getInstructions(parser.getLines());

//        for(Pair<String[],Integer> pair : instructions){
//            String instruction = Arrays.toString(pair.getFirst());
//            System.out.println("Line " + pair.getSecond() + ": " + instruction);
//        }

        // ANSI escape code for red text
        final String RED = "\033[0;31m";
        // ANSI escape code for green text
        final String GREEN = "\033[1;32m";
        // ANSI escape code to reset color
        final String RESET = "\033[0m";

        try{
            long compileStart = System.currentTimeMillis();
            TreeNode program = Compiler.compile(instructions);
            long compileEnd = System.currentTimeMillis();
            System.out.println(GREEN + "Compilation Success! Done in " + (compileEnd-compileStart) + "ms" + RESET);
            program.execute();
            long runtimeEnd = System.currentTimeMillis();
            System.out.println(GREEN + "Program execution finished in " + (runtimeEnd-compileEnd) + "ms" + RESET);
        }catch (CompilationError e){
            System.out.println(RED + "Compilation Error: " + e.getMessage() + RESET);
        }catch (RuntimeError e){
            System.out.println(RED + "Runtime Error: " + e.getMessage() + RESET);
        }
    }
}