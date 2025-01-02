package Compilation;

public enum CompType {
    PROG,       // Program
    SCOPES,     // Scope Start
    SCOPEE,     // Scope End

    DECL,       // Declaration
    ASGM,       // Assignment
    PRINT,      // Print
    INPUT,      // Input
    IF,         // If
    ELSE,       // Else
    ELIF,       // Else If
    WHILE,      // While loop
    FOR,        // For loop
    BRK,        // Break
    CNT,        // Continue
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


    BOOL,CHAR,BYTE,SHORT,INT,LONG,FLOAT,DOUBLE,STRING,    // Specific Variable Types

    LIT,        // Literal
    VAR,        // Variable

    AND,OR,GRE,LE,EQ,NEQ,GEQ,LEQ,NOT,  // Boolean Expressions
    ADD,SUB,MULT,DIV,MOD,POW,   // Used for expressions and for assignments
    INC,DEC,                    // Increment/Decrement fo assignments
    PREINC, POSINC, PREDEC, POSDEC,         // Pre/Post Increment/Decrements
    INVALID,
}


