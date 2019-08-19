package com.craftinginterpreters.Lox;

import java.util.List;
import java.util.Map;

public class LoxClass implements LoxCallable{
    final String name;

    LoxClass(String name) {
        this.name = name;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        LoxInstance instance = new LoxInstance(this);
        return instance;
    }

    @Override
    public int arity() {
        // todo add actual arity
        return 0;
    }

    @Override
    public String toString() {
        return "<Class " + name + ">";
    }
}
