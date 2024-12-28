package Compilation;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.io.*;
import java.util.List;
import java.util.Scanner;


/**
 * The {@code Parser} class processes BASIC source code files.
 *
 * It reads the file and stores its content in a single string,
 * {@code code} and an array of lines {@code lines}.
 */
public class Parser{

    //List to store lines read from the file
    private List<String> fileLines = new ArrayList<>();

    //Array to hold processed lines
    private String[] lines;

    //Name of the file to be parsed - fileName
    private static String fileName;

    /**
     * Constructs a {@code Parser} instance with the given file name.
     * @param fileName the name of the file to be parsed
     */
    public Parser(String fileName){
        this.fileName = fileName;
    }


    public void readFile(){
        File file = new File(fileName);

        String name = String.valueOf(Parser.class.getProtectionDomain().getCodeSource().getLocation());

        name = name.substring(0, name.length() - 27);

        name += "Xel/Xel/Codes/" + fileName;
        name = name.substring(6);

        // Try-with-resources to ensure the BufferedReader is closed automatically
        try (BufferedReader reader = new BufferedReader(new FileReader(name))) {
            String line;

            // Read each line and add it to the fileLines list
            while ((line = reader.readLine()) != null){
                fileLines.add(line);
            }

        } catch (FileNotFoundException e) {
            // Handle the case where the specified file does not exist
            System.out.println("File not found: " + name);

        } catch (IOException e) {
            // Handle errors that occur while reading the file
            System.out.println("An error occurred while reading the file: " + e.getMessage());
        }


        // Prepare the lines for further processing
        evalLines();
    }


    private void evalLines(){
        lines = new String[fileLines.size()];

        // Go through each line in fileLines and fill the lines array
        for(int i = 0; i < fileLines.size(); i++)
            lines[i] = fileLines.get(i);
    }




    public String[] getLines(){
        return lines;
    }

}