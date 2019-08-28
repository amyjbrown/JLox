package com.craftinginterpreters.Lox;

public class NativeFunctionError extends RuntimeException {
    public final String message;

    NativeFunctionError (String message) {
        this.message = message;
    }

}
