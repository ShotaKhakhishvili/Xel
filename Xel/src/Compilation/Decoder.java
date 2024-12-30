package Compilation;

import Extra.Functions;
import Extra.Pair;

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

    public static final Map<String, BinaryOperator<Variable>> BIOP_Functions = new HashMap<>(){
        {
            put("+", Variable::add);
            put("-", Variable::sub);
            put("/", Variable::div);
            put("*", Variable::mult);
            put("%", Variable::mod);
        }
    };
    private static final Map<String, CompType> BIOP_Types = new HashMap<>(){
        {
            put("+", ADD);
            put("-", SUB);
            put("/", DIV);
            put("*", MULT);
            put("%", MOD);
            put("%^", POW);
            put("", INVALID);
        }
    };
    private static final Map<String,Integer> precedence = new HashMap<>(){{
        put("+", 1);
        put("-", 1);
        put("*", 2);
        put("/", 2);
        put("%", 2);
    }};

    private static final Map<String, BinaryOperator<Variable>> UOPs = new HashMap<>(){
        {
            put("-", Variable::sub);
        }
    };

    private static final Map<String,CompType> easyTypes = new HashMap<>(){
        {
            put("if", CompType.IF);
            put("elif", CompType.ELIF);
            put("else", CompType.ELSE);
            put("while", CompType.WHILE);
            put("for", CompType.FOR);
            put("func", CompType.FDEC);
        }
    };

    static CompType getGeneralType(String statement,String[] parts, Scope scope){
        if(easyTypes.containsKey(parts[0]))
            return easyTypes.get(parts[0]);

        if(parts[0].charAt(0) == '{')
            return CompType.SCOPE;
        if(varTypes.containsKey(parts[0])){
            if(statement.contains("="))
                return CompType.INIT;
            return CompType.DECL;
        }
        if(scope.getMemory().getFunctions().containsKey(parts[0]))
            return CompType.FCALL;
        if(scope.getMemory().containsVariable(parts[0]))
            return CompType.ASGM;

        return CompType.INVALID;
    }

    static TreeNode DECL_checkValidity(String statement, String[] parts, Scope scope) throws CompilationError{
        String variables = String.join("", Arrays.copyOfRange(parts, 1, parts.length));

        for(Character ch : invalidNameChars)
            if(variables.contains(String.valueOf(ch)) && ch != ',')
                throw  new CompilationError(0);

        Set<String> tokens = new HashSet<>();

        StringBuilder current = new StringBuilder();

        for(Character ch : variables.toCharArray()){
            if(ch == ','){
                if(!current.isEmpty()){

                    if(easyTypes.containsKey(current.toString()) || varTypes.containsKey(current.toString()))
                        throw new CompilationError(2);
                    if(scope.getMemory().containsVariable(current.toString()) || tokens.contains(current.toString()))
                        throw new CompilationError(1);

                    tokens.add(current.toString());
                    current = new StringBuilder();
                }
            }
            else{

                if(current.isEmpty() && ch >= '0' && ch <= '9')
                        throw new CompilationError(0);
                current.append(ch);

            }
        }

        if(!current.isEmpty()){

            if(easyTypes.containsKey(current.toString()) || varTypes.containsKey(current.toString()))
                throw new CompilationError(2);
            if(scope.getMemory().containsVariable(current.toString()) || tokens.contains(current.toString()))
                throw new CompilationError(1);
            tokens.add(current.toString());

        }

        for(String varName : tokens)
            scope.getMemory().declareVariable(varName, Variable.getDefaultValue(varTypes.get(parts[0])));

        return new NodeDECL(varTypes.get(parts[0]), tokens.toArray(new String[0]));
    }

    static NodeEXP EXP_checkValidity(String statement, String[] parts, Memory memory) throws CompilationError {

        for(String part : parts){
            for(char ch : part.toCharArray()){
                if(invalidNameChars.contains(ch) && !BIOP_Functions.containsKey(Character.toString(ch)) && ch != '(' && ch != ')' && ch != '.')
                    throw new CompilationError(5);
            }
        }

        String joined = String.join("", Arrays.copyOfRange(parts, 0, parts.length));

        String[] tokens = Functions.tokenize(joined).toArray(new String[0]);

        return EXP_checkSyntax(tokens, 0, tokens.length, memory);
    }

    public static NodeEXP EXP_checkSyntax(String[] tokens,int l, int r, Memory memory) throws CompilationError {
        if(r - l == 0)
            throw  new CompilationError(8);
        if(r - l == 1){
            try {
                try {
                    Long.parseLong(String.valueOf(tokens[l]));
                    return new NodeEXP(new String[]{tokens[l]}, LIT);
                }catch (NumberFormatException e){
                    Double.parseDouble(String.valueOf(tokens[l]));
                    return new NodeEXP(new String[]{tokens[l]}, LIT);
                }
            }catch (NumberFormatException e){
                if (memory.containsVariable(tokens[l]))
                    return new NodeEXP(new String[]{tokens[l]}, VAR);
                throw new CompilationError(9);
            }
        }

        NodeEXP left;
        NodeEXP right;

        for(int j = 1; j <= 3; j++){
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
                    }while(i < r);

                    if(counter > 0)
                        throw new CompilationError(6);

                    if(i == l && starting == r - 1) {
                        for(int a = l + 1, b = r - 2; a < b; a++,b--){
                            if(!(tokens[a].equals("(") && tokens[b].equals(")")))
                                return EXP_checkSyntax(tokens,a, b+1, memory);
                        }
                    }
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
                    else if(i == r - 1)
                        throw new CompilationError(7);
                    low = i;
                    break;
                }
                i--;
            }

            if(high > 0){
                left = EXP_checkSyntax(tokens, l, low,memory);
                right = EXP_checkSyntax(tokens, high, r,memory);

                NodeEXP answer = new NodeEXP(new String[]{tokens[high-1]}, BIOP);

                answer.getChildren().add(left);
                answer.getChildren().add(right);

                return answer;
            }
        }
        return  null;
    }

    static String[] ASGM_checkValidity(String statement, String[] parts){


        return null;
    }
}

