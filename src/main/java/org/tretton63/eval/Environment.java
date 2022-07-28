package org.tretton63.eval;

import org.tretton63.obj.JSObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class Environment {
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
