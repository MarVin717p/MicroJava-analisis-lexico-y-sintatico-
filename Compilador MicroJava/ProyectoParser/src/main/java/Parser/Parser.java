package Parser;

import java.io.IOException;

public class Parser {

    private final Scanner scanner;
    private tipoID tokenActual;

    public Parser(Scanner scanner) throws IOException {
        this.scanner = scanner;
        this.tokenActual = scanner.getToken(); // Primer token
    }

    private void error(String mensaje) {
        throw new RuntimeException("Error sintactico en linea " + scanner.getNumeroDeLinea() + ": " + mensaje);
    }

    private void eat(tipoID esperado) throws IOException {
        if (tokenActual == esperado) {
            tokenActual = scanner.getToken(); // avanzar al siguiente token
        } else {
            error("Se esperaba el token '" + esperado + "', pero se encontro '" + tokenActual + "'.");
        }
    }

    // Program ::= "class" Identifier (VarDeclaration)* "{" (Statement)* "}" <EOF>
    public void parseProgram() throws IOException {
        eat(tipoID.CLASS);
        eat(tipoID.IDENTIFICADOR);
        parseVarDeclaration();

        if (tokenActual != tipoID.LLAVE_ABRE) {
            error("Se esperaba una llave que abre '{' o una declaracion de variable.");
        }
        eat(tipoID.LLAVE_ABRE);
        parseStatement();
        eat(tipoID.LLAVE_CIERRA);
        eat(tipoID.EOF);
    }

    // VarDeclaration ::= Type Identifier ";"
    private void parseVarDeclaration() throws IOException {
        while (tokenActual == tipoID.BOOLEAN || tokenActual == tipoID.INT) {
            parseType();
            eat(tipoID.IDENTIFICADOR);
            eat(tipoID.PUNTOYCOMA);
        }
    }

    // Type ::= "boolean" | "int"
    private void parseType() throws IOException {
        if (tokenActual == tipoID.BOOLEAN || tokenActual == tipoID.INT) {
            tokenActual = scanner.getToken();
        } else {
            error("Tipo de dato invalido.");
        }
    }

    /*
     Statement ::= "{" (Statement)* "}"
                | "while" "(" Expression ")" Statement
                | "System.out.println" "(" Expression ")" ";"
                | Identifier "=" Expression ";"
     */
    private void parseStatement() throws IOException {
        if (tokenActual == tipoID.INT || tokenActual == tipoID.BOOLEAN) {
            error(tokenActual + " encontrado. No se pueden declarar variables dentro de un statement.");
        }

        while (tokenActual == tipoID.LLAVE_ABRE || tokenActual == tipoID.WHILE
                || tokenActual == tipoID.SOUT || tokenActual == tipoID.IDENTIFICADOR) {

            switch (tokenActual) {
                case LLAVE_ABRE:
                    eat(tipoID.LLAVE_ABRE);
                    while (tokenActual == tipoID.LLAVE_ABRE || tokenActual == tipoID.WHILE
                            || tokenActual == tipoID.SOUT || tokenActual == tipoID.IDENTIFICADOR) {
                        parseStatement();
                    }
                    eat(tipoID.LLAVE_CIERRA);
                    break;

                case WHILE:
                    eat(tipoID.WHILE);
                    eat(tipoID.PARENTESIS_ABRE);
                    if (tokenActual == tipoID.PARENTESIS_CIERRA) {
                        error("Se esperaba una expresion dentro de los parentesis del 'while'.");
                    }
                    parseExpression();
                    eat(tipoID.PARENTESIS_CIERRA);
                    parseStatement();
                    break;

                case SOUT:
                    eat(tipoID.SOUT);
                    eat(tipoID.PARENTESIS_ABRE);
                    if (tokenActual == tipoID.PARENTESIS_CIERRA) {
                        error("Se esperaba una expresion dentro de los parentesis del 'System.out.println'.");
                    }
                    parseExpression();
                    eat(tipoID.PARENTESIS_CIERRA);
                    eat(tipoID.PUNTOYCOMA);
                    break;

                case IDENTIFICADOR:
                    eat(tipoID.IDENTIFICADOR);
                    eat(tipoID.IGUAL);
                    parseExpression();
                    eat(tipoID.PUNTOYCOMA);
                    break;

                default:
                    error("Token inesperado: '" + tokenActual + "'.");
            }
        }
    }

    /*
     Expression ::= Identifier (("<" | "+" | "-" | "*") Identifier)?
                  | "true" | "false" | Integer
     */
    private void parseExpression() throws IOException {
        switch (tokenActual) {
            case IDENTIFICADOR:
                eat(tipoID.IDENTIFICADOR);
                if (tokenActual == tipoID.MENOR_QUE || tokenActual == tipoID.MAS
                        || tokenActual == tipoID.GUION || tokenActual == tipoID.ASTERISCO) {

                    tipoID operador = tokenActual;
                    eat(operador);

                    if (tokenActual == tipoID.IDENTIFICADOR) {
                        eat(tipoID.IDENTIFICADOR);
                    } else {
                        error("El operador '" + operador + "' debe ir seguido de un identificador.");
                    }
                }
                break;

            case TRUE:
                eat(tipoID.TRUE);
                break;

            case FALSE:
                eat(tipoID.FALSE);
                break;

            case NUM_ENTERO:
                eat(tipoID.NUM_ENTERO);
                break;

            default:
                error("Expresion no valida con el token '" + tokenActual + "'.");
        }
    }
}
