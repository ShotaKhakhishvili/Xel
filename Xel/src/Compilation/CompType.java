package Compilation;

public enum CompType {
    PROG,       // Program
    SCOPE,      // Scope

    DECL,       // Declaration
    INIT,       // Initialization
    ASGM,       // Assignment
    IF,         // If
    ELSE,       // Else
    ELIF,       // Else If
    WHILE,      // While loop
    FOR,        // For loop
    FCALL,      // Function Call
    FDEC,       // Function Declaration

    NAME,       // Variable Name
    EXP,        // Expression
    UOP,        // Unary Operations             {-}
    BIOP,       // Binary Operations            {-, +, *, /, %}
    COND,       // Condition                    {true, false}
    COMP,       // Compartment                  {>, <, >=, <=, ==, !=}
    LUNOP,      // Logical Unary Operations     {!}
    LINOP,      // Logical Binary Operations    {&&, ||}

    VTYPE,      // Any Variable Type
    BYTE,SHORT,INT,LONG,FLOAT,DOUBLE,STRING,CHAR,    // Specific Variable Types

    INVALID
}


