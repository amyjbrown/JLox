package com.craftinginterpreters.Lox;

import java.util.HashMap;
import java.util.Map;

class LoxInstance {
    private LoxClass klass;
    private final Map<String, Object> fields = new HashMap<>();

    LoxInstance(){}

    LoxInstance(LoxClass klass) {
        this.klass = klass;
    }

    Object get(Token name) {
        // Check if instance contains field x - if it does, return it
        if (fields.containsKey(name.lexeme)) {
            return fields.get(name.lexeme);
        }
        // If the reffered name isn't a local field, check the class to see if it contains the method
        // If it does, return it while binding 'this'
        LoxFunction method = klass.findMethod(name.lexeme);

        if (method != null) return method.bind(this);

        // if an instance property or a classes's methods cannot be found, throw a RunTime error
        throw new RuntimeError(name, "Undefined property '" + name.lexeme
        + "'.");

    }

    void set(Token name, Object value) {
        fields.put(name.lexeme, value);
    }

    @Override
    public String toString() {
        return klass.name + " instance";
    }

}
