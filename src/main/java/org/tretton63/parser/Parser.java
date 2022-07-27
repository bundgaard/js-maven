package org.tretton63.parser;

import org.tretton63.ast.*;
import org.tretton63.lexer.Lexer;
import org.tretton63.lexer.Token;
import org.tretton63.lexer.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        prefixParserMap.put(OpenBracket, new ArrayParselet());

        infixParserMap.put(Plus, new ParseInfixExpression());
        infixParserMap.put(Minus, new ParseInfixExpression());
        infixParserMap.put(Equal, new ParseInfixExpression());
        infixParserMap.put(Multiply, new ParseInfixExpression());
        infixParserMap.put(Divide, new ParseInfixExpression());
        infixParserMap.put(Percentage, new ParseInfixExpression());

        // IndexExpression and CallExpression

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
        System.out.println("Added " + p.statements().size() + " statement(s)");
        System.out.println(p);
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
        System.out.println("1. parseVariable current=" + current);
        var varToken = current;
        if (!expectPeek(Type.Identifier)) {
            return null;
        }

        System.out.println("2. parseVariable current=" + current);
        var name = new Identifier(current, current.value());
        if (!expectPeek(Equal)) {
            System.out.println("2a. return null" + expectPeek(Equal) + " " + next);
            return null;
        }
        nextToken();
        System.out.println("3. parseVariable current=" + current);
        var value = parseExpression(Lowest);
        if (peekTokenIs(Type.Semi)) {
            nextToken();
        }

        System.out.println(varToken);
        System.out.println(name);
        System.out.println(value);
        return new VariableStatement(varToken, name, value);

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
            System.out.println("next " + next.type());
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

    public class ParseInfixExpression implements InfixParser {

        @Override
        public Expression apply(Parser parser, Expression left, Token token) {
            var expression = new InfixExpression(current, left, current.value());
            var curPrecedence = currentPrecedence();
            nextToken();
            expression.setRight(parseExpression(curPrecedence));
            return expression;
        }
    }
    private List<Expression> parseExpressionList(Type end) {
        var list = new ArrayList<Expression>();
        if (peekTokenIs(end)) {
            nextToken();
            return list;
        }

        nextToken();
        list.add(parseExpression(Lowest));

        while(peekTokenIs(Comma)) {
            nextToken(); // Eat Comma
            nextToken(); // Expression
            list.add(parseExpression(Lowest));
        }

        if (!expectPeek(end)) {
            return null;
        }
        return list;
    }

    public class ArrayParselet implements PrefixParser{

        @Override
        public Expression apply(Parser parser, Token token) {
            var array = new ArrayLiteral(token);
            array.setElements(parseExpressionList(CloseBracket));
            return array;
        }
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
