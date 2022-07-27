package org.tretton63.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Program implements Node {
    private final List<Statement> statementList = new ArrayList<>();

    public List<Statement> statements() {
        return statementList;
    }

    public void add(Statement statement) {
        statementList.add(statement);
    }

    public String toString() {
        return statementList.stream().map(Statement::toString).collect(Collectors.joining("\n"));
    }
}
