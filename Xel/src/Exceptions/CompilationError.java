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
        put(6,"Poor Bracket Sequence On Expression [Error Code 6]");
        put(7,"Illegal Operation Sequence On Expression [Error Code 7]");
        put(8,"Not Enough Parameters On Expression [Error Code 8]");
        put(9,"Unknown Argument On Expression [Error Code 9]");
        put(10,"Tried To Access An Unknown Variable[Error Code 10]");
        put(11,"Illegal Expression [Error Code 11]");
        put(12,"Unknown Assignment Operator [Error Code 12]");
        put(13,"Illegal Scope Closing Detected [Error Code 13]");
        put(14,"Not Enough Parameters For An Input Statement[Error Code 14]");
        put(15,"Invalid Input Statement [Error Code 15]");
        put(16,"Invalid Boolean Expression [Error Code 16]");
        put(17,"Extra Symbols On An Increment Operation [Error Code 17]");
        put(18,"Extra Symbols On A Decrement Operation [Error Code 18]");
        put(19,"Illegal Declaration [Error Code 19]");
        put(20,"Unfinished Initialization [Error Code 20]");
        put(21,"Statement Expected [Error Code 21]");
        put(22,"Else If Statement Without An If Statement [Error Code 22]");
        put(23,"Continue Statement Needs To Be Inside A Loop [Error Code 23]");
        put(24,"Break Statement Needs To Be Inside A Loop [Error Code 24]");
        put(25,"Invalid For Statement [Error Code 25]");
        put(26,"Brackets Were Expected [Error Code 26]");
        put(27,"Invalid Print Statement [Error Code 27]");
        put(28,"Invalid Square Bracket Sequence [Error Code 28]");
        put(29,"Empty Input Statement [Error Code 29]");
        put(30,"Invalid If Statement [Error Code 30]");
        put(31,"Invalid Else-If Statement [Error Code 31]");
        put(32,"Else Statement Without An If Statement [Error Code 32]");
        put(33,"Invalid Increment Inside Expression [Error Code 33]");
        put(34,"Invalid Decrement Inside Expression [Error Code 34]");
        put(35,"Tried To Reach Index Of Unknown Variable With Square Brackets [Error Code 35]");
        put(36,"Invalid Array Element [Error Code 36]");
        put(37,"Invalid Array Declaration [Error Code 37]");
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
