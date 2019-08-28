package com.craftinginterpreters.Lox;
/**Note Done error
 * JUse this for signalling to the interpreter that more input is expected and please grab some
 *
 * */
public class NotDoneErr extends RuntimeException{
    NotDoneErr(String message) {
        // Message to be stored as needed
        this.message = message;
    }

    final String message;

}
