package org.tretton63.lexer;

import java.io.IOException;

public abstract class AbstractLexer {

    private final Source source;

    public AbstractLexer(String input) {
        this.source = new Source(input);
    }

    protected char peekChar() throws IOException {
        return source.peekChar();
    }

    protected void nextChar() throws IOException {
        source.nextChar();
    }

    protected void unread(int ch) throws IOException {
        source.unread(ch);
    }

    protected char currentChar() {
        return source.currentChar();
    }

    public abstract Token nextToken();
}
