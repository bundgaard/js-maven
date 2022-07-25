package org.tretton63.ast;

import org.tretton63.lexer.Token;

import java.util.HashMap;
import java.util.Map;

public class HashLiteral implements Expression {
    private final Token token;
    private Map<Expression, Expression> pairs = new HashMap<>();

    public HashLiteral(Token token) {
        this.token = token;
    }

    public void setPairs(Map<Expression, Expression> newPairs) {
        this.pairs = newPairs;
    }

    public void add(Expression newKey, Expression newValue) {
        pairs.put(newKey, newValue);
    }
}
