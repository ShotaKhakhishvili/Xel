package Compilation;

import Exceptions.RuntimeError;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static Compilation.CompType.*;

public class Variable<T>{
    public T value;

    public static final Map<String,Boolean> boolKeys = new HashMap<>(){{
        put("true", true);
        put("false", false);
        put("jaybe", Math.random() > 0.5);
    }};
    public static final Map<String,Long> longKeys = new HashMap<>(){{
        put("BYTE_MIN", (long) Byte.MIN_VALUE);
        put("BYTE_MAX", (long) Byte.MAX_VALUE);
        put("SHORT_MIN", (long)Short.MIN_VALUE);
        put("SHORT_MAX", (long)Short.MAX_VALUE);
        put("INT_MIN", (long)Integer.MIN_VALUE);
        put("INT_MAX", (long)Integer.MAX_VALUE);
        put("LONG_MIN", Long.MIN_VALUE);
        put("LONG_MAX", Long.MAX_VALUE);
    }};
    public static final Map<String,Double> doubleKeys = new HashMap<>(){{
        put("FLOAT_MIN", (double) Float.MIN_VALUE);
        put("FLOAT_MAX", (double) Float.MAX_VALUE);
        put("DOUBLE_MIN", Double.MIN_VALUE);
        put("DOUBLE_MAX", Double.MAX_VALUE);
    }};

    private static final CompType[][] varTypeClasses = new CompType[][]{
            {
                    BOOL,CHAR,BYTE,SHORT,INT,LONG
            },
            
            {
                    FLOAT,DOUBLE
            }
    };

    public static Object getDefaultValue(CompType type) {
        return switch (type) {
            case BOOL -> false;
            case BYTE -> (byte) 0;
            case SHORT -> (short) 0;
            case INT -> 0;
            case LONG -> 0L;
            case FLOAT -> 0f;
            case DOUBLE -> 0.0;
            case STRING -> "\"\"";
            default -> '\u0000';
        };
    }

    public Variable(T value) {
        this.value = value;
    }

    public Variable<?> add(Variable<?> other){
        CompType thisType = getVarType();
        CompType otherType = other.getVarType();

        if(thisType.equals(STRING) || otherType.equals(STRING))
            return new Variable<>(String.valueOf(value) + other.value);

        return castNumToAppropriate(getDoubleValue(this) + getDoubleValue(other), thisType, otherType);
    }

    public Variable<?> sub(Variable<?> other){
        CompType thisType = getVarType();
        CompType otherType = other.getVarType();

        if(thisType.equals(STRING) && (otherType.equals(STRING) || otherType.equals(CHAR)))
            return new Variable<>((String.valueOf(value)).replaceFirst(String.valueOf(other.value) ,""));

        if(thisType.equals(STRING))
            throw new RuntimeError(201);

        return castNumToAppropriate(getDoubleValue(this) - getDoubleValue(other), thisType, otherType);
    }

    public Variable<?> mult(Variable<?> other){
        CompType thisType = this.getVarType();
        CompType otherType = other.getVarType();

        if(thisType.equals(STRING) && otherType.equals(STRING))
            throw new RuntimeError(202);
        
        if(thisType.equals(STRING) || otherType.equals(STRING)){
            StringBuilder answerString = stringMultiplication(other, thisType);

            return new Variable<>(answerString.toString());
        }

        return castNumToAppropriate(getDoubleValue(this) * getDoubleValue(other), thisType, otherType);
    }

    public Variable<?> div(Variable<?> other){
        CompType thisType = this.getVarType();
        CompType otherType = other.getVarType();

        if(thisType.equals(STRING) && (otherType.equals(STRING) || otherType.equals(CHAR)))
            return new Variable<>((String.valueOf(value)).replaceAll(String.valueOf(other.value) ,""));

        return castNumToAppropriate(getDoubleValue(this) / getDoubleValue(other), thisType, otherType);
    }
    public Variable<?> mod(Variable<?> other){
        CompType thisType = this.getVarType();
        CompType otherType = other.getVarType();
        
        if(thisType.equals(STRING) && (otherType.equals(STRING) || otherType.equals(CHAR)))
            return replaceLast((Variable<String>) this, (Variable<String>)other);

        return castNumToAppropriate(getDoubleValue(this) % getDoubleValue(other), thisType, otherType);
    }

    public Variable<?> pow(Variable<?> other){
        CompType thisType = this.getVarType();
        CompType otherType = other.getVarType();

        if(thisType.equals(STRING) && (otherType.equals(STRING) || otherType.equals(CHAR)))
            throw new RuntimeError(14);

        return castNumToAppropriate(Math.pow(getDoubleValue(this), getDoubleValue(other)), thisType, otherType);
    }

