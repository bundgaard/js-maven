package org.tretton63.ast;

import org.tretton63.lexer.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BlockStatement implements Statement {
    private final Token token;

    private final List<Statement> statements;

    public BlockStatement(Token token) {
        this.token = token;
        statements = new ArrayList<>();
    }

    public String toString() {
        return "{\n" + statements.stream().map(Statement::toString).collect(Collectors.joining()) + "\n}";
    }

    public void addStatement(Statement newStatement) {
        statements.add(newStatement);
    }

}
