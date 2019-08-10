# JLox
<3 Amy Brown <3
Custom Implementation of the Lox language in Java via Bob Nystrom's Crafting Interpreters (https://craftinginterpreters.com)
This implementation hopes to add several of the challenge features discussed in the book
Features that could interfere with future implementations are stored in branch "Extra", but may be added later

JLox current featurs:
 * Interactive parser for handling expressions
 * Functional Lexer and Scanner
 * Strong error feedback for user convenience

Bonus features:
  * Multiline nested comments, using the familiar `/*` and `*/`. These may be nested multiple times, so that
    `/* Nesting 1 /* Nesting 2  /* Nesting 3 */ */ */`
  * Error production handling for unary expressions, so that invalid expressions like `/2` will give users a better error format like 
  `[line 1] Error at '/': Invalid unary operator. `
  *[in extra] C-style comma operator: `true, false, "walrus"` evaluates all elements in the comma list and returns the last item
  *[in extra] C-style ternary operator: `true ? "Walrus" : "Panda"`
