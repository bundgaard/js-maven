package org.tretton63.lexer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.nio.charset.StandardCharsets;

public class Source {

    private char currentChar;
    public static final char EOL = '\n';
    public static final char EOF = 0;

    private final PushbackInputStream pis;

    public Source(String input) {
        pis = new PushbackInputStream(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
    }

    public char peekChar() throws IOException {
        var peek = (char) pis.read();
        pis.unread(peek);
        return peek;
    }

    public void nextChar() throws IOException {
        var ch = pis.read();
        if (ch == -1) {
            currentChar = EOF;
        } else {
            currentChar = (char) ch;
        }
    }

    public char currentChar() {
        return currentChar;
    }

    public void unread(int ch) throws IOException {
        pis.unread(ch);
    }

}