    public Variable<Boolean> binaries(Variable<?> other, CompType type){
        CompType thisType = this.getVarType();
        CompType otherType = other.getVarType();

        if(thisType.equals(STRING) && (otherType.equals(STRING) || otherType.equals(CHAR)))
            throw new RuntimeError(15);

        return switch (type){
            case AND -> new Variable<>(getDoubleValue(this) != 0.0 && getDoubleValue(other) != 0.0);
            case OR -> new Variable<>(getDoubleValue(this) != 0.0 || getDoubleValue(other) != 0.0);
            case EQ -> new Variable<>(getDoubleValue(this) == getDoubleValue(other));
            case NEQ -> new Variable<>(getDoubleValue(this) != getDoubleValue(other));
            case GRE -> new Variable<>(getDoubleValue(this) > getDoubleValue(other));
            case LE -> new Variable<>(getDoubleValue(this) < getDoubleValue(other));
            case GEQ -> new Variable<>(getDoubleValue(this) >= getDoubleValue(other));
            default -> new Variable<>(getDoubleValue(this) <= getDoubleValue(other));
        };
    }

    private StringBuilder stringMultiplication(Variable<?> other, CompType thisType) {
        String str;
        long cnt;

        if(thisType.equals(STRING)){
            str = String.valueOf(value);
            cnt = Long.parseLong(String.valueOf(other.value));
        }
        else{
            str = String.valueOf(other.value);
            cnt = Long.parseLong(String.valueOf(value));
        }

        StringBuilder answerString = new StringBuilder();

        for(int i = 0; i < cnt; i++)
            answerString.append(str);
        return answerString;
    }

    public static Variable<String> replaceLast(Variable<String> a, Variable<String> b) {
        if (a == null || b == null || b.value.isEmpty()) {
            return a; // Return original string if inputs are invalid
        }
        int lastIndex = a.value.lastIndexOf(b.value);
        if (lastIndex == -1) {
            return a; // Return original string if 'b' is not found
        }
        return new Variable<>(a.value.substring(0, lastIndex) + a.value.substring(lastIndex + b.value.length()));
    }

    private static double getDoubleValue(Variable<?> a){
        return a.getVarType() == BOOL ? (String.valueOf(a.value).equals("true") ? 1 : 0) : Double.parseDouble(String.valueOf(a.value));
    }

    private static Variable<?> castNumToAppropriate(Double num, CompType a, CompType b){
        CompType dominantType = minCompType(a,b);
        return switch (dominantType){
            case BOOL -> new Variable<>(num.longValue() != 0);
            case CHAR -> new Variable<>((char)num.longValue());
            case BYTE -> new Variable<>(num.byteValue());
            case SHORT -> new Variable<>(num.shortValue());
            case INT -> new Variable<>(num.intValue());
            case FLOAT -> new Variable<>(num.longValue());
            default -> new Variable<>(num);
        };
    }

    private CompType getVarType(){
        if(value instanceof Boolean)
            return BOOL;
        if(value instanceof Character)
            return CHAR;
        if(value instanceof Byte)
            return BYTE;
        if(value instanceof Short)
            return SHORT;
        if(value instanceof Integer)
            return INT;
        if(value instanceof Long)
            return FLOAT;
        if(value instanceof Float)
            return FLOAT;
        if(value instanceof Double)
            return DOUBLE;

        return STRING;
    }

    public static CompType minCompType(CompType a, CompType b){
        CompType answer = BOOL;

        if(a == STRING || b == STRING)
            return STRING;

        if(Arrays.stream(varTypeClasses[1]).toList().contains(a) ||
                Arrays.stream(varTypeClasses[1]).toList().contains(b)){
            if(a == DOUBLE || b == DOUBLE)
                return DOUBLE;
            return FLOAT;
        }
        for(CompType type : varTypeClasses[0])
            if(type == a || type == b)
                answer = type;

        return answer;
    }

    public static long strToLong(String str){
        if(boolKeys.containsKey(str))
            return boolKeys.get(str)? 1L : 0L;
        if(longKeys.containsKey(str))
            return longKeys.get(str);
        if(doubleKeys.containsKey(str))
            return doubleKeys.get(str).longValue();
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            try {
                return Double.valueOf(str).longValue();
            } catch (NumberFormatException ex) {
                throw new RuntimeError(205);//CODE205
            }
        }
    }

    public static double strToDouble(String str){
        if(boolKeys.containsKey(str))
            return boolKeys.get(str)? 1 : 0;
        if(longKeys.containsKey(str))
            return longKeys.get(str).doubleValue();
        if(doubleKeys.containsKey(str))
            return doubleKeys.get(str);
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            throw new RuntimeError(206);//CODE206
        }
    }

    public static boolean strToBool(String str){
        return strToLong(str) == 1;
    }
}
