package Compilation;

import java.util.HashMap;
import java.util.Map;

public class Memory {
    class Variable<T>{
        T value;
//        public Object getValue(){
//
//            return new Object();
//        }
        public Variable add(Variable other){

            return new Variable<>();
        }
        public Variable sub(Variable other){

            return new Variable<>();
        }
        public Variable mult(Variable other){

            return new Variable<>();
        }
        public Variable div(Variable other){

            return new Variable<>();
        }
        public Variable mod(Variable other){

            return new Variable<>();
        }
    }
    class Functions<T>{
        T value;
//        public Object getValue(){
//
//            return new Object();
//        }
    }

    private static Map<String,Variable> variables = new HashMap<>();
    private static Map<String,Functions> functions = new HashMap<>();

    public static Map<String, Variable> getVariables() {
        return variables;
    }

    public static Map<String, Functions> getFunctions() {
        return functions;
    }
}
