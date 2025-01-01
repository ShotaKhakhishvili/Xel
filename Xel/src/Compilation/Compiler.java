package Compilation;

import Compilation.SyntaxTree.TreeNode;
import Exceptions.CompilationError;
import Extra.Pair;

import java.util.Arrays;

import static Compilation.CompType.*;
import static Compilation.Decoder.*;

public class Compiler {

    static int programsCreated = 0;
    public static int currentLine = 1;
    public static TreeNode compile(Pair<String[],Integer>[] instructions) throws CompilationError {
        TreeNode currentProgram = new TreeNode(new Scope());

        for(Pair<String[],Integer> pair : instructions){
            String[] instruction = pair.getFirst();
            currentLine = pair.getSecond();

            CompType type = Decoder.getGeneralType(instruction, currentProgram);

            if(type == SCOPEE){
                programsCreated--;
                currentProgram = currentProgram.getParentNode();
                continue;
            }
            else if(type == SCOPES){
                programsCreated++;
                TreeNode newNode = new TreeNode(currentProgram, new Scope(currentProgram.getScope()));
                currentProgram.addChild(newNode);
                currentProgram = newNode;
                continue;
            }

            try {
                TreeNode newNode = evaluateNode(type, instruction, currentProgram);
                currentProgram.addChild(newNode);
            }catch (CompilationError e){
                throw new CompilationError(e.getMessage(), pair.getSecond());
            }catch (RuntimeException e){
                try{
                    int errorIndex = Integer.parseInt(e.getMessage());
                    throw new CompilationError(CompilationError.errors.get(errorIndex), pair.getSecond());
                } catch (NumberFormatException ex) {
                    throw new RuntimeException(ex);
                }
            }

        }

        return currentProgram;
    }

    private static TreeNode evaluateNode(CompType type, String[] instruction, TreeNode parentNode) throws CompilationError {
        return switch (type) {
            case DECL -> DECL_checkValidity(instruction, parentNode);
            case ASGM -> Decoder.ASGM_checkValidity(instruction, parentNode);
            case EXP -> Decoder.EXP_checkValidity(instruction, parentNode);
            case PRINT -> Decoder.PRINT_checkValidity(instruction, parentNode);
            case INPUT -> Decoder.INPUT_checkValidity(instruction, parentNode);
            default -> throw new CompilationError(404);
        };
    }
}
