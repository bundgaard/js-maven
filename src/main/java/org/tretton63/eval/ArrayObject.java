package org.tretton63.eval;

import java.util.List;
import java.util.stream.Collectors;

public class ArrayObject extends JSObject {
    private final List<JSObject> elements;

    public ArrayObject(List<JSObject> elements) {
        this.elements = elements;
    }

    public String toString() {
        return "[" + elements.stream()
                .map(JSObject::toString)
                .collect(Collectors.joining(", ")) +
                "]";
    }
}
