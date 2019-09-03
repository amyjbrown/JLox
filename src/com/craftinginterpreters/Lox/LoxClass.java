package com.craftinginterpreters.Lox;

import java.util.List;
import java.util.Map;

public class LoxClass extends LoxInstance implements LoxCallable{
    final String name;
    private final Map<String, LoxFunction> methods;
    private final Map<String, LoxFunction> static_methods;

    LoxClass(String name, Map<String, LoxFunction> methods, Map<String, LoxFunction> static_methods) {
        this.name = name;
        this.methods = methods;
        this.static_methods = static_methods;
    }

    LoxFunction findMethod(String name) {
        if (methods.containsKey(name)) {
            return methods.get(name);
        }
        return null;
    }

    LoxFunction findStaticMethod(String name) {
        if (static_methods.containsKey(name)) {
            return static_methods.get(name);
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
    Object get(Token name) {
        LoxFunction static_method = findStaticMethod(name.lexeme);

        if (static_method != null) {
            return static_method;
        }
        throw new RuntimeError(name, "Static method '" + name.lexeme +
                "' could not be found on class " + name + "'");
    }

    @Override
    void set(Token name, Object value) {
        // Override to keep things kosher
        throw new RuntimeError(name, "Cannot assign class attributes currently.");
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
