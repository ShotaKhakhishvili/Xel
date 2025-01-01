package Exceptions;

import java.util.HashMap;
import java.util.Map;

public class CompilationError extends Throwable {
    public static final Map<Integer,String> errors = new HashMap<>(){{
        put(404,"Invalid Instruction [Error Code 404]");

        put(0,"Illegal Character On Declaration [Error Code 0]");
        put(1,"Duplicate Variable Declaration [Error Code 1]");
        put(2,"Illegal Variable Name Declaration [Error Code 2]");
        put(5,"Illegal Character On Expression [Error Code 5]");
        put(6,"Poor Bracket Sequence On Arithmetic Expression [Error Code 6]");
        put(7,"Illegal Operation Sequence On Expression [Error Code 7]");
        put(8,"Not Enough Parameters On Arithmetic Expression [Error Code 8]");
        put(9,"Unknown Argument On Arithmetic Expression [Error Code 9]");
        put(10,"Tried To Access An Unknown Variable[Error Code 10]");
        put(11,"Illegal Expression [Error Code 11]");
        put(12,"Unknown Assignment Operator [Error Code 12]");
        put(13,"Illegal Scope Closing Detected [Error Code 13]");
        put(14,"Not Enough Parameters For An Input Statement[Error Code 14]");
        put(15,"Tried An Input Statement On An Undeclared Variable [Error Code 15]");
        put(16,"Invalid Boolean Expression [Error Code 16]");
        put(17,"Extra Symbols On An Increment Operation [Error Code 17]");
        put(18,"Extra Symbols On A Decrement Operation [Error Code 18]");
        put(19,"Illegal Declaration [Error Code 19]");
        put(20,"Unfinished Initialization [Error Code 20]");
    }};
    public CompilationError(String message, int line) {
        super("[Line " + line + "] " + message);
    }
    public CompilationError(String message) {
        super(message);
    }
    public CompilationError(int errorNumber) {
        super(errors.get(errorNumber));
    }
}
