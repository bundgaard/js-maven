package org.tretton63.lexer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Lexer {

    private char currentChar = 0;
    private final PushbackInputStream pis;
    private static final Map<String, Type> KEYWORDS = new HashMap<>();

    static {
        KEYWORDS.put("var", Type.Var);
        KEYWORDS.put("function", Type.Function);
        KEYWORDS.put("null", Type.Null);
        KEYWORDS.put("undefined", Type.Undefined);
        KEYWORDS.put("true", Type.True);
        KEYWORDS.put("false", Type.False);
    }

    public Lexer(String input) {
        this(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
    }

    public Lexer(InputStream in) {
        pis = new PushbackInputStream(in);
    }

    public Token nextToken() {
        try {
            nextChar();
            while (Character.isWhitespace(currentChar)) {
                nextChar();
            }
            if (isMathOperator(currentChar)) {
                return mathToken(currentChar);
            } else if (currentChar == '=') {
                return new Token(String.valueOf(currentChar), Type.Equal);
            } else if (currentChar == ':') {
                return new Token(String.valueOf(currentChar), Type.Colon);
            } else if (currentChar == '.') {
                return new Token(String.valueOf(currentChar), Type.Dot);
            } else if (currentChar == ',') {
                return new Token(String.valueOf(currentChar), Type.Semi);
            } else if (currentChar == '"' || currentChar == '\'') {
                return stringToken();
            } else if (currentChar == '(') {
                return new Token(String.valueOf(currentChar), Type.OpenParen);
            } else if (currentChar == ')') {
                return new Token(String.valueOf(currentChar), Type.CloseParen);
            } else if (currentChar == '[') {
                return new Token(String.valueOf(currentChar), Type.OpenBracket);
            } else if (currentChar == ']') {
                return new Token(String.valueOf(currentChar), Type.CloseBracket);
            } else if (currentChar == '{') {
                return new Token(String.valueOf(currentChar), Type.OpenCurly);
            } else if (currentChar == '}') {
                return new Token(String.valueOf(currentChar), Type.CloseCurly);
            } else if (Character.isLetter(currentChar)) {
                var name = readName();
                var type = KEYWORDS.getOrDefault(name, Type.Identifier);
                return new Token(name, type);
            } else if (Character.isDigit(currentChar)) {
                var value = readNumber();
                return new Token(value, Type.Number);
            } else {
                if (currentChar == 0) {
                    return new Token("eof", Type.EOF);
                }
                return new Token(String.valueOf(currentChar), Type.Illegal);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String readName() throws IOException {
        var out = new StringBuilder();

        while(Character.isLetterOrDigit(currentChar)) {
            out.append(currentChar);
            nextChar();
        }
        return out.toString();
    }

    private char peekChar() throws IOException {
        var peek = (char) pis.read();
        pis.unread(peek);
        return peek;
    }

    private Token stringToken() throws IOException {
        var out = new StringBuilder();
        var quote = currentChar;
        var escaped = false;
        while (true) {
            out.append(currentChar);
            if (!escaped && peekChar() == quote) {
                nextChar();
                out.append(currentChar);
                break;
            }
            nextChar();
        }

        return new Token(out.toString(), Type.String);
    }

    private boolean isMathOperator(char ch) {
        return switch (ch) {
            case '-', '+', '*', '/', '%' -> true;
            default -> false;
        };
    }

    private Token mathToken(char ch) {
        return switch (ch) {
            case '-' -> new Token(String.valueOf(ch), Type.Minus);
            case '*' -> new Token(String.valueOf(ch), Type.Multiply);
            case '/' -> new Token(String.valueOf(ch), Type.Divide);
            case '+' -> new Token(String.valueOf(ch), Type.Plus);
            case '%' -> new Token(String.valueOf(ch), Type.Percentage);
            default -> new Token(String.valueOf(ch), Type.Illegal);
        };
    }

    private void nextChar() throws IOException {
        var ch = pis.read();
        if (ch==-1) {
            currentChar = 0;
        } else {
            currentChar = (char) ch;
        }

    }

    private String readNumber() throws IOException {

        var out = new StringBuilder();
        while (Character.isDigit(currentChar)) {
            out.append(currentChar);
            nextChar();
        }
        pis.unread(currentChar);
        return out.toString();
    }

}
