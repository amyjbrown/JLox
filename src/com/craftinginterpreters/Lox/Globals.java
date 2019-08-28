package com.craftinginterpreters.Lox;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.io.IOException;
import java.util.Random.*;
// import java.lang.Math.*;



public class Globals {
    public static void define(Interpreter interpreter) {
        // Define all of the builtin functions
        interpreter.globals.define("clock", clock);
        interpreter.globals.define("abs", abs);
        interpreter.globals.define("assert", Assert);
        interpreter.globals.define("exit", Exit);
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
            if (arguments.get(0) instanceof Double)
                return Math.abs((double) arguments.get(0));
            throw new NativeFunctionError("abs(x) argument must be a number.");
        }
        @Override
        public String toString() {return "<Native function 'abs'>";}
    };


    // Assertion
    private static LoxCallable Assert = new LoxCallable() {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            Object condition = arguments.get(0);

            if (!(condition instanceof Boolean))
                throw new NativeFunctionError("assert(condition) argument must evaluate to a boolean.");

            if (arguments.get(0) instanceof Boolean && arguments.get(0) == Boolean.FALSE)
                throw new NativeFunctionError("Assert failed.");
            else return null;

        }
    };

    private static LoxCallable Exit = new LoxCallable() {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            Double exitCode = (Double) arguments.get(0);
            System.out.println("Exiting with code " + exitCode.intValue());
            System.exit(exitCode.intValue());
            return null;
        }

        @Override
        public String toString() {return "<Native function 'exit'>";}
    };

    // input: Prompts user for info
    /* private static LoxCallable Input = new LoxCallable() {
        @Override
        public int arity() { return 1; }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments){
            String result;
            InputStreamReader input = new InputStreamReader(System.in);
            BufferedReader reader = new BufferedReader(input);
            System.out.print(arguments.get(0));

            result = reader.readLine();

            try {
                reader.readLine();
            } catch (IOException) {

            }
        }
    }; */

}
