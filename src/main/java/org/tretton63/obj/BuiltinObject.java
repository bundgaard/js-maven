package org.tretton63.obj;

public interface BuiltinObject extends JSObject {

    JSObject apply(JSObject... args);

}
