package GUICompilador;

import Parser.Parser;
import Parser.Scanner;
import Parser.tipoID;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.MatteBorder;

public class windowgui extends JFrame implements ActionListener, WindowListener {

    private static final long serialVersionUID = 1L;

    private JToolBar tbHerramientas;
    private JButton btnAbrir, btnLimpiar, btnCompilar;

    private JTextArea taCodigo;
    private JTextArea taScanner;
    private JTextArea taParser;

    public windowgui() {
        super("Compilador");
        setLayout(new BorderLayout());
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addWindowListener(this);
        initComponents();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        tbHerramientas = new JToolBar();
        tbHerramientas.setFloatable(false);

        btnAbrir = new JButton("Abrir archivo");
        btnAbrir.addActionListener(this);

        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(this);

        btnCompilar = new JButton("Compilar");
        btnCompilar.addActionListener(this);

        tbHerramientas.add(btnAbrir);
        tbHerramientas.add(Box.createRigidArea(new Dimension(10, 0)));
        tbHerramientas.add(btnLimpiar);
        tbHerramientas.add(Box.createRigidArea(new Dimension(10, 0)));
        tbHerramientas.add(btnCompilar);

        add(tbHerramientas, BorderLayout.NORTH);

        taCodigo = new JTextArea();
        taCodigo.setFont(new Font("Monospaced", Font.PLAIN, 16));
        taCodigo.setLineWrap(true);
        taCodigo.setWrapStyleWord(true);
        JScrollPane spCodigo = new JScrollPane(taCodigo);
        spCodigo.setBorder(BorderFactory.createTitledBorder(
                new MatteBorder(1, 1, 1, 1, Color.GRAY), "Código"));

        taScanner = new JTextArea();
        taScanner.setEditable(false);
        taScanner.setFont(new Font("Monospaced", Font.PLAIN, 14));
        taScanner.setLineWrap(true);
        taScanner.setWrapStyleWord(true);
        JScrollPane spScanner = new JScrollPane(taScanner);
        spScanner.setBorder(BorderFactory.createTitledBorder(
                new MatteBorder(1, 1, 1, 1, Color.GRAY), "Scanner"));

        taParser = new JTextArea();
        taParser.setEditable(false);
        taParser.setFont(new Font("Monospaced", Font.PLAIN, 14));
        taParser.setLineWrap(true);
        taParser.setWrapStyleWord(true);
        JScrollPane spParser = new JScrollPane(taParser);
        spParser.setBorder(BorderFactory.createTitledBorder(
                new MatteBorder(1, 1, 1, 1, Color.GRAY), "Parser"));

        JSplitPane splitScannerParser = new JSplitPane(JSplitPane.VERTICAL_SPLIT, spScanner, spParser);
        splitScannerParser.setResizeWeight(0.6);
        splitScannerParser.setBorder(null);
        splitScannerParser.setPreferredSize(new Dimension(300, 0));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, spCodigo, splitScannerParser);
        split.setResizeWeight(0.4);
        add(split, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAbrir) {
            abrirArchivo();
        } else if (e.getSource() == btnLimpiar) {
            taCodigo.setText("");
            taScanner.setText("");
            taParser.setText("");
        } else if (e.getSource() == btnCompilar) {
            compilarCodigo();
        }
    }

    private void abrirArchivo() {
        taScanner.setText("");
        taParser.setText("");

        FileDialog fd = new FileDialog((Frame) null, "Selecciona archivo", FileDialog.LOAD);
        fd.setVisible(true);
        String dir = fd.getDirectory();
        String file = fd.getFile();
        if (dir != null && file != null) {
            File f = new File(dir, file);
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                taCodigo.setText("");
                String line;
                while ((line = br.readLine()) != null) {
                    taCodigo.append(line + "\n");
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error lectura", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void compilarCodigo() {
        try {
            File tmp = File.createTempFile("fuente", ".tmp");
            tmp.deleteOnExit();
            try (PrintWriter pw = new PrintWriter(tmp)) {
                pw.print(taCodigo.getText());
            }

            String salida = ejecutarAnalisis(tmp);

            boolean parteSintactica = false;
            taScanner.setText("");
            taParser.setText("");
            for (String linea : salida.split("\\n")) {
                if (linea.startsWith("--- Analisis sintactico exitoso")
                        || linea.toLowerCase().contains("error sintactico")) {
                    parteSintactica = true;
                }
                if (!parteSintactica) {
                    taScanner.append(linea + "\n");
                } else {
                    taParser.append(linea + "\n");
                }
            }

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error del archivo temp", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static String ejecutarAnalisis(File file) {
        StringBuilder salida = new StringBuilder();
        try {
            // Análisis léxico
            Scanner scanner1 = new Scanner(file);
            tipoID token;
            salida.append("--- Tokens encontrados ---\n");
            do {
                token = scanner1.getToken();
                if (token != tipoID.EOF) {
                    salida.append("Token: ")
                            .append(Scanner.getNombreToken(token))
                            .append(" : '").append(scanner1.getLexema()).append("'")
                            .append(" | Linea: ").append(scanner1.getNumeroDeLinea()).append("\n");
                }
            } while (token != tipoID.EOF);
            salida.append("\n--- Analisis lexico exitoso ---\n\n");

            // Análisis sintáctico
            Scanner scanner2 = new Scanner(file);
            Parser parser = new Parser(scanner2);
            parser.parseProgram();
            salida.append("--- Analisis sintactico exitoso ---\n");

        } catch (Exception e) {
            salida.append(" ").append(e.getMessage()).append("\n");
        }
        return salida.toString();
    }

    // Métodos vacíos de WindowListener
    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        dispose();
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
}
