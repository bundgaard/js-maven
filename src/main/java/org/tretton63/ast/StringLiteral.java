package org.tretton63.ast;

import org.tretton63.lexer.Token;

public class StringLiteral extends AbstractExpression {
    public StringLiteral(Token token, String value) {
        super(token, value);
    }
}
