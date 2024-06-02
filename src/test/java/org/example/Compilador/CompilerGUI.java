package org.example.Compilador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CompilerGUI extends JFrame {
    private JTextArea codeTextArea;
    private JTextArea resultTextArea;
    private JComboBox<String> languageComboBox;
    private SymbolTable symbolTable;
    private ErrorManager errorManager;

    public CompilerGUI() {
        setTitle("Compilador");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        symbolTable = new SymbolTable();
        errorManager = new ErrorManager();

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel(new BorderLayout());
        JLabel codeLabel = new JLabel("Ingrese el código:");
        codeTextArea = new JTextArea(20, 40);
        JScrollPane codeScrollPane = new JScrollPane(codeTextArea);
        inputPanel.add(codeLabel, BorderLayout.NORTH);
        inputPanel.add(codeScrollPane, BorderLayout.CENTER);

        JPanel resultPanel = new JPanel(new BorderLayout());
        JLabel resultLabel = new JLabel("Resultado del análisis:");
        resultTextArea = new JTextArea(20, 40);
        JScrollPane resultScrollPane = new JScrollPane(resultTextArea);
        resultTextArea.setEditable(false); // Hacer el área de texto de resultados no editable
        resultPanel.add(resultLabel, BorderLayout.NORTH);
        resultPanel.add(resultScrollPane, BorderLayout.CENTER);

        languageComboBox = new JComboBox<>(new String[]{"PL/SQL", "T-SQL", "C++", "Pascal", "JavaScript", "HTML", "Python"});
        JPanel languagePanel = new JPanel();
        languagePanel.add(new JLabel("Seleccione el lenguaje:"));
        languagePanel.add(languageComboBox);

        JButton analyzeButton = new JButton("Analizar");
        analyzeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                analyzeCode();
            }
        });

        JButton executeButton = new JButton("Ejecutar");
        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeCode();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(analyzeButton);
        buttonPanel.add(executeButton);

        mainPanel.add(inputPanel, BorderLayout.WEST);
        mainPanel.add(resultPanel, BorderLayout.EAST);
        mainPanel.add(languagePanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void analyzeCode() {
        String code = codeTextArea.getText();
        String language = (String) languageComboBox.getSelectedItem();
        resultTextArea.setText("");

        errorManager = new ErrorManager();  // Reiniciar errores antes de análisis

        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(code, language, errorManager);
        SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(lexicalAnalyzer, errorManager);
        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(symbolTable, errorManager);

        lexicalAnalyzer.analyze();
        syntaxAnalyzer.analyze();
        semanticAnalyzer.analyze();

        if (!errorManager.hasErrors()) {
            resultTextArea.append("El código no contiene errores.\n\n");
            resultTextArea.append("Tokens:\n");
            for (Token token : lexicalAnalyzer.getTokens()) {
                resultTextArea.append(token.toString() + "\n");
            }
        } else {
            resultTextArea.append("Se encontraron errores en el código:\n");
            for (Error error : errorManager.getErrors()) {
                resultTextArea.append(error.toString() + "\n");
            }
        }
    }

    private void executeCode() {
        String code = codeTextArea.getText();
        String language = (String) languageComboBox.getSelectedItem();
        resultTextArea.setText("");

        String result = CodeExecutor.executeCode(code, language);
        resultTextArea.setText(result);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CompilerGUI().setVisible(true);
            }
        });
    }
}

class LexicalAnalyzer {
    private String code;
    private List<Token> tokens;
    private ErrorManager errorManager;
    private String language;

    public LexicalAnalyzer(String code, String language, ErrorManager errorManager) {
        this.code = code;
        this.tokens = new ArrayList<>();
        this.errorManager = errorManager;
        this.language = language;
    }

    public void analyze() {
        String[] words = code.split("\\s+");
        for (String word : words) {
            TokenType type = getTokenType(word);
            if (type == null) {
                errorManager.addError(new Error("Token no reconocido: " + word, getLineNumber(word)));
            } else {
                tokens.add(new Token(word, type));
            }
        }
    }

