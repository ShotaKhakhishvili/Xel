package Compilation;

import Compilation.SyntaxTree.*;
import Exceptions.CompilationError;
import Extra.Functions;
import org.w3c.dom.Node;

import java.util.*;
import java.util.function.BinaryOperator;

import static Compilation.CompType.*;

public class Decoder {
    private static final Set<Character> invalidNameChars = new HashSet<>(Set.of(
            '@', '#', '%', '^', '&', '*', '(', ')', '-', '+', '=', '{', '}',
            '[', ']', '|', '\\', ':', ';', '"', '\'', '<', '>', ',', '.', '?',
            '/', '~', '`', ' ', '$'
    ));

    private static final Map<String,CompType> varTypes = new HashMap<>(){
        {
            put("bool", BOOL);
            put("byte", BYTE);
            put("short", SHORT);
            put("int", INT);
            put("long", LONG);
            put("char", CHAR);
            put("float", FLOAT);
            put("double", DOUBLE);
            put("string", STRING);
        }
    };

    public static final Map<CompType, BinaryOperator<Variable<?>>> BIOP_Functions = new HashMap<>(){
        {
            put(ADD, Variable::add);
            put(SUB, Variable::sub);
            put(DIV, Variable::div);
            put(MULT, Variable::mult);
            put(MOD, Variable::mod);
            put(POW, Variable::pow);

            put(AND, (a,b) -> a.binaries(b, AND));
            put(OR, (a,b) -> a.binaries(b, OR));
            put(EQ, (a,b) -> a.binaries(b, EQ));
            put(NEQ, (a,b) -> a.binaries(b, NEQ));
            put(GRE, (a,b) -> a.binaries(b, GRE));
            put(LE, (a,b) -> a.binaries(b, LE));
            put(GEQ, (a,b) -> a.binaries(b, GEQ));
            put(LEQ, (a,b) -> a.binaries(b, LEQ));
            put(NOT, (a,b) -> a.binaries(b, NOT));
        }
    };
    public static final Map<String, CompType> OP_Types = new HashMap<>(){
        {
            put("&&", AND);
            put("||", OR);
            put("==", EQ);
            put("!=", NEQ);
            put(">", GRE);
            put("<", LE);
            put(">=", GEQ);
            put("<=", LEQ);
            put("+", ADD);
            put("-", SUB);
            put("/", DIV);
            put("*", MULT);
            put("%", MOD);
            put("^", POW);
            put("", INVALID);
        }
    };
    private static final Map<String,Integer> precedence = new HashMap<>(){{
        put("&&",-2);
        put("||",-1);
        put("==",0);
        put("!=",0);
        put(">",0);
        put("<",0);
        put(">=",0);
        put("<=",0);
        put("+", 1);
        put("-", 1);
        put("*", 2);
        put("/", 2);
        put("%", 2);
        put("^", 3);
    }};

    static final Map<String,CompType> scopedInstructions = new HashMap<>(){
        {
            put("if", IF);
            put("while", WHILE);
            put("for", FOR);
            put("func", FDEC);
            put("print", PRINT);
            put("input", INPUT);
            put("break", BRK);
            put("continue", CNT);
        }
    };

    static CompType getGeneralType(String[] tokens, TreeNode parentNode){
        if(scopedInstructions.containsKey(tokens[0]))
            return scopedInstructions.get(tokens[0]);

        if(tokens[0].equals("{"))
            return SCOPES;
        if(tokens[0].equals("}"))
            return SCOPEE;

        if(varTypes.containsKey(tokens[0]))
            return DECL;
        if(parentNode.getScope().getMemory().getFunctions().containsKey(tokens[0]))
            return FCALL;
        if(parentNode.getScope().containsVariable(tokens[0]))
            return ASGM;
        if(tokens.length >= 2 && tokens[0].equals("else") && tokens[1].equals("if"))
            return ELIF;
        if(tokens[0].equals("else"))
            return ELSE;

        return INVALID;
    }

    static TreeNode DECL_checkValidity(String[] tokens, TreeNode parentNode) throws CompilationError {
        String[][] declarations = Functions.declarationSeperator(Arrays.copyOfRange(tokens,1,tokens.length));
        String[] variables = new String[declarations.length];
        NodeEXP[] initExps = new NodeEXP[declarations.length];
        CompType varType = varTypes.get(tokens[0]);

        for(int i = 0; i < declarations.length; i++){
            String[] declaration = declarations[i];
            variables[i] = declaration[0];

            if(!invalidNameChars.contains(declaration[0].charAt(0)) && !(declaration[0].charAt(0) <= '9' && declaration[0].charAt(0) >= '0'))
                parentNode.getScopeMemory().declareVariable(declaration[0], String.valueOf(Variable.getDefaultValue(varType)), varType);
            else
                throw new CompilationError(0);

            if(declaration.length == 1)// Declaration without initialization
                initExps[i] = new NodeEXP(Variable.getDefaultValue(varType).toString(), LIT, parentNode);
            else{
                if(declaration[1].equals("=") && declaration.length == 2)
                    throw new CompilationError(20);
                if(!declaration[1].equals("="))
                    throw new CompilationError(19);
                initExps[i] = EXP_checkValidity(Arrays.copyOfRange(declaration,2,declaration.length),parentNode);
            }
        }

        return new NodeDECL(varTypes.get(tokens[0]), initExps, variables, parentNode);
    }

