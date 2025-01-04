package Compilation.DataTypes;

import Compilation.CompType;
import Exceptions.RuntimeError;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static Compilation.CompType.*;

public class Variable<T>{
    private T value;
    private final CompType type;

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
                    BOOL,BYTE,SHORT,INT,LONG,CHAR
            },
            
            {
                    FLOAT,DOUBLE
            }
    };

    public static Object getDefaultValue(CompType type) {
        return switch (type) {
            case BOOL -> false;
            case CHAR -> '?';
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

    public Variable(CompType type) {
        this.type = type;
    }

    public Variable(T value,CompType type) {
        this.type = type;
        this.value = value;
    }

    public CompType getType() {
        return type;
    }

    public void setValue(Object val) {
        String value = val.toString();

        if(val instanceof Character){
            if(type != STRING)
                value = String.valueOf(Long.valueOf((char) val));
        }
        else{
            if(type == CHAR){
                if(val instanceof String) {
                    if(((String) val).length() > 1)
                        throw new RuntimeError(210);
                    value = (String.valueOf((long) ((String) val).charAt(0)));
                }
                else
                    value = String.valueOf((long)val);
            }
        }
        switch (type){
            case BOOL -> this.value = (T)((Boolean)Variable.strToBool(value));
            case CHAR -> this.value = (T)Character.valueOf((char)Byte.parseByte(value));
            case BYTE -> this.value = (T)(Byte)(byte)Variable.strToLong(value);
            case SHORT -> this.value = (T)(Short)(short)Variable.strToLong(value);
            case INT -> this.value = (T)(Integer)(int)(Variable.strToLong(value));
            case LONG -> this.value = (T)(Long)Variable.strToLong(value);
            case FLOAT -> this.value = (T)(Float)(float)Variable.strToDouble(value);
            case DOUBLE -> this.value = (T)(Double)(Variable.strToDouble(value));
            case STRING -> this.value = (T)value;
        }
    }

    protected T valFromObject(Object val){
        String value = val.toString();

        if(val instanceof Character){
            if(type != STRING)
                value = String.valueOf(Long.valueOf((char) val));
        }
        else{
            if(type == CHAR){
                if(val instanceof String) {
                    if(((String) val).length() > 1)
                        throw new RuntimeError(210);
                    value = (String.valueOf((long) ((String) val).charAt(0)));
                }
                else
                    value = String.valueOf((long)val);
            }
        }
        return switch (type){
            case BOOL -> (T)((Boolean)Variable.strToBool(value));
            case CHAR -> (T)Character.valueOf((char)Byte.parseByte(value));
            case BYTE ->  (T)(Byte)(byte)Variable.strToLong(value);
            case SHORT -> (T)(Short)(short)Variable.strToLong(value);
            case INT -> (T)(Integer)(int)(Variable.strToLong(value));
            case LONG -> (T)(Long)Variable.strToLong(value);
            case FLOAT -> (T)(Float)(float)Variable.strToDouble(value);
            case DOUBLE -> (T)(Double)(Variable.strToDouble(value));
            default -> (T)value;
        };
    }

    public Variable<?> add(Variable<?> other){
        CompType thisType = getType();
        CompType otherType = other.getType();

        if(thisType.equals(STRING) || otherType.equals(STRING))
            return new Variable<>(value.toString() + other.value, STRING);

        return castNumToAppropriate(getDoubleValue(this) + getDoubleValue(other), thisType, otherType);
    }

    public Variable<?> sub(Variable<?> other){
        CompType thisType = getType();
        CompType otherType = other.getType();

        if(thisType.equals(STRING) && (otherType.equals(STRING) || otherType.equals(CHAR)))
            return new Variable<>((String.valueOf(value)).replaceFirst(String.valueOf(other.value) ,""), STRING);

        if(thisType.equals(STRING))
            throw new RuntimeError(201);

        return castNumToAppropriate(getDoubleValue(this) - getDoubleValue(other), thisType, otherType);
    }

    public Variable<?> mult(Variable<?> other){
        CompType thisType = this.getType();
        CompType otherType = other.getType();

        if(thisType.equals(STRING) && otherType.equals(STRING))
            throw new RuntimeError(202);
        
        if(thisType.equals(STRING) || otherType.equals(STRING)){
            StringBuilder answerString = stringMultiplication(other, thisType);

            return new Variable<>(answerString.toString(), STRING);
        }

        return castNumToAppropriate(getDoubleValue(this) * getDoubleValue(other), thisType, otherType);
    }

    public Variable<?> div(Variable<?> other){
        CompType thisType = this.getType();
        CompType otherType = other.getType();

        if(thisType.equals(STRING) && (otherType.equals(STRING) || otherType.equals(CHAR)))
            return new Variable<>((String.valueOf(value)).replaceAll(String.valueOf(other.value) ,""), STRING);

        return castNumToAppropriate(getDoubleValue(this) / getDoubleValue(other), thisType, otherType);
    }
    public Variable<?> mod(Variable<?> other){
        CompType thisType = this.getType();
        CompType otherType = other.getType();
        
        if(thisType.equals(STRING) && (otherType.equals(STRING) || otherType.equals(CHAR)))
            return replaceLast((Variable<String>) this, (Variable<String>)other);

        return castNumToAppropriate(getDoubleValue(this) % getDoubleValue(other), thisType, otherType);
    }

    public Variable<?> pow(Variable<?> other){
        CompType thisType = this.getType();
        CompType otherType = other.getType();

        if(thisType.equals(STRING) && (otherType.equals(STRING) || otherType.equals(CHAR)))
            throw new RuntimeError(14);

        return castNumToAppropriate(Math.pow(getDoubleValue(this), getDoubleValue(other)), thisType, otherType);
    }

    public Variable<?> binaries(Variable<?> other, CompType type){
        CompType thisType = this.getType();
        CompType otherType = other.getType();

        if(thisType.equals(STRING) && (otherType.equals(STRING) || otherType.equals(CHAR)))
            throw new RuntimeError(15);

        return switch (type){
            case AND -> new Variable<>(getDoubleValue(this) != 0.0 && getDoubleValue(other) != 0.0, BOOL);
            case OR -> new Variable<>(getDoubleValue(this) != 0.0 || getDoubleValue(other) != 0.0, BOOL);
            case EQ -> new Variable<>(getDoubleValue(this) == getDoubleValue(other), BOOL);
            case NEQ -> new Variable<>(getDoubleValue(this) != getDoubleValue(other), BOOL);
            case GRE -> new Variable<>(getDoubleValue(this) > getDoubleValue(other), BOOL);
            case LE -> new Variable<>(getDoubleValue(this) < getDoubleValue(other), BOOL);
            case GEQ -> new Variable<>(getDoubleValue(this) >= getDoubleValue(other), BOOL);
            default -> new Variable<>(getDoubleValue(this) <= getDoubleValue(other), BOOL);
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
        return new Variable<>(a.value.substring(0, lastIndex) + a.value.substring(lastIndex + b.value.length()), STRING);
    }

    private static double getDoubleValue(Variable<?> a){
        if(a.value instanceof Character)
            return Long.valueOf((Character)a.value);
        try{
            return a.getType() == BOOL ? (String.valueOf(a.value).equals("true") ? 1 : 0) : Double.parseDouble(String.valueOf(a.value));
        }catch (NumberFormatException e){
            if(a.value.toString().length() == 1)
                return a.value.toString().charAt(0);
            throw new RuntimeError(208);
        }
    }

    private static Variable<?> castNumToAppropriate(Double num, CompType a, CompType b){
        CompType dominantType = minCompType(a,b);
        return switch (dominantType){
            case BOOL -> new Variable<>(num.longValue() != 0, BOOL);
            case CHAR -> new Variable<>((char)num.longValue(), CHAR);
            case BYTE -> new Variable<>(num.byteValue(), BYTE);
            case SHORT -> new Variable<>(num.shortValue(), SHORT);
            case INT -> new Variable<>(num.intValue(), INT);
            case LONG -> new Variable<>(num.longValue(), LONG);
            case FLOAT -> new Variable<>(num.floatValue(), FLOAT);
            case DOUBLE -> new Variable<>(num, DOUBLE);
            default -> new Variable<>(num,STRING);
        };
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
        if(str.isEmpty()) return 0;
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
        if(str.isEmpty()) return 0;
        if(boolKeys.containsKey(str))
            return boolKeys.get(str)? 1 : 0;
        if(longKeys.containsKey(str))
            return longKeys.get(str).doubleValue();
        if(doubleKeys.containsKey(str))
            return doubleKeys.get(str);
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            if(str.length() == 1)
                return str.charAt(0);
            throw new RuntimeError(206);//CODE206
        }
    }

    public T getValue() {
        return value;
    }

    public static boolean strToBool(String str){
        return !str.isEmpty() && strToLong(str) == 1;
    }
}
