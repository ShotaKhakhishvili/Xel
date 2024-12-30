package Runtime;

import Compilation.*;
import Compilation.Compiler;

import javax.script.ScriptException;
import java.util.Base64;

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
//            Compiler.compile(parser.getLines());
            Compiler.test();

            System.out.println(GREEN + "Compilation Success!" + RESET);
        }catch (CompilationError e){
            System.out.println(RED + "Compilation Error: " + e.getMessage() + RESET);
        }catch (RuntimeError e){
            System.out.println(RED + "Runtime Error: " + e.getMessage() + RESET);
        }
    }
}