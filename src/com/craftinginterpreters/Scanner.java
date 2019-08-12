// Scanner class
// Use for scanning through source code and generating a set of tokens
package com.craftinginterpreters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.craftinginterpreters.TokenType.*;


class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;
    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and",     AND);
        keywords.put("class",   CLASS);
        keywords.put("else",    ELSE);
        keywords.put("false",   FALSE);
        keywords.put("fun",     FUN);
        keywords.put("if",      IF);
        keywords.put("nil",     NIL);
        keywords.put("or",      OR);
        keywords.put("print",   PRINT);
        keywords.put("return",  RETURN);
        keywords.put("super",   SUPER);
        keywords.put("this",    THIS);
        keywords.put("true",    TRUE);
        keywords.put("var",     VAR);
        keywords.put("while",   WHILE);
        keywords.put("for",     FOR);

    }

    Scanner (String source) {
        this.source = source;
    }

    //Main methods that create a list of tokens and spit them out
    List<Token> scanTokens () {
        while (!isAtEnd()){
            start = current;
            scanToken();
        }
        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private void scanToken () {
        // This will scan through the source code, find the appropriate tokens, and add them to a list of tokens
        char c = advance();
        switch(c){
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;

            //multiline characters
            case '!': addToken( match('=') ? BANG_EQUAL: BANG ); break;
            case '=': addToken( match('=') ? EQUAL_EQUAL: EQUAL ); break;
            case '<': addToken( match('=') ? LESS_EQUAL : LESS ); break;
            case '>': addToken( match('=')? GREATER_EQUAL: GREATER); break;

            // Line comment matching
            // TODO multine comments of the form `/* */`  and nesting
            case '/':
                if (match('/')) {
                    while ( peek() != '\n' && !isAtEnd()) advance();
                } else if (match('*')) {
                    advance();
                    multiline();
                }
                else{
                    addToken(SLASH);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                //ignore white whitepsace
                break;
            case '\n':
                line++;
                break;
                // Literals
            case '"': string(); break;

            default:
                // Check to see if number
                if (isDigit(c)){
                 number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    //Todo coalescce all invalid characters into a single error string for
                    Lox.error(line, "Unexpected Character '" + c + "''");
                }
                break;

        }


    }
    // Literal Creation

    private void string() {
        while (peek() != '"' && !isAtEnd() ){
            if (peek() == '\n') line ++;
            advance();
        }
        // Throw error if a string is unterminated
        if (isAtEnd()){
            Lox.error(line, "Unterminated String");
            return;
        }
        // Closing "
        advance();
        // Trim the surrounding quotes of the string before adding the token
        String value = source.substring(start + 1, current -1);
        addToken(STRING, value);
    }

    private void number() {
        /* Generate a Number literal */
        while (isDigit(peek())) advance();

    // look for fractional information
        if (peek() == '.' && isDigit(peekNext())) {
            // consume "."
            advance();

            while ( isDigit(peek())) advance();
        }
        addToken(NUMBER,
                Double.parseDouble( source.substring(start, current)));
    }

    private void identifier() {
        // Generate a identifier token
        while (isAlphaNumeric(peek())) advance();

        // See if Identifier is a reserved word
        String text = source.substring(start, current);

        TokenType type = keywords.get(text);
        if (type == null) type = IDENTIFIER;
        addToken(type);
    }

    // Multiline comment

    private void multiline() {
        // This edit will allow for multiple embedded multiline comments
        int depth = 1;
        // This will churn through a multiline comment until it reaches its end
        while (depth > 0 && !isAtEnd()) {
            char c = advance();
            switch (c){
                // jump to new line
                case '\n': line ++; break;
                // Check to see if further embedded
                case '/':
                    if (match('*')) depth++; break;
                case '*':
                    if (match('/')) depth--; break;
                default:
                    break;
            }
        }
    }

    // Helper functions

    private boolean match(char expected){
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;
        // If it is sound, then step along the character and then return true
        current ++;
        return true;
    }

    private char peek(){
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private boolean isAtEnd () {
        return current >= source.length();
    }

    private char advance() {
        current ++;
        return source.charAt(current - 1);
    }

    private void addToken(TokenType type){
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private boolean isDigit(char c){
        return c >= '0' && c <= '9';
    }

    private char peekNext(){
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private boolean isAlpha(char c) {
        // Returns true if C is alphaabetical, or is an underscore [a-zA-Z_]
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }
}
