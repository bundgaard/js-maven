package org.tretton63.eval;

import org.tretton63.ast.*;
import org.tretton63.obj.*;

import java.util.*;

public class Evaluator {

    private Environment environment = new Environment();
    private static final Map<String, BuiltinObject> builtins = new HashMap<>();

    private static final NullObject nullobject = new NullObject();

    static {
        builtins.put("println", (args) -> {
            if (args.length == 1) {
                System.out.println(args[0]);
            }
            return new NullObject();
        });
        builtins.put("len", (args) -> {
            if (args.length != 1) {
                return new JSError("wrong number of arguments");
            }

            if (args[0] instanceof StringObject str) {
                return new NumberObject(str.getValue().length());
            } else {
                return new JSError("len is not supported");
            }
        });
    }

    public void setEnvironment(Environment newEnvironment) {
        this.environment = environment;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public JSObject eval(Node program) {
        return eval(program, environment);
    }

    public JSObject eval(Node program, Environment environment) {
        switch (program) {
            case ArrayLiteral array -> {
                var elements = evalExpressions(array.elements());
                if (elements.size() == 1) {
                    return elements.get(0);
                }
                return new ArrayObject(elements);
            }
            case VariableStatement variableStatement -> {
                var value = eval(variableStatement.getValue());
                if (value == null) {
                    System.err.println("variablestatement: " + variableStatement.getValue());
                }
                if (value instanceof JSError) {
                    return value;
                }

                environment.put(variableStatement.getName().getValue(), value);

            }
            case FunctionLiteral fn -> {
                var obj = new FunctionObject(fn.getToken(), fn.getName(), fn.getParameters(), fn.getBody(), environment);
                if (!Objects.equals(fn.getName(), "")) {
                    environment.put(fn.getName(), obj);
                }
                return obj;
            }
            case Program p -> {
                return evalProgram(p);
            }
            case Identifier i -> {
                return evalIdentifier(i);
            }
            case ExpressionStatement expressionStatement -> {
                return eval(expressionStatement.getExpression());
            }
            case CallExpression callExpression -> {
                var function = eval(callExpression.getFunction());
                if (function instanceof JSError err) {
                    return err;
                }
                var args = evalExpressions(callExpression.getArguments());
                if (args.size() == 1 && args.get(0) instanceof JSError err) {
                    return err;
                }
                return applyFunction(function, args);

            }
            case InfixExpression infixExpression -> {
                var left = eval(infixExpression.getLeft());
                if (left instanceof JSError err) {
                    return err;
                }

                var right = eval(infixExpression.getRight());
                if (right instanceof JSError err) {
                    return err;
                }

                return evalInfixEvaluator(infixExpression.getOperator(), left, right);
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
        return nullobject;
    }

    private JSObject applyFunction(JSObject function, List<JSObject> args) {
        if (function instanceof BuiltinObject fn) {
            return fn.apply(args.toArray(new JSObject[0]));
        } else if (function instanceof FunctionObject fn) {
            var extendedEnvironment = extendEnvironment(fn, args);
            var evaluated = eval(fn.getBody(), extendedEnvironment);
            return unwrapReturnValue(evaluated);
        }
        return new JSError("not a function " + function.getClass().getSimpleName());
    }

    private JSObject unwrapReturnValue(JSObject obj) {
        if (obj instanceof ReturnValue retValue) {
            return retValue.getValue();
        }
        return obj;
    }

    private Environment extendEnvironment(FunctionObject fn, List<JSObject> args) {
        var environ = Environment.withOuter(fn.getEnvironment());

        return environ;
    }

    private JSObject evalInfixEvaluator(String operator, JSObject left, JSObject right) {
        if (left instanceof NumberObject && right instanceof NumberObject) {
            return evalNumberInfixExpression(operator, left, right);
        } else if (left instanceof StringObject && right instanceof StringObject) {
            return evalStringInfixExpression(operator, left, right);
        }
        return nullobject;
    }

    private List<JSObject> evalExpressions(List<Expression> elements) {
        var result = new ArrayList<JSObject>();
        for (var element : elements) {
            var evaluated = eval(element);
            if (evaluated instanceof JSError) {
                return List.of(evaluated);
            }
            result.add(evaluated);
        }
        return result;
    }

    private JSObject evalNumberInfixExpression(String operator, JSObject left, JSObject right) {
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

        return nullobject;
    }

    private JSObject evalStringInfixExpression(String operator, JSObject left, JSObject right) {

        return nullobject;
    }

    private JSObject evalProgram(Program program) {
        JSObject result = nullobject;
        for (var statement : program.statements()) {
            result = eval(statement);
            if (result instanceof JSError err) {
                System.err.println(err);
            } else if (result instanceof ReturnValue retValue) {
                return retValue.getValue();
            }
        }
        return result;
    }

    private JSObject evalIdentifier(Identifier n) {
        var identifier = environment.get(n.getValue());
        if (identifier != null) {
            return identifier;
        }
        var builtin = builtins.get(n.getValue());
        if (builtin != null) {
            return builtin;
        }

        return new JSError("identifier " + n.getValue() + " not found!");
    }

}
