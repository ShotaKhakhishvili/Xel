package Compilation.SyntaxTree;

import java.util.Scanner;

public class NodeINPUT extends TreeNode{

    static Scanner scanner = new Scanner(System.in);
    final String printString;
    final String[] varNames;

    public NodeINPUT(String printStrings, String[] varNames, TreeNode parentNode){
        super(parentNode);
        this.printString = printStrings;
        this.varNames = varNames;
    }

    @Override
    public void execute(){
        System.out.print(printString);
        for(int i = 0; i < varNames.length; i++){
            String next = scanner.next();
            getScopeMemory().setVariable(varNames[i], next);
        }
    }

}
