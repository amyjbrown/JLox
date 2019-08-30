# JLox
Amy Brown  
Custom Implementation of the Lox language in Java via Bob Nystrom's Crafting Interpreters (https://craftinginterpreters.com)
This implementation hopes to add several of the challenge features discussed in the book. Additionally, some other features plan to be added, including a more epxressive interactive mode and an extended library of native functions.

Currently implemented features:
 * Interactive parser
 * Functional Lexer and Scanner
 * Semantic analyzer
 * Lexically scoped language
 * First-class Functions and closures
 * Classes
 * Docstrings, which are added between a callable and it's body  
     `fun max(a, b) "Returns the largest element of (a, b)" {...}`
 * Multiline embedded comments using `/* */` syntax
 * Ternary operator `condition ? value1 : value 2`
 * Comma operator like C `val1, val2`
 * Added more native functions and convenient way of extending them

Extended Native Functions:
 * `abs(x)` find the absolute value of `x`
 * `assert(condition)` throws a runtime exception if conditin evaluates to false
 * `exit(code)` Exits application with exit-code `code`
 * `help(object)` Prints a help-string for a class or function;