    private TokenType getTokenType(String word) {
        switch (language) {
            case "PL/SQL":
                return getPLSQLTokenType(word);
            case "T-SQL":
                return getTSQLTokenType(word);
            case "C++":
                return getCppTokenType(word);
            case "Pascal":
                return getPascalTokenType(word);
            case "JavaScript":
                return getJavaScriptTokenType(word);
            case "HTML":
                return getHTMLTokenType(word);
            case "Python":
                return getPythonTokenType(word);
            default:
                return null;
        }
    }

    private TokenType getPLSQLTokenType(String word) {
        if (word.matches("(?i)begin|end|declare|function|procedure")) {
            return TokenType.RESERVED_WORD;
        } else if (word.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
            return TokenType.VARIABLE;
        } else if (word.matches("[0-9]+")) {
            return TokenType.CONSTANT;
        } else {
            return null;
        }
    }

    private TokenType getTSQLTokenType(String word) {
        if (word.matches("(?i)select|insert|update|delete|from|where")) {
            return TokenType.RESERVED_WORD;
        } else if (word.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
            return TokenType.VARIABLE;
        } else if (word.matches("[0-9]+")) {
            return TokenType.CONSTANT;
        } else {
            return null;
        }
    }

    private TokenType getCppTokenType(String word) {
        if (word.matches("(?i)int|float|double|if|else|while|for")) {
            return TokenType.RESERVED_WORD;
        } else if (word.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
            return TokenType.VARIABLE;
        } else if (word.matches("[0-9]+")) {
            return TokenType.CONSTANT;
        } else {
            return null;
        }
    }

    private TokenType getPascalTokenType(String word) {
        if (word.matches("(?i)begin|end|var|program|procedure|function")) {
            return TokenType.RESERVED_WORD;
        } else if (word.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
            return TokenType.VARIABLE;
        } else if (word.matches("[0-9]+")) {
            return TokenType.CONSTANT;
        } else {
            return null;
        }
    }

    private TokenType getJavaScriptTokenType(String word) {
        if (word.matches("(?i)var|let|const|function|if|else|while|for")) {
            return TokenType.RESERVED_WORD;
        } else if (word.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
            return TokenType.VARIABLE;
        } else if (word.matches("[0-9]+")) {
            return TokenType.CONSTANT;
        } else {
            return null;
        }
    }

    private TokenType getHTMLTokenType(String word) {
        if (word.matches("</?[a-zA-Z]+>")) {
            return TokenType.RESERVED_WORD;
        } else if (word.matches("[a-zA-Z]+")) {
            return TokenType.VARIABLE;
        } else {
            return null;
        }
    }

    private TokenType getPythonTokenType(String word) {
        if (word.matches("(?i)def|class|if|else|elif|while|for|import|from|as")) {
            return TokenType.RESERVED_WORD;
        } else if (word.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
            return TokenType.VARIABLE;
        } else if (word.matches("[0-9]+")) {
            return TokenType.CONSTANT;
        } else {
            return null;
        }
    }

    private int getLineNumber(String word) {
        String[] lines = code.split("\n");
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].contains(word)) {
                return i + 1;
            }
        }
        return 0;
    }

    public List<Token> getTokens() {
        return tokens;
    }
}

class SyntaxAnalyzer {
    private LexicalAnalyzer lexicalAnalyzer;
    private ErrorManager errorManager;

    public SyntaxAnalyzer(LexicalAnalyzer lexicalAnalyzer, ErrorManager errorManager) {
        this.lexicalAnalyzer = lexicalAnalyzer;
        this.errorManager = errorManager;
    }

    public void analyze() {
        // Implementación básica: Verificar si hay un token que no sea una variable o una constante
        List<Token> tokens = lexicalAnalyzer.getTokens();
        for (Token token : tokens) {
            if (token.getType() != TokenType.VARIABLE && token.getType() != TokenType.CONSTANT && token.getType() != TokenType.RESERVED_WORD) {
                errorManager.addError(new Error("Error sintáctico: token inesperado " + token.getLexeme(), 0)); // 0 debería ser la línea real
            }
        }
    }
}

