package Compilation;

import Extra.Functions;
import Extra.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import Compilation.Decoder.*;

public class LinesToInstructions {

    // Strings arrays are the instructions, while integers are their corresponding lines from the user input
    public static Pair<String[],Integer>[] getInstructions(String[] lines){
        List<Pair<List<String>,Integer>> instructions = new ArrayList<>();

        for(int i = 0; i < lines.length; i++){
            List<String> parts = Functions.generalTokenizer(lines[i]);
            List<List<String>> instructionsParts = Functions.groupInstructions(parts);

            for(List<String> instruction : instructionsParts){
                instructions.add(new Pair<>(instruction,i+1));
            }
        }
        Pair<String[],Integer>[] instructionArray = new Pair[instructions.size()];

        for(int i = 0; i < instructions.size(); i++){
            instructionArray[i] = new Pair<>(instructions.get(i).getFirst().toArray(new String[0]),instructions.get(i).getSecond());
        }

        return instructionArray;
    }
}
