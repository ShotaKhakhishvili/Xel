package Compilation;

import Compilation.SyntaxTree.TreeNode;
import Exceptions.CompilationError;
import Extra.Pair;

import java.util.Arrays;

import static Compilation.CompType.*;
import static Compilation.Decoder.*;

public class Compiler {

    static int programsCreated = 0;

    public static TreeNode compile(Pair<String[],Integer>[] instructions) throws CompilationError {
        TreeNode currentProgram = new TreeNode(new Scope());

        for(Pair<String[],Integer> pair : instructions){
            String[] instruction = pair.getFirst();

            CompType type = Decoder.getGeneralType(instruction, currentProgram);

            if(type == SCOPEE){
                programsCreated--;
                currentProgram = currentProgram.getParentNode();
                continue;
            }
            else if(type == SCOPES){
                programsCreated++;
                currentProgram = new TreeNode(currentProgram, new Scope(currentProgram.getScope()));
                continue;
            }

            try {
                TreeNode newNode = evaluateNode(type, instruction, currentProgram);
                System.out.println(Arrays.toString(instruction) + ": " + type + " " + programsCreated);
                currentProgram.addChild(newNode);
            }catch (CompilationError e){
                throw new CompilationError(e.getMessage(), pair.getSecond());
            }

        }

        return currentProgram;
    }

    private static TreeNode evaluateNode(CompType type, String[] instruction, TreeNode parentNode) throws CompilationError {
        return switch (type) {
            case DECL -> DECL_checkValidity(instruction, parentNode);
            case ASGM -> Decoder.ASGM_checkValidity(instruction, parentNode);
            case EXP -> Decoder.EXP_checkValidity(instruction, parentNode);
            default -> {
                System.out.println(type + " NOT IMPLEMENTED YET, SORRY");
                yield new TreeNode(parentNode);
            }
        };
    }
}
