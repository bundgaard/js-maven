package org.tretton63.parser;

import org.tretton63.ast.Expression;
import org.tretton63.lexer.Token;
import org.tretton63.parser.Parser;

public interface PrefixParser {
    Expression apply(Parser parser, Token token);
}
