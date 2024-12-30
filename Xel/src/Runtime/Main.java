package Runtime;

import Compilation.Compiler;
import Compilation.SyntaxTree.TreeNode;
import Compilation.Variable;
import Exceptions.CompilationError;
import Exceptions.RuntimeError;
import Extra.Parser;

import java.security.KeyPair;
import java.util.Map;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws CompilationError {
        Parser parser = new Parser(args[0]);
        parser.readFile();

        // ANSI escape code for red text
        final String RED = "\033[0;31m";
        // ANSI escape code for green text
        final String GREEN = "\033[1;32m";
        // ANSI escape code to reset color
        final String RESET = "\033[0m";

        try{
            TreeNode program = Compiler.compile(parser.getLines());
            System.out.println(GREEN + "Compilation Success!" + RESET);

            program.execute();

            Map<String, Variable<Integer>> ints =  program.getScopeMemory().getInts();
            Set<String> keyset = ints.keySet();
            for(String key : keyset){
                System.out.println(key + ": " + ints.get(key).value);
            }

        }catch (CompilationError e){
            System.out.println(RED + "Compilation Error: " + e.getMessage() + RESET);
        }catch (RuntimeError e){
            System.out.println(RED + "Runtime Error: " + e.getMessage() + RESET);
        }
    }
}