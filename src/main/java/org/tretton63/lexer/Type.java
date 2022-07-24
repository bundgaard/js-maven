package org.tretton63.lexer;

public enum Type {
    EOF, Illegal, Identifier, Literal, String, OpenParen, CloseParen, OpenBracket, CloseBracket, OpenCurly, CloseCurly, Plus, Minus, Multiply, Divide, Percentage, Quote, SQuote,
    CommentLine, CommentBlock, Equal, Semi, Colon, Dot, Comma, Number, Var, Function, Null, Undefined, True, False;
}
