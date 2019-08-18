package com.craftinginterpreters.Lox;

class Return extends RuntimeException{
    final Object value;

    Return(Object value) {
        // What is this ?
        super(null, null, false, false);
        this.value = value;
    }
}
