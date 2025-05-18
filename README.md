# MicroJava-analisis-lexico-y-sintatico-
Se realizo el proyecto de crear un compilador que incluya el analisis lexico y sintatico dentro de una gramatica de microjava
La gramatica que se utilizo a tomar en cuenta es la siguiente:

/*
    GRAMATICA
    
    Program ::= "class" Identifier (VarDeclaration)* "{" (Statement)* "}" <EOF>
    VarDeclaration ::= Type Identifier ";"
    Type ::= "boolean" | "int"
    Statement ::= "{" (Statement)* "}"
              | "while" "(" Expression ")" Statement
              | "System.out.println" "(" Expression ")" ";"
              | Identifier "=" Expression ";"
    Expression ::= Identifier (("<" | "+" | "-" | "*") Identifier)?
             | "true" | "false" | Identifier | Integer
    Identifier ::= letter (letter | digit)*
    Integer ::= digit+
    
     */
