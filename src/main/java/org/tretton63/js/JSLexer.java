package org.tretton63.js;

import org.tretton63.lexer.AbstractLexer;
import org.tretton63.lexer.Source;
import org.tretton63.lexer.Token;
import org.tretton63.lexer.Type;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JSLexer extends AbstractLexer {


    private static final Map<String, Type> KEYWORDS = new HashMap<>();

    public static final Token EOF = new Token("<eof>", Type.EOF);
    static {
        KEYWORDS.put("var", Type.Var);
        KEYWORDS.put("function", Type.Function);
        KEYWORDS.put("null", Type.Null);
        KEYWORDS.put("undefined", Type.Undefined);
        KEYWORDS.put("true", Type.True);
        KEYWORDS.put("false", Type.False);
        KEYWORDS.put("return", Type.Return);
    }

    public JSLexer(String input) {
        super(input);
    }

    @Override
    public Token nextToken() {
        try {
            nextChar();
            while (Character.isWhitespace(currentChar())) {
                nextChar();
            }
            if (currentChar() == '/' && (peekChar() == '/' || peekChar() == '*')) {
                return commentToken();
            } else if (isMathOperator(currentChar())) {
                return mathToken(currentChar());
            } else if (currentChar() == '=') {
                return new Token(String.valueOf(currentChar()), Type.Equal);
            } else if (currentChar() == ';') {
                return new Token(";", Type.Semi);
            } else if (currentChar() == ':') {
                return new Token(String.valueOf(currentChar()), Type.Colon);
            } else if (currentChar() == '.') {
                return new Token(String.valueOf(currentChar()), Type.Dot);
            } else if (currentChar() == ',') {
                return new Token(String.valueOf(currentChar()), Type.Comma);
            } else if (currentChar() == '"' || currentChar() == '\'') {
                return stringToken();
            } else if (currentChar() == '(') {
                return new Token(String.valueOf(currentChar()), Type.OpenParen);
            } else if (currentChar() == ')') {
                return new Token(String.valueOf(currentChar()), Type.CloseParen);
            } else if (currentChar() == '[') {
                return new Token(String.valueOf(currentChar()), Type.OpenBracket);
            } else if (currentChar() == ']') {
                return new Token(String.valueOf(currentChar()), Type.CloseBracket);
            } else if (currentChar() == '{') {
                return new Token(String.valueOf(currentChar()), Type.OpenCurly);
            } else if (currentChar() == '}') {
                return new Token(String.valueOf(currentChar()), Type.CloseCurly);
            } else if (Character.isLetter(currentChar())) {
                var name = readName();
                var type = KEYWORDS.getOrDefault(name, Type.Identifier);
                return new Token(name, type);
            } else if (Character.isDigit(currentChar())) {
                var value = readNumber();
                return new Token(value, Type.Number);
            } else {
                if (currentChar() == Source.EOF) {
                    return EOF;
                }
                return new Token(String.valueOf(currentChar()), Type.Illegal);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private Token commentToken() throws IOException {
        var value = new StringBuilder();
        var peek = peekChar();
        var block = false;

        if (peek == '/') {
            while (!(currentChar() == Source.EOL || currentChar() == Source.EOF)) {
                value.append(currentChar());
                nextChar(); // EAT /
            }
        } else if (peek == '*') {
            block = true;
            while (true) {
                if (currentChar() == '*' && peekChar() == '/') {
                    value.append(currentChar());
                    nextChar();
                    value.append(currentChar());
                    break;
                }
                value.append(currentChar());
                nextChar();
            }
        }
        return new Token(value.toString(), block ? Type.CommentBlock : Type.CommentLine);
    }

    private String readName() throws IOException {
        var out = new StringBuilder();

        while (Character.isLetterOrDigit(currentChar())) {
            out.append(currentChar());
            nextChar();
        }
        unread(currentChar());
        return out.toString();
    }


    private Token stringToken() throws IOException {
        var out = new StringBuilder();
        var quote = currentChar();
        var escaped = false;
        while (true) {
            out.append(currentChar());
            if (!escaped && peekChar() == quote) {
                nextChar();
                out.append(currentChar());
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

    private String readNumber() throws IOException {
        var out = new StringBuilder();
        while (Character.isDigit(currentChar())) {
            out.append(currentChar());
            nextChar();
        }
        unread(currentChar());
        return out.toString();
    }


    ///////////////////////////////////


}
