package Compilation;

import java.util.Arrays;

public class Compiler {
    public static void compile(String[] lines) throws CompilationError {
        int pc = 1;

        for(String line : lines){
            String[] parts = line.split(" ");
            CompType type =  Decoder.getGeneralType(line, parts);

            try {
                switch (type){
                    case DECL:
                        Decoder.DECL_checkValidity(line, parts);
                        break;
                    case EXP :
                        Decoder.EXP_checkValidity(line, parts);
                        break;
                    default:
                        System.out.println("NOT IMPLEMENTED YET, SORRY");
                }
            }catch (CompilationError e){
                throw new CompilationError(e.getMessage() + " On Line " + pc);
            }
            pc++;
        }
    }

    public static void test() throws CompilationError{
        String str = "+12-4+32";
        String[] parts = str.split(" ");

        Decoder.EXP_checkValidity(str,parts);
    }
}