    static NodeEXP EXP_checkValidity( String[] parts, TreeNode parentNode) throws CompilationError {
//        for(String part : parts){
//            if(OP_Types.containsKey(part))continue;
//            for(char ch : part.toCharArray()){
//                if(invalidNameChars.contains(ch) && ch != '(' && ch != ')' && ch != '.' && ch != '"')
//                    throw new CompilationError(5);//CODE5
//            }
//        }

        return EXP_checkSyntax(parts, 0, parts.length, parentNode);
    }

    public static NodeEXP EXP_checkSyntax(String[] tokens,int l, int r, TreeNode parentNode) throws CompilationError {
        NodeEXP edgeCases = edgeCases(tokens,l,r,parentNode);
        if(edgeCases != null)
            return edgeCases;

        NodeEXP left;
        NodeEXP right;

        for(int j = -2; j <= 3; j++){
            int i = r - 1;
            int low = -1, high = -1;
            while(i > l){
                if(tokens[i].equals(")")){
                    int starting = i;
                    int counter = 0;
                    do{
                        if(tokens[i].equals("("))
                            counter++;
                        if(tokens[i].equals(")"))
                            counter--;
                        if(counter == 0)
                            break;
                        i--;
                    }while(i >= l);

                    if(counter < 0)
                        throw new CompilationError(6);//CODE6
                    if(i == l && starting == r - 1)
                        return EXP_checkSyntax(tokens,l+1,r-1,parentNode);
                    if(i == l + 1)
                        throw new CompilationError(7);//CODE7
                }
                if(precedence.containsKey(tokens[i]) && precedence.get(tokens[i]) == j) {
                    high = i + 1;
                    if(j == 1) {
                        int cntMinus = 0;
                        do {
                            if (tokens[i].equals("-"))
                                cntMinus++;
                            else if (!tokens[i].equals("+"))
                                break;
                            i--;
                        } while (i > l);
                        tokens[high-1] = cntMinus % 2 == 0 ? "+" : "-";
                        i++;
                    }
                    low = i;
                    break;
                }
                i--;
            }

            if(high > l){
                left = EXP_checkSyntax(tokens, l, low, parentNode);
                right = EXP_checkSyntax(tokens, high, r, parentNode);

                NodeEXP answer = new NodeEXP(tokens[high-1], OP_Types.get(tokens[high-1]), parentNode);

                answer.getChildren().add(left);
                answer.getChildren().add(right);

                return answer;
            }
        }

        throw new CompilationError(11);//CODE11
    }

    static NodeEXP edgeCases(String[] tokens,int l, int r, TreeNode parentNode) throws CompilationError {
        if(r - l == 0)
            throw  new CompilationError(8);//CODE8
        if(r - l == 1){
            try {
                try {
                    Long.parseLong(String.valueOf(tokens[l]));
                    return new NodeEXP(tokens[l], LIT, parentNode);
                }catch (NumberFormatException e){
                    Double.parseDouble(String.valueOf(tokens[l]));
                    return new NodeEXP(tokens[l], LIT, parentNode);
                }
            }catch (NumberFormatException e){
                if (parentNode.getScope().containsVariable(tokens[l]))
                    return new NodeEXP(tokens[l], VAR, parentNode);
                else if(tokens[l].charAt(0) == '"' && tokens[l].length() > 1 && tokens[l].charAt(tokens[l].length()-1) == '"')
                    return new NodeEXP(tokens[l], LIT, parentNode);
                else if(Variable.boolKeys.containsKey(tokens[l]) || Variable.longKeys.containsKey(tokens[l]) || Variable.doubleKeys.containsKey(tokens[l]))
                    return new NodeEXP(tokens[l], LIT, parentNode);
                throw new CompilationError(9);//CODE9
            }
        }
        switch (tokens[l]) {
            case "!" -> {
                String[] newSequence = new String[r - l + 1];
                newSequence[0] = "0";
                if (r - l >= 0) System.arraycopy(tokens, l, newSequence, 1, r - l);
                newSequence[1] = "==";
                return EXP_checkSyntax(newSequence, 0, r - l + 1, parentNode);
            }
            case "-" -> {
                String[] newSequence = new String[r - l + 1];
                newSequence[0] = "0";
                if (r - l >= 0) System.arraycopy(tokens, l, newSequence, 1, r - l);
                return EXP_checkSyntax(newSequence, 0, r - l + 1, parentNode);
            }
            case "+" -> {
                return EXP_checkSyntax(tokens, l + 1, r, parentNode);
            }
        }

        return null;
    }

