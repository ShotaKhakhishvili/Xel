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

    static final Map<String,CompType> scopedInstructions = new HashMap<>(){
        {
            put("if", IF);
            put("elif", ELIF);
            put("else", ELSE);
            put("while", WHILE);
            put("for", FOR);
            put("func", FDEC);
        }
    };

    static CompType getGeneralType(String[] parts, TreeNode parentNode){
        if(scopedInstructions.containsKey(parts[0]))
            return scopedInstructions.get(parts[0]);

        if(parts[0].equals("{"))
            return SCOPES;
        if(parts[0].equals("}"))
            return SCOPEE;

        if(varTypes.containsKey(parts[0])){
            if(Arrays.stream(parts).toList().contains("="))
                return INIT;
            return DECL;
        }
        if(parentNode.getScope().getMemory().getFunctions().containsKey(parts[0]))
            return FCALL;
        if(parentNode.getScope().containsVariable(parts[0]))
            return ASGM;

        return INVALID;
    }

    static TreeNode DECL_checkValidity(String[] parts, TreeNode parentNode) throws CompilationError {
        String variablesJoined = String.join("", Arrays.copyOfRange(parts, 1, parts.length));

        for(Character ch : invalidNameChars)
            if(variablesJoined.contains(String.valueOf(ch)) && ch != ',')
                throw  new CompilationError(0);//CODE0

        String[] variables = Functions.commaRemover(variablesJoined);

        for(String varName : variables){
            if(!invalidNameChars.contains(varName.charAt(0)) && !(varName.charAt(0) <= '9' && varName.charAt(0) >= '0'))
                parentNode.getScopeMemory().declareVariable(varName, Variable.getDefaultValue(varTypes.get(parts[0])));
        }

        return new NodeDECL(varTypes.get(parts[0]), variables, parentNode);
    }

    static NodeEXP EXP_checkValidity( String[] parts, TreeNode parentNode) throws CompilationError {
        for(String part : parts){
            for(char ch : part.toCharArray()){
                if(invalidNameChars.contains(ch) && !BIOP_Types.containsKey(Character.toString(ch)) && ch != '(' && ch != ')' && ch != '.')
                    throw new CompilationError(5);//CODE5
            }
        }

        return EXP_checkSyntax(parts, 0, parts.length, parentNode);
    }

    public static NodeEXP EXP_checkSyntax(String[] tokens,int l, int r, TreeNode parentNode) throws CompilationError {
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
                throw new CompilationError(9);//CODE9
            }
        }
        if(tokens[l].equals("-")){
            String[] newSequence = new String[r-l+1];
            newSequence[0] = "0";
            for(int i = l; i < r; i++){
                newSequence[i-l+1] = tokens[i];
            }
            return EXP_checkSyntax(newSequence,0,r-l+1, parentNode);
        }
        if(tokens[l].equals("+")){
            return EXP_checkSyntax(tokens,l+1,r, parentNode);
        }

        NodeEXP left;
        NodeEXP right;

        for(int j = 1; j <= 2; j++){
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
                                return EXP_checkSyntax(tokens,a, b+1, parentNode);
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
                left = EXP_checkSyntax(tokens, l, low, parentNode);
                right = EXP_checkSyntax(tokens, high, r, parentNode);

                NodeEXP answer = new NodeEXP(tokens[high-1], BIOP_Types.get(tokens[high-1]), parentNode);

                answer.getChildren().add(left);
                answer.getChildren().add(right);

                return answer;
            }
        }

        throw new CompilationError(11);//CODE11
    }

    static NodeASGM ASGM_checkValidity(String[] parts, TreeNode parentNode) throws CompilationError {
        if(!parentNode.getScope().containsVariable(parts[0]))
            throw new CompilationError(10);//CODE10

        if(parts.length < 2)
            throw new CompilationError(11);//CODE11

        CompType asgmType;

        if(parts[1].equals("="))
            asgmType = ASGM;
        else if(BIOP_Types.containsKey(String.valueOf(parts[1].charAt(0)))){
            if(parts[1].length() == 2 && parts[1].charAt(1) == '=')
                asgmType = BIOP_Types.get(String.valueOf(parts[1].charAt(0)));
            else
                throw new CompilationError(12);//CODE12
        }
        else
            throw new CompilationError(12);//CODE12

        NodeEXP exp = EXP_checkValidity(Arrays.copyOfRange(parts,2, parts.length), parentNode);

        return new NodeASGM(parts[0], asgmType, exp, parentNode);
    }
}

