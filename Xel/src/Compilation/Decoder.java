package Compilation;

import Extra.Pair;

import java.util.*;
import java.util.function.BinaryOperator;

class Decoder {
    private static final Set<Character> invalidNameChars = new HashSet<>(Set.of(
            '@', '#', '%', '^', '&', '*', '(', ')', '-', '+', '=', '{', '}',
            '[', ']', '|', '\\', ':', ';', '"', '\'', '<', '>', ',', '.', '?',
            '/', '~', '`', ' ', '$'
    ));

    private static final Map<String,CompType> varTypes = new HashMap<>(){
        {
            put("byte", CompType.BYTE);
            put("short", CompType.SHORT);
            put("int", CompType.INT);
            put("long", CompType.LONG);
            put("char", CompType.CHAR);
            put("float", CompType.FLOAT);
            put("double", CompType.DOUBLE);
            put("string", CompType.STRING);
        }
    };

    private static final Map<String, BinaryOperator<Memory.Variable<Number>>> BIOPs = new HashMap<>(){
        {
            put("+", Memory.Variable::add);
            put("-", Memory.Variable::sub);
            put("/", Memory.Variable::div);
            put("*", Memory.Variable::mult);
            put("%", Memory.Variable::mod);
        }
    };

    private static final Map<String, BinaryOperator<Memory.Variable<Number>>> UOPs = new HashMap<>(){
        {
            put("-", Memory.Variable::sub);
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

    static CompType getGeneralType(String statement,String[] parts){
        if(easyTypes.containsKey(parts[0]))
            return easyTypes.get(parts[0]);

        if(parts[0].charAt(0) == '{')
            return CompType.SCOPE;
        if(varTypes.containsKey(parts[0])){
            if(statement.contains("="))
                return CompType.INIT;
            return CompType.DECL;
        }
        if(Memory.getFunctions().containsKey(parts[0]))
            return CompType.FCALL;
        if(Memory.getVariables().containsKey(parts[0]))
            return CompType.ASGM;

        return CompType.INVALID;
    }

    static String[] DECL_checkValidity(String statement, String[] parts) throws CompilationError{
        String variables = String.join("", Arrays.copyOfRange(parts, 1, parts.length));

        for(Character ch : invalidNameChars)
            if(variables.contains(String.valueOf(ch)) && ch != ',')
                throw  new CompilationError(0);

        Set<String> tokens = new HashSet<>(){{ add(parts[0]); }};

        StringBuilder current = new StringBuilder();

        for(Character ch : variables.toCharArray()){
            if(ch == ','){
                if(!current.isEmpty()){
                    if(easyTypes.containsKey(current.toString()) || varTypes.containsKey(current.toString()))
                        throw new CompilationError(0);
                    if(Memory.getVariables().containsKey(current.toString()) || tokens.contains(current.toString()))
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
                throw new CompilationError(0);
            if(Memory.getVariables().containsKey(current.toString()) || tokens.contains(current.toString()))
                throw new CompilationError(1);
            tokens.add(current.toString());
        }

        return tokens.toArray(new String[0]);
    }

    static String[] EXP_checkValidity(String statement, String[] parts) throws CompilationError {
        for(String part : parts){
            for(char ch : part.toCharArray()){
                if(invalidNameChars.contains(ch) && !BIOPs.containsKey(Character.toString(ch)))
                    throw new CompilationError(5);
            }
        }

        String joined = String.join("", Arrays.copyOfRange(parts, 0, parts.length));

        for(char ch : joined.toCharArray()){
            if((ch >= '0' && ch <= '9') || BIOPs.containsKey(Character.toString(ch))){

            }
        }

        return null;
    }

    private static void EXP_checkSyntax(boolean left){

    }

    static String[] ASGM_checkValidity(String statement, String[] parts){


        return null;
    }
}

