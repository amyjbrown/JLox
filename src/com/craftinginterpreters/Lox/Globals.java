package com.craftinginterpreters.Lox;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.lang.Math.*;

public class Globals {
    public static void define(Interpreter interpreter) {
        // Define all of the builtin functions
        interpreter.globals.define("clock", clock);
    }

    // This is where all of the items will be defined, along with explanations

    // Builtin functions

    // Clock: returns time between invocations
    private static LoxCallable clock = new LoxCallable() {
        @Override
        public int arity() {
            return 0;
        }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            return (double) System.currentTimeMillis() / 1000.0;
        }

        @Override
        public String toString() {return "<Native function 'clock'>";}
    };

    // Abs: return abs value of X
    private static LoxCallable abs = new LoxCallable() {
        @Override
        public int arity() { return 1; }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            return Math.abs((double) arguments.get(0));
        }
        @Override
        public String toString() {return "<Native function 'abs'>";}
    };

    // input: Prompts user for info
    private static LoxCallable input = new LoxCallable() {
        @Override
        public int arity() { return 1; }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            InputStreamReader input = new InputStreamReader(System.in);
            BufferedReader reader = new BufferedReader(input);
            System.out.print(arguments.get(0));

            return null; //reader.readLine();
        }
    };

}
