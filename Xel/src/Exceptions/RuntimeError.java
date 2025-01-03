package Exceptions;

import java.util.HashMap;
import java.util.Map;

public class RuntimeError extends RuntimeException {
    private static final Map<Integer,String> errors = new HashMap<>(){{
        put(201, "Can't Subtract String From Non-String [Error Code 201]");
        put(202, "Can't Multiply String With Another String [Error Code 202]");
        put(203, "Division Operation By 0 [Error Code 203]");
        put(204, "Modulo Operation By 0 [Error Code 204]");
        put(205, "Invalid Integer Number Formating [Error Code 205]");
        put(206, "Invalid Decimal Number Formating [Error Code 206]");
        put(207,"Illegal Expression. Can't Apply Binary Operations On Strings [Error Code 207]");
        put(208,"Unable To Cast A String Value [Error Code 208]");
        put(209,"Illegal Expression. Can't Pow Apply Operation On Strings [Error Code 209]");
        put(210,"You Can't Set A String Value Of Size More Than 1 To A Character [Error Code 210]");
    }};
    public RuntimeError(String message, int line) {
        super("[Line " + line + "] " + message);
    }
    public RuntimeError(String message) {
        super(message);
    }
    public RuntimeError(int errorNumber) {
        super(errors.get(errorNumber));
    }
}
