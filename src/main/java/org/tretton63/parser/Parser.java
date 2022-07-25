package org.tretton63.parser;

import org.tretton63.ast.*;
import org.tretton63.lexer.Lexer;
import org.tretton63.lexer.Token;
import org.tretton63.lexer.Type;

import java.util.HashMap;
import java.util.Map;

import static org.tretton63.ast.Priority.Lowest;
import static org.tretton63.lexer.Type.*;

public class Parser {
    private final Lexer lexer;
    private Token current;
    private Token next;

    private final Map<Type, PrefixParser> prefixParserMap = new HashMap<>();
    private final Map<Type, InfixParser> infixParserMap = new HashMap<>();

    public Parser(String input) {
        lexer = new Lexer(input);

        prefixParserMap.put(Identifier, new NameParselet());
        prefixParserMap.put(String, new StringParselet());
        prefixParserMap.put(Number, new NumberParselet());
        prefixParserMap.put(OpenCurly, new HashParselet());

        nextToken();
        nextToken();
    }

    public Program parse() {
        var p = new Program();
        while (current.type() != Type.EOF) {
            var statement = parseStatement();
            if (statement != null) {
                p.add(statement);
            }
            nextToken();
        }
        return p;
    }

    private Statement parseStatement() {
        return switch (current.type()) {
            case Var -> parseVariable();
            case CommentBlock, CommentLine -> null;
            default -> parseExpressionStatement();
        };
    }

    private Statement parseVariable() {

       // var variableStatement = new VariableStatement(current);
        return null;
    }

    private ExpressionStatement parseExpressionStatement() {
        var statement = new ExpressionStatement(current);
        statement.setExpression(parseExpression(Lowest));

        if (peekTokenIs(Dot)) {
            nextToken();
            var right = parseInfixExpression(statement.getExpression());
            statement.setExpression(right);
        }

        if (peekTokenIs(Equal)) {
            nextToken();
            var right = parseInfixExpression(statement.getExpression());
            statement.setExpression(right);
        }

        if (peekTokenIs(Semi)) {
            nextToken();
        }
        return statement;
    }

    private Expression parseExpression(Priority priority) {
        if (peekTokenIs(CommentLine) || peekTokenIs(CommentBlock)) {
            nextToken();
        }
        var prefix = prefixParserMap.get(current.type());
        if (prefix == null) {
            return null;
        }

        var left = prefix.apply(this, current);
        while (!peekTokenIs(Semi) && priority.compareTo(peekPrecedence()) < 0) {
            var infix = infixParserMap.get(next.type());
            if (infix == null) {
                return left;
            }
            nextToken();
            left = infix.apply(this, left, current);
        }
        return left;
    }

    private Priority peekPrecedence() {
        return Priority.PRECEDENCES.getOrDefault(next.type(), Lowest);
    }

    private Expression parseInfixExpression(Expression left) {
        var expression = new InfixExpression(current, left, current.value());
        var precedence = currentPrecedence();
        nextToken();
        expression.setRight(parseExpression(precedence));
        return expression;
    }

    private Priority currentPrecedence() {
        return Priority.PRECEDENCES.getOrDefault(current.type(), Lowest);
    }

    private void nextToken() {
        current = next;
        next = lexer.nextToken();
    }

    private boolean peekTokenIs(Type token) {
        return next.type() == token;
    }

    private boolean expectPeek(Type token) {
        if (peekTokenIs(token)) {
            nextToken();
            return true;
        }
        return false;
    }

    public class HashParselet implements PrefixParser {
        @Override
        public Expression apply(Parser parser, Token token) {
            var hash = new HashLiteral(token);
            while (!peekTokenIs(CloseCurly)) {
                nextToken(); // eat open curly
                var key = parseExpression(Lowest);
                if (!expectPeek(Colon)) {
                    return null;
                }
                nextToken();
                var value = parseExpression(Lowest);
                hash.add(key, value);
                if (!peekTokenIs(CloseCurly) && !expectPeek(Comma)) {
                    return null;
                }
            }
            return hash;
        }
    }

    public static class NameParselet implements PrefixParser {

        @Override
        public Expression apply(Parser parser, Token token) {
            return new Identifier(token, token.value());
        }
    }

    public static class NumberParselet implements PrefixParser {
        @Override
        public Expression apply(Parser parser, Token token) {
            return new NumberLiteral(token, token.value());
        }
    }

    public static class StringParselet implements PrefixParser {
        @Override
        public Expression apply(Parser parser, Token token) {
            return new StringLiteral(token, token.value());
        }
    }
}