class SemanticAnalyzer {
    private SymbolTable symbolTable;
    private ErrorManager errorManager;

    public SemanticAnalyzer(SymbolTable symbolTable, ErrorManager errorManager) {
        this.symbolTable = symbolTable;
        this.errorManager = errorManager;
    }

    public void analyze() {
        // Implementación básica: Verificar si todas las variables están definidas
        for (Token token : symbolTable.getAllTokens()) {
            if (token.getType() == TokenType.VARIABLE && !symbolTable.isDefined(token.getLexeme())) {
                errorManager.addError(new Error("Error semántico: variable no definida " + token.getLexeme(), 0)); // 0 debería ser la línea real
            }
        }
    }
}

class Token {
    private String lexeme;
    private TokenType type;

    public Token(String lexeme, TokenType type) {
        this.lexeme = lexeme;
        this.type = type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public TokenType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Lexeme: " + lexeme + ", Type: " + type;
    }
}

enum TokenType {
    RESERVED_WORD, LOGICAL_EXPRESSION, MATHEMATICAL_EXPRESSION, VARIABLE, CONSTANT,
    FUNCTION, CLASS, LOOP, CONDITIONAL, CRUD
}

class SymbolTable {
    private List<Token> tokens;

    public SymbolTable() {
        tokens = new ArrayList<>();
    }

    public void addToken(Token token) {
        tokens.add(token);
    }

    public List<Token> getAllTokens() {
        return tokens;
    }

    public boolean isDefined(String lexeme) {
        for (Token token : tokens) {
            if (token.getLexeme().equals(lexeme)) {
                return true;
            }
        }
        return false;
    }
}

class ErrorManager {
    private List<Error> errors;

    public ErrorManager() {
        errors = new ArrayList<>();
    }

    public void addError(Error error) {
        errors.add(error);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public List<Error> getErrors() {
        return errors;
    }
}

class Error {
    private String message;
    private int line;

    public Error(String message, int line) {
        this.message = message;
        this.line = line;
    }

    @Override
    public String toString() {
        return "Error en línea " + line + ": " + message;
    }
}

class CodeExecutor {
    public static String executeCode(String code, String language) {
        try {
            switch (language) {
                case "Python":
                    return executePython(code);
                case "JavaScript":
                    return executeJavaScript(code);
                case "C++":
                    return executeCpp(code);
                case "Pascal":
                    return executePascal(code);
                // Agrega los casos para otros lenguajes aquí
                default:
                    return "Lenguaje no soportado.";
            }
        } catch (Exception e) {
            return "Error al ejecutar el código: " + e.getMessage();
        }
    }

    private static String executePython(String code) throws IOException, InterruptedException {
        String filename = "temp.py";
        writeToFile(filename, code);
        return runCommand("python " + filename);
    }

    private static String executeJavaScript(String code) throws IOException, InterruptedException {
        String filename = "temp.js";
        writeToFile(filename, code);
        return runCommand("node " + filename);
    }

    private static String executeCpp(String code) throws IOException, InterruptedException {
        String filename = "temp.cpp";
        writeToFile(filename, code);
        runCommand("g++ " + filename + " -o temp");
        return runCommand("./temp");
    }

    private static String executePascal(String code) throws IOException, InterruptedException {
        String filename = "temp.pas";
        writeToFile(filename, code);
        runCommand("fpc " + filename);
        return runCommand("./temp");
    }

    // Métodos de ayuda para escribir a un archivo y ejecutar un comando
    private static void writeToFile(String filename, String content) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        writer.write(content);
        writer.close();
    }

    private static String runCommand(String command) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(command);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
        int exitVal = process.waitFor();
        if (exitVal != 0) {
            return "Error: " + output.toString();
        }
        return output.toString();
    }
}