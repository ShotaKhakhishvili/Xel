package Compilation;

import java.util.HashMap;
import java.util.Map;

public class CompilationError extends Throwable {
    private static final Map<Integer,String> errors = new HashMap<>(){{
        put(0,"[Code 0] Illegal Character On Declaration");
        put(1,"[Code 1] Duplicate Variable Declaration");
        put(5,"[Code 5] Illegal Character On Expression");
    }};
    public CompilationError(String message) {
        super(message);
    }
    public CompilationError(int errorNumber) {
        super(errors.get(errorNumber));
    }
}
