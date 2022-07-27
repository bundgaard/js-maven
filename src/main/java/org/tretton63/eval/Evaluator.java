package org.tretton63.eval;

import jdk.security.jarsigner.JarSignerException;
import org.tretton63.ast.*;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class Evaluator {

    private final Environment scope = new Environment();

    private List<JSObject> evalExpressions(List<Expression> elements, Environment environment) {
        var result = new ArrayList<JSObject>();
        for(var element : elements) {
            var evaluated = eval(element, environment);
            if (evaluated instanceof JSError) {
                return List.of(evaluated);
            }
            result.add(evaluated);
        }
        return result;
    }
    public JSObject eval(Node program, Environment environment) {
        switch (program) {
            case ArrayLiteral array -> {
                var elements = evalExpressions(array.elements(), environment);
                if (elements.size() == 1) {
                    return elements.get(0);
                }
                return new ArrayObject(elements);
            }
            case VariableStatement variableStatement -> {
                var value = eval(variableStatement.getValue(), environment);
                if (value == null) {
                    System.err.println("variablestatement: " + variableStatement.getValue());
                }
                if (value instanceof JSError) {
                    return value;
                }

                environment.put(variableStatement.getName().getValue(), value);

            }
            case Program p -> {
                return evalProgram(p, environment);
            }
            case Identifier i -> {
                return evalIdentifier(i, environment);
            }
            case ExpressionStatement expressionStatement -> {
                return eval(expressionStatement.getExpression(), environment);
            }
            case InfixExpression infixExpression -> {
                var left = eval(infixExpression.getLeft(), environment);
                if (left instanceof JSError err) {
                    return left;
                }
                var right = eval(infixExpression.getRight(), environment);
                if (right instanceof JSError) {
                    return right;
                }

                return evalInfixEvaluator(infixExpression.getOperator(), left, right, environment);

            }

            case NumberLiteral number -> {
                return new NumberObject(Long.parseLong(number.getValue()));
            }
            case StringLiteral str -> {
                return new StringObject(str.getValue());
            }
            default -> {
                return new JSError("program " + program.getClass().getSimpleName());
            }

        }
        return null;
    }

    private JSObject evalInfixEvaluator(String operator, JSObject left, JSObject right, Environment environment) {
        if (left instanceof NumberObject && right instanceof NumberObject) {
            return evalNumberInfixExpression(operator, left, right, environment);
        } else if (left instanceof StringObject && right instanceof StringObject) {
            return evalStringInfixExpression(operator, left, right, environment);
        }
        return null;
    }

    private JSObject evalNumberInfixExpression(String operator, JSObject left, JSObject right, Environment environment) {
        if (left instanceof NumberObject leftNumber && right instanceof NumberObject rightNumber) {
            return switch (operator) {
                case "+" -> new NumberObject(leftNumber.getValue() + rightNumber.getValue());
                case "-" -> new NumberObject(leftNumber.getValue() - rightNumber.getValue());
                case "*" -> new NumberObject(leftNumber.getValue() * rightNumber.getValue());
                case "/" -> new NumberObject(leftNumber.getValue() / rightNumber.getValue());
                case "%" -> new NumberObject(leftNumber.getValue() % rightNumber.getValue());
                default -> throw new IllegalStateException("Unexpected value: " + operator);
            };
        }

        return null;
    }

    private JSObject evalStringInfixExpression(String operator, JSObject left, JSObject right, Environment environment) {

        return null;
    }

    private JSObject evalProgram(Program program, Environment environment) {
        var result = new JSObject();
        for (var statement : program.statements()) {
            result = eval(statement, environment);
            if (result instanceof JSError err) {
                System.err.println(err);
            } else if (result instanceof ReturnValue retValue) {
                return retValue.getValue();
            }
        }
        return result;
    }

    private JSObject evalIdentifier(Identifier n, Environment environment) {
        var identifier = environment.get(n.getValue());
        if (identifier != null) {
            return identifier;
        }

        return new JSError("identifier " + n.getValue() + " not found!");
    }

    public static class Environment {
        private final Map<String, JSObject> store;
        private final Environment outer;

        private Environment(Environment outer) {
            store = new HashMap<>();
            this.outer = outer;
        }

        public Environment() {
            this(null);
        }

        public JSObject get(String name) {
            return store.get(name);
        }

        public void put(String name, JSObject object) {
            store.put(name, object);
        }

        public static Environment withOuter(Environment outer) {
            return new Environment(outer);
        }

        public void forEach(BiConsumer<String, JSObject> fn) {
            store.forEach(fn);
        }

        public String toString() {
            return store.entrySet().stream().map((entry) -> String.format("%s -> %s", entry.getKey(), entry.getValue())).collect(Collectors.joining("\n"));
        }
    }
}
