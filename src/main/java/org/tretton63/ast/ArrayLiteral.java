package org.tretton63.ast;

import org.tretton63.lexer.Token;

import java.util.List;
import java.util.stream.Collectors;

public class ArrayLiteral implements Expression {
    private Token token;
    private List<Expression> elements;

    public ArrayLiteral(Token token) {
        this.token = token;
    }

    public void setElements(List<Expression> elements) {
        this.elements = elements;
    }

    public String toString() {
        return "[" + elements.stream().map(Expression::toString).collect(Collectors.joining(",")) + "]";
    }

    public List<Expression> elements() {
        return elements;
    }
}
