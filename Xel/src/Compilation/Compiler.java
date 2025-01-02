package Compilation;

import Compilation.SyntaxTree.*;
import Exceptions.CompilationError;
import Extra.Pair;

import java.util.Arrays;

import static Compilation.CompType.*;
import static Compilation.Decoder.*;

public class Compiler {

    public static int currentLine = 1;

    public static TreeNode compile(Pair<String[],Integer>[] instructions) throws CompilationError {
        TreeNode currentProgram = new TreeNode(new Scope());

        for(Pair<String[],Integer> pair : instructions){
            String[] instruction = pair.getFirst();
            currentLine = pair.getSecond();

            CompType type = Decoder.getGeneralType(instruction, currentProgram);

            if(type == SCOPEE){
                if(currentProgram.isScopeShortenable() && currentProgram.getChildren().isEmpty())
                    throw new CompilationError(new CompilationError(21).getMessage(), currentLine);
                currentProgram = currentProgram.getParentNode(); // Climbing up to the parent node
                if(currentProgram.isScopeShortenable()) // If the new parent is scope shortenable, then we climb up one more time
                    currentProgram = currentProgram.getParentNode();
                continue;
            }
            else if(type == SCOPES){
                TreeNode newNode = new TreeNode(currentProgram, new Scope(currentProgram.getScope()));
                currentProgram.addChild(newNode);
                currentProgram = newNode;
                continue;
            }
            try {
                TreeNode newNode = evaluateNode(type, instruction, currentProgram);
                currentProgram.addChild(newNode);

                // If the new statement is a scope statement, we climb down to it.
                if(newNode.isScopeStatement())
                    currentProgram = newNode;

                // This is for one-statement scope parents.
                // If we have some one-statement scope and we add a child to it, we climb back to the parent immediately.
                // We don't check for .getParentNode() being null, that only happens for a program node, which is not shortenable.
                else if(currentProgram.isScopeShortenable())
                    currentProgram = currentProgram.getParentNode();
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

        if(currentProgram.isScopeShortenable())
            throw new CompilationError(new CompilationError(21).getMessage(), currentLine);
        return currentProgram;
    }

    private static TreeNode evaluateNode(CompType type, String[] instruction, TreeNode parentNode) throws CompilationError {
        return switch (type) {
            case DECL -> DECL_checkValidity(instruction, parentNode);
            case ASGM -> Decoder.ASGM_checkValidity(instruction, parentNode);
            case EXP -> Decoder.EXP_checkValidity(instruction, parentNode);
            case PRINT -> Decoder.PRINT_checkValidity(instruction, parentNode);
            case INPUT -> Decoder.INPUT_checkValidity(instruction, parentNode);
            case IF -> Decoder.IF_checkValidity(instruction, parentNode);
            case WHILE -> Decoder.WHILE_checkValidity(instruction,parentNode);
            case CNT,BRK -> Decoder.NodeCMD(type, parentNode);
            case FOR -> Decoder.FOR_checkValidity(instruction, parentNode);
            case ELIF -> { // If the last statement was not NodeIF type, then we throw an error.
                if(parentNode.getChildren().isEmpty() ||
                        !(parentNode.getChildren().get(parentNode.getChildren().size()-1) instanceof NodeIF))
                    throw new CompilationError(22);
                yield Decoder.ELSEIF_checkValidity(instruction, parentNode);
            }
            case ELSE -> {// If the last statement was not NodeIF type, then we throw an error.
                if(parentNode.getChildren().isEmpty() ||  !(parentNode.getChildren().get(parentNode.getChildren().size()-1) instanceof NodeIF))
                    throw new CompilationError(22);
                // Otherwise, we make a "else true" kind of if statement, that would always execute if previous if statements failed.
                yield new NodeIF(new NodeEXP("true",LIT,parentNode),parentNode);
            }
            default -> throw new CompilationError(404);
        };
    }
}
