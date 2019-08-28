package com.craftinginterpreters.Lox;

import java.util.List;
import java.util.Map;

public class LoxClass implements LoxCallable{
    final String name;
    private final Map<String, LoxFunction> methods;
    final LoxClass superclass;

    LoxClass(String name, LoxClass superclass,Map<String, LoxFunction> methods) {
        this.name = name;
        this.methods = methods;
        this.superclass = superclass;
    }

    LoxFunction findMethod(String name) {
        if (methods.containsKey(name)) {
            return methods.get(name);
        }

        if (superclass != null) {
            return superclass.findMethod(name);
        }

        return null;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        LoxInstance instance = new LoxInstance(this);
        // check to see if the initializer is declared
        LoxFunction initializer = findMethod("init");
        // If it is declared, assign to "this" the reference to the instance in
        // the constructor, and then call it given the appropriate arguements
        if (initializer != null) {
            initializer.bind(instance).call(interpreter, arguments);
        }
        return instance;
    }

    @Override
    public int arity() {
        LoxFunction init =  findMethod("init");
        if (init!= null) return init.arity();
        // return 0 if no defined initializer
        return 0;
    }

    @Override
    public String toString() {
        return "<Class " + name + ">";
    }
}