    static NodeASGM ASGM_checkValidity(String[] tokens, TreeNode parentNode) throws CompilationError {
        if(!parentNode.getScope().containsVariable(tokens[0]))
            throw new CompilationError(10);//CODE10

        if(tokens.length < 2)
            throw new CompilationError(11);//CODE11

        CompType asgm;
        NodeEXP exp = null;

        if(tokens[1].equals("="))
            asgm = ASGM;
        else if(tokens[1].equals("++")){
            if(tokens.length > 2)
                throw new CompilationError(17);
            asgm = ASGM;
            tokens = new String[]{tokens[0],"=",tokens[0],"+","1"};
        }
        else if(tokens[1].equals("--")){
            if(tokens.length > 2)
                throw new CompilationError(18);
            asgm = ASGM;
            tokens = new String[]{tokens[0],"=",tokens[0],"-","1"};
        }
        else if(OP_Types.containsKey(String.valueOf(tokens[1].charAt(0)))){
            if(tokens[1].length() == 2 && tokens[1].charAt(1) == '=')
                asgm = OP_Types.get(String.valueOf(tokens[1].charAt(0)));
            else
                throw new CompilationError(12);//CODE12
        }
        else
            throw new CompilationError(12);//CODE12

        exp = EXP_checkValidity(Arrays.copyOfRange(tokens,2, tokens.length), parentNode);

        return new NodeASGM(tokens[0], asgm, exp, parentNode);
    }

    static NodePRINT PRINT_checkValidity(String[] tokens, TreeNode parentNode) throws CompilationError {
        return new NodePRINT(EXP_checkValidity(Arrays.copyOfRange(tokens,1,tokens.length), parentNode),parentNode);
    }

    static NodeINPUT INPUT_checkValidity(String[] tokens, TreeNode parentNode) throws CompilationError{
        String printString = "";
        int i = 1;
        if(tokens[1].charAt(0) == '"' && tokens[1].charAt(tokens[1].length()-1) == '"') {
            printString = tokens[1].substring(1,tokens[1].length()-1);
            i++;
        }
        if(tokens.length == i)
            throw new CompilationError(14);

        List<String> varNames = new ArrayList<>();

        while(i < tokens.length){
            if(tokens[i].equals(",")){
                i++;
                continue;
            }
            if(!parentNode.getScope().containsVariable(tokens[i])){
                System.out.println(tokens[i]);
                throw new CompilationError(15);
            }
            varNames.add(tokens[i]);
            i++;
        }

        return new NodeINPUT(printString, varNames.toArray(new String[0]), parentNode);
    }

    static NodeIF IF_checkValidity(String[] tokens, TreeNode parentNode) throws CompilationError {
        NodeEXP statement = EXP_checkValidity(Arrays.copyOfRange(tokens,1,tokens.length), parentNode);

        return new NodeIF(statement,  parentNode);
    }
    static NodeIF ELSEIF_checkValidity(String[] tokens, TreeNode parentNode) throws CompilationError {
        NodeEXP statement = EXP_checkValidity(Arrays.copyOfRange(tokens,2,tokens.length), parentNode);

        return new NodeIF(statement,  parentNode);
    }

    static NodeWHILE WHILE_checkValidity(String[] tokens, TreeNode parentNode) throws CompilationError {
        NodeEXP statement = EXP_checkValidity(Arrays.copyOfRange(tokens,1,tokens.length), parentNode);

        return new NodeWHILE(statement, parentNode);
    }

    static NodeCMD NodeCMD(CompType cmd, TreeNode parentNode) throws CompilationError {
        boolean b = switch (cmd){
            case CNT,BRK ->{
                TreeNode parent = parentNode;
                while(parent != null){
                    if(parent instanceof NodeWHILE)
                        yield true;
                    parent = parent.getParentNode();
                }
                yield false;
            }
            default -> false;
        };
        if(b)
            return new NodeCMD(cmd, parentNode);
        switch (cmd){
            case CNT -> throw new CompilationError(23);
            case BRK -> throw new CompilationError(24);
            default -> throw new CompilationError(404);
        }
    }
}

