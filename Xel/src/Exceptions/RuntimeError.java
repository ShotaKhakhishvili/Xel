package Exceptions;

import java.util.HashMap;
import java.util.Map;

public class RuntimeError extends RuntimeException {
    private static final Map<Integer,String> errors = new HashMap<>(){{
        put(201, "Can't Subtract String From Non-String [Error Code 201]");
        put(202, "Can't Multiply String With Another String [Error Code 202]");
        put(203, "Division Operation By 0 [Error Code 203]");
        put(204, "Modulo Operation By 0 [Error Code 204]");
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
