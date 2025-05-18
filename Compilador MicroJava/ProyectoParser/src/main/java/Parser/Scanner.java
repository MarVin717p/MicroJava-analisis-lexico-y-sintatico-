package Parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Scanner {

    private BufferedReader entrada;
    private String lineaActual = null;
    private int posicionActualEnLinea = 0;
    private int numLinea = 1;
    private String lexemaActual = "";

    public Scanner(File file) throws IOException {
        this.entrada = new BufferedReader(new FileReader(file));
        this.lineaActual = entrada.readLine();
    }

    private void avanzaLinea() throws IOException {
        lineaActual = entrada.readLine();
        posicionActualEnLinea = 0;
        numLinea++;
    }

    private char verCaracter() {
        if (lineaActual == null) {
            return '\n';
        }
        if (posicionActualEnLinea < lineaActual.length()) {
            return lineaActual.charAt(posicionActualEnLinea);
        } else {
            return '\n';
        }
    }

    private char advance() {
        char charActual = verCaracter();
        if (lineaActual != null && posicionActualEnLinea < lineaActual.length()) {
            posicionActualEnLinea++;
        }
        return charActual;
    }

    private void ignorarEspacios() throws IOException {
        while (true) {
            if (lineaActual == null) {
                break;
            }

            while (posicionActualEnLinea < lineaActual.length() && Character.isWhitespace(verCaracter())) {
                posicionActualEnLinea++;
            }

            if (posicionActualEnLinea >= lineaActual.length()) {
                avanzaLinea();
            } else {
                break;
            }
        }
    }
    
    public tipoID getToken() throws IOException {
        ignorarEspacios();

        if (lineaActual == null) {
            if (entrada != null) {
                entrada.close();
                entrada = null;
            }
            lexemaActual = "";
            return tipoID.EOF;
        }

        tipoID tokenClave = encontrarTokenClave();
        if (tokenClave != null) {
            return tokenClave;
        }

        tipoID identificadorToken = encontrarIdentificador();
        if (identificadorToken != null) {
            return identificadorToken;
        }

        tipoID numeroEnteroToken = encontrarNumeroEntero();
        if (numeroEnteroToken != null) {
            return numeroEnteroToken;
        }

        tipoID porCaracter = tipoID.getPorCaracter(verCaracter());
        if (porCaracter != null) {
            lexemaActual = String.valueOf(verCaracter());
            advance();
            return porCaracter;
        }

        throw new RuntimeException("\n Error lexico: en la linea " + numLinea + ", token no reconocido");
    }

    private tipoID encontrarTokenClave() {
        int posInicial = posicionActualEnLinea;

        StringBuilder palabra = new StringBuilder();
        
        while (Character.isLetterOrDigit(verCaracter()) || verCaracter() == '.') {
            palabra.append(verCaracter());
            advance();
        }

        String str = palabra.toString();
        lexemaActual = str;

        switch (str) {
            case "class":
                return tipoID.CLASS;
            case "boolean":
                return tipoID.BOOLEAN;
            case "int":
                return tipoID.INT;
            case "while":
                return tipoID.WHILE;
            case "true":
                return tipoID.TRUE;
            case "false":
                return tipoID.FALSE;
            case "System.out.println":
                return tipoID.SOUT;
            default:
                posicionActualEnLinea = posInicial;
                return null;
        }

    }

    private tipoID encontrarIdentificador() {
        if (!Character.isLetter(verCaracter())) {
            return null;
        }

        StringBuilder palabra = new StringBuilder();
        while (Character.isLetterOrDigit(verCaracter())) {
            palabra.append(verCaracter());
            advance();
        }

        lexemaActual = palabra.toString();
        return tipoID.IDENTIFICADOR;
    }

    private tipoID encontrarNumeroEntero() {
        if (!Character.isDigit(verCaracter())) {
            return null;
        }

        StringBuilder numero = new StringBuilder();

        while (Character.isDigit(verCaracter())) {
            numero.append(verCaracter());
            advance();
        }

        lexemaActual = numero.toString();
        return tipoID.NUM_ENTERO;
    }

    

    public int getNumeroDeLinea() {
        return numLinea;
    }

    public String getLexema() {
        return lexemaActual;
    }

    public static String getNombreToken(tipoID tipo) {
        if (tipo != null) {
            return tipo.toString();
        } else {
            return "Token erroneo";
        }
    }

}
