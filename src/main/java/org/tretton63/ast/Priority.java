package org.tretton63.ast;

import org.tretton63.lexer.Type;

import java.util.HashMap;
import java.util.Map;

public enum Priority {
    Lowest, Sum, Product, Index, Call;

    public static Map<Type, Priority> PRECEDENCES = new HashMap<>();
    static {
        PRECEDENCES.put(Type.Plus, Sum);
        PRECEDENCES.put(Type.Minus, Sum);
        PRECEDENCES.put(Type.Multiply, Product);
        PRECEDENCES.put(Type.Divide, Product);
        PRECEDENCES.put(Type.Percentage, Product);

    }
}
