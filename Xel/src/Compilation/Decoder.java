package Compilation;

import Compilation.SyntaxTree.NodeASGM;
import Compilation.SyntaxTree.NodeDECL;
import Compilation.SyntaxTree.NodeEXP;
import Compilation.SyntaxTree.TreeNode;
import Exceptions.CompilationError;
import Extra.Functions;

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

    public static final Map<CompType, BinaryOperator<Variable>> BIOP_Functions = new HashMap<>(){
        {
            put(ADD, Variable::add);
            put(SUB, Variable::sub);
            put(DIV, Variable::div);
            put(MULT, Variable::mult);
            put(MOD, Variable::mod);
        }
    };
    public static final Map<String, CompType> BIOP_Types = new HashMap<>(){
        {
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
//        parts = Functions.tokenize(statement).toArray(new String[0]);

//        System.out.println(Arrays.toString(parts));

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

    static TreeNode DECL_checkValidity(String statement, String[] parts, Scope scope) throws CompilationError {
        String variables = String.join("", Arrays.copyOfRange(parts, 1, parts.length));

        for(Character ch : invalidNameChars)
            if(variables.contains(String.valueOf(ch)) && ch != ',')
                throw  new CompilationError(0);//CODE0

        Set<String> tokens = new HashSet<>();

        StringBuilder current = new StringBuilder();

        for(Character ch : variables.toCharArray()){
            if(ch == ','){
                if(!current.isEmpty()){

                    if(easyTypes.containsKey(current.toString()) || varTypes.containsKey(current.toString()))
                        throw new CompilationError(2);//CODE2
                    if(scope.getMemory().containsVariable(current.toString()) || tokens.contains(current.toString()))
                        throw new CompilationError(1);//CODE1

                    tokens.add(current.toString());
                    current = new StringBuilder();
                }
            }
            else{

                if(current.isEmpty() && ch >= '0' && ch <= '9')
                        throw new CompilationError(0);//CODE0
                current.append(ch);

            }
        }

        if(!current.isEmpty()){

            if(easyTypes.containsKey(current.toString()) || varTypes.containsKey(current.toString()))
                throw new CompilationError(2);//CODE2
            if(scope.getMemory().containsVariable(current.toString()) || tokens.contains(current.toString()))
                throw new CompilationError(1);//CODE1
            tokens.add(current.toString());

        }

        for(String varName : tokens)
            scope.getMemory().declareVariable(varName, Variable.getDefaultValue(varTypes.get(parts[0])));

        return new NodeDECL(varTypes.get(parts[0]), tokens.toArray(new String[0]), scope);
    }

    static NodeEXP EXP_checkValidity(String statement, String[] parts, Scope scope) throws CompilationError {
        for(String part : parts){
            for(char ch : part.toCharArray()){
                if(invalidNameChars.contains(ch) && !BIOP_Types.containsKey(Character.toString(ch)) && ch != '(' && ch != ')' && ch != '.')
                    throw new CompilationError(5);//CODE5
            }
        }

        String joined = String.join(" ", Arrays.copyOfRange(parts, 0, parts.length));

        String[] tokens = Functions.tokenize(joined).toArray(new String[0]);

        System.out.println(Arrays.toString(tokens));

        return EXP_checkSyntax(tokens, 0, tokens.length, scope);
    }

    public static NodeEXP EXP_checkSyntax(String[] tokens,int l, int r, Scope scope) throws CompilationError {
        if(r - l == 0)
            throw  new CompilationError(8);//CODE8
        if(r - l == 1){
            try {
                try {
                    Long.parseLong(String.valueOf(tokens[l]));
                    return new NodeEXP(tokens[l], LIT, scope);
                }catch (NumberFormatException e){
                    Double.parseDouble(String.valueOf(tokens[l]));
                    return new NodeEXP(tokens[l], LIT, scope);
                }
            }catch (NumberFormatException e){
                if (scope.getMemory().containsVariable(tokens[l]))
                    return new NodeEXP(tokens[l], VAR, scope);
                throw new CompilationError(9);//CODE9
            }
        }
        if(tokens[l].equals("-")){
            String[] newSequence = new String[r-l+1];
            newSequence[0] = "0";
            for(int i = l; i < r; i++){
                newSequence[i-l+1] = tokens[i];
            }
            return EXP_checkSyntax(newSequence,0,r-l+1,scope);
        }
        if(tokens[l].equals("+")){
            return EXP_checkSyntax(tokens,l+1,r,scope);
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
                        throw new CompilationError(6);//CODE6

                    if(i == l && starting == r - 1) {
                        for(int a = l + 1, b = r - 2; a < b; a++,b--){
                            if(!(tokens[a].equals("(") && tokens[b].equals(")")))
                                return EXP_checkSyntax(tokens,a, b+1, scope);
                        }
                    }
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
                left = EXP_checkSyntax(tokens, l, low,scope);
                right = EXP_checkSyntax(tokens, high, r,scope);

                NodeEXP answer = new NodeEXP(tokens[high-1], BIOP_Types.get(tokens[high-1]), scope);

                answer.getChildren().add(left);
                answer.getChildren().add(right);

                return answer;
            }
        }

        throw new CompilationError(11);//CODE11
    }

    static NodeASGM ASGM_checkValidity(String statement, String[] parts, Scope scope) throws CompilationError {
        if(!scope.getMemory().containsVariable(parts[0]))
            throw new CompilationError(10);//CODE10

        if(parts.length < 2)
            throw new CompilationError(11);//CODE11

        CompType asgmType;
        int cropped = 0;

        if(parts[1].charAt(0) == '='){
            asgmType = ASGM;
            if(parts[1].length() > 1){
                cropped = 1;
                parts[1] = parts[1].substring(2);
            }
        }
        else if(parts[1].length() > 1){
            if(BIOP_Types.containsKey(String.valueOf(parts[1].charAt(0))) && parts[1].charAt(1) == '='){
                asgmType = BIOP_Types.get(String.valueOf(parts[1].charAt(0)));
                if(parts[1].length() > 2){
                    cropped = 1;
                    parts[1] = parts[1].substring(2);
                }
            }
            else
                throw new CompilationError(12);//CODE12
        }else
            throw new CompilationError(12);//CODE12

        NodeEXP exp = EXP_checkValidity(statement, Arrays.copyOfRange(parts,2 - cropped, parts.length), scope);

        return new NodeASGM(parts[0], asgmType, exp, scope);
    }
}

