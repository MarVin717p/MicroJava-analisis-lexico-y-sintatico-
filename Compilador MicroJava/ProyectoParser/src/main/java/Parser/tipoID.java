package Parser;

public enum tipoID {
    CLASS,
    LLAVE_ABRE, 
    LLAVE_CIERRA,
    PUNTOYCOMA,
    PARENTESIS_ABRE, 
    PARENTESIS_CIERRA,
    BOOLEAN,
    INT,
    WHILE,
    IGUAL,
    MENOR_QUE,
    MAS,
    GUION,
    ASTERISCO,
    SOUT,
    TRUE, FALSE,
    IDENTIFICADOR,
    NUM_ENTERO,
    EOF;

    public static tipoID getPorCaracter(char c) {
        return switch (c) {
            case '{' -> LLAVE_ABRE;
            case '}' -> LLAVE_CIERRA;
            case '(' -> PARENTESIS_ABRE;
            case ')' -> PARENTESIS_CIERRA;
            case '<' -> MENOR_QUE;
            case '+' -> MAS;
            case '-' -> GUION;
            case '*' -> ASTERISCO;
            case ';' -> PUNTOYCOMA;
            case '=' -> IGUAL;
            default -> null;
        };
    }
}
