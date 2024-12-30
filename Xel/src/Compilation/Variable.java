package Compilation;

import java.util.Arrays;

import static Compilation.CompType.*;

public class Variable<T>{
    public T value;

    private static CompType[][] varTypeClassess = new CompType[][]{
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
            case STRING -> "";
            default -> '\u0000';
        };
    }

    public Variable(T value) {
        this.value = value;
    }

    public Variable add(Variable other){
        CompType thisType = this.getVarType();
        CompType otherType = other.getVarType();

        if(thisType.equals(STRING) || otherType.equals(STRING))
            return new Variable<>(String.valueOf(value) + other.value);

        return castNumToAppropriate(getDoubleValue(this) + getDoubleValue(other), thisType, otherType);
    }

    public Variable sub(Variable other){
        CompType thisType = this.getVarType();
        CompType otherType = other.getVarType();

        if(thisType.equals(STRING) && (otherType.equals(STRING) || otherType.equals(CHAR)))
            return new Variable<>((String.valueOf(value)).replaceFirst(String.valueOf(other.value) ,""));

        if(otherType.equals(STRING))
            throw new RuntimeError(201);

        return castNumToAppropriate(getDoubleValue(this) - getDoubleValue(other), thisType, otherType);
    }

    public Variable mult(Variable other){
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

    public Variable div(Variable other){
        CompType thisType = this.getVarType();
        CompType otherType = other.getVarType();

        if(thisType.equals(STRING) && (otherType.equals(STRING) || otherType.equals(CHAR)))
            return new Variable<>((String.valueOf(value)).replaceAll(String.valueOf(other.value) ,""));

        return castNumToAppropriate(getDoubleValue(this) / getDoubleValue(other), thisType, otherType);
    }
    public Variable mod(Variable other){
        System.out.println(value + " " + other.value);
        CompType thisType = this.getVarType();
        CompType otherType = other.getVarType();
        
        if(thisType.equals(STRING) && (otherType.equals(STRING) || otherType.equals(CHAR)))
            return replaceLast((Variable<String>) this, (Variable<String>)other);

        return castNumToAppropriate(getDoubleValue(this) % getDoubleValue(other), thisType, otherType);
    }

    private StringBuilder stringMultiplication(Variable other, CompType thisType) {
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
        return new Variable<String>(a.value.substring(0, lastIndex) + a.value.substring(lastIndex + b.value.length()));
    }

    private static double getDoubleValue(Variable a){
        return a.getVarType() == BOOL ? (String.valueOf(a.value).equals("true") ? 1 : 0) : Double.parseDouble(String.valueOf(a.value));
    }

    private static Variable castNumToAppropriate(Double num, CompType a, CompType b){
        CompType dominantType = minCompType(a,b);
        switch (dominantType){
            case BOOL:
                return new Variable(num.longValue() != 0);
            case CHAR:
                return new Variable((char)num.longValue());
            case BYTE:
                return new Variable(num.byteValue());
            case SHORT:
                return new Variable(num.shortValue());
            case INT:
                return new Variable(num.intValue());
            case FLOAT:
                return new Variable(num.longValue());
            default:
                return new Variable<>(num);
        }
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

        if(Arrays.stream(varTypeClassess[1]).toList().contains(a) ||
                Arrays.stream(varTypeClassess[1]).toList().contains(b)){
            if(a == DOUBLE || b == DOUBLE)
                return DOUBLE;
            return FLOAT;
        }
        for(CompType type : varTypeClassess[0])
            if(type == a || type == b)
                answer = type;

        return answer;
    }
}
