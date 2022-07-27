package org.tretton63.eval;

import org.junit.jupiter.api.Test;
import org.tretton63.ast.Identifier;
import org.tretton63.lexer.Token;
import org.tretton63.lexer.Type;
import org.tretton63.parser.Parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EvaluatorTest {

    @Test
    void testEvaluatorIdentifier() {
        var evaler = new Evaluator();
        var env = new Evaluator.Environment();
        env.put("x", new NumberObject(100L));
        evaler.setEnvironment(env);
        var result = evaler.eval(
                new Identifier(new Token("x", Type.Identifier), "x"));
        if (result instanceof JSError err) {
            System.out.println(err);
        } else if (result instanceof NumberObject number) {
            System.out.println(number);
            assertEquals(100L, number.getValue());

        }
    }


    @Test
    void testProgram() {
        var evaluator = new Evaluator();
        var parser = new Parser("var x = 100;");
        var environment = new Evaluator.Environment();
        var result = evaluator.eval(parser.parse());
        System.out.println("Environment");
        environment.forEach((key, value) -> {
            System.out.println(key + " " + value);
        });
        System.out.println(result);
    }

    @Test
    void testInfix() {
        var evaluator = new Evaluator();

        var parser = new Parser("var a = 1 + 1;\nvar b = 2 * 2;\nvar c = 100 % 10;");
        var result = evaluator.eval(parser.parse());
        var environment = evaluator.getEnvironment();
        System.out.println(result);
        System.out.println("=".repeat(80));
        System.out.println(environment);
    }

    @Test
    void testArray() {
        var evaluator = new Evaluator();

        var parser = new Parser("var a = [1,2,3,4,5, \"Foo\"];");
        var result = evaluator.eval(parser.parse());
        var environment = evaluator.getEnvironment();
        System.out.println(result);
        System.out.println("=".repeat(80));
        System.out.println(environment);
    }

}