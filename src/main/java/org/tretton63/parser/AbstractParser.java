package org.tretton63.parser;

import org.tretton63.ast.Priority;
import org.tretton63.lexer.AbstractLexer;
import org.tretton63.lexer.Token;
import org.tretton63.lexer.Type;

import static org.tretton63.ast.Priority.Lowest;

public class AbstractParser {

    private Token current;
    private Token next;

    private AbstractLexer lexer;

    public AbstractParser(AbstractLexer lexer) {
        this.lexer = lexer;
    }

    protected Token current() {
        return current;
    }

    protected Token next() {
        return next;
    }

    protected Priority currentPrecedence() {
        return Priority.PRECEDENCES.getOrDefault(current.type(), Lowest);
    }

    protected void nextToken() {
        current = next;
        next = lexer.nextToken();
    }

    protected boolean peekTokenIs(Type token) {
        return next.type() == token;
    }

    protected boolean expectPeek(Type token) {
        if (peekTokenIs(token)) {
            nextToken();
            return true;
        }
        return false;
    }

    protected boolean currentTokenIs(Type token) {
        return current.type() == token;
    }

    protected Priority peekPrecedence() {
        return Priority.PRECEDENCES.getOrDefault(next.type(), Lowest);
    }


}
