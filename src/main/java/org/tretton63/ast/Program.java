package org.tretton63.ast;

import java.util.List;

public class Program implements Node {
    private List<Statement> statementList;

    public List<Statement> statements() {
        return statementList;
    }

    public void add(Statement statement) {
        statementList.add(statement);
    }
}
