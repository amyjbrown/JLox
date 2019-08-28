package com.craftinginterpreters.Lox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void> {
    private final Interpreter interpreter;
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();
    private FunctionType currentFunction = FunctionType.NONE;
    private ClassType currentClass = ClassType.NONE;


    Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    // for handling returns and other things
    private enum ClassType {
        NONE,
        CLASS
    }

    private enum FunctionType {
        NONE,
        FUNCTION,
        METHOD,
    }

    @Override
    public Void visitClassStmt(Stmt.Class stmt) {
        ClassType enclosingClass = currentClass;
        currentClass = ClassType.CLASS;

        declare(stmt.name);
        define(stmt.name);

        // Prevent circular loops of inheritance
        if (stmt.superclass != null &&
                stmt.name.lexeme.equals(stmt.superclass.name.lexeme)){
            Lox.error(stmt.superclass.name,
                    "A class cannot inherit from itself.");
        }

        // Resolve superclass
        if (stmt.superclass != null) {
            resolve(stmt.superclass);
        }

        if (stmt.superclass != null) {
            beginScope();
            scopes.peek().put("super", true);
        }


        beginScope();
        scopes.peek().put("this", true);

        for (Stmt.Function method : stmt.methods) {
            FunctionType declaration = FunctionType.METHOD;
            resolveFunction(method, declaration);
        }

        endScope();

        if (stmt.superclass != null) endScope();

        //???? HOW DID THIS WORK IF THIS WAS GONE
        currentClass = enclosingClass;

        return null;
    }



    @Override
    public Void visitBlockStmt (Stmt.Block stmt) {
        beginScope();
        resolve(stmt.statements);
        endScope();
        return null;
    }


    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        declare(stmt.name);
        define(stmt.name);

        resolveFunction(stmt, FunctionType.FUNCTION);
        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        declare(stmt.name);
        if (stmt.initializer != null) {
            resolve(stmt.initializer);
        }
        define(stmt.name);
        return null;
    }

    @Override
    public Void visitExpressionStmt (Stmt.Expression stmt) {
        resolve (stmt.expression);
        return null;
    }

    @Override
    public Void visitIfStmt (Stmt.If stmt) {
        resolve (stmt.condition);
        resolve(stmt.thenBranch);
        if (stmt.elseBranch != null) resolve(stmt.elseBranch);
        return null;
    }

    @Override
    public Void visitVariableExpr(Expr.Variable expr) {
        // This checks if we're not in global scope (wherein we don't enforce the stronger rules of static scoping
        // And then if we're in a a variable declaration
        // scopes.peek().get(expr.name.lexeme) == Boolean.FALSE ensures that a variable is still being delcared but
        // hasn't been defined, in which case that would eval to True since it has been defined as either nil or the
        // user added expression
        if (!scopes.isEmpty() &&
        scopes.peek().get(expr.name.lexeme) == Boolean.FALSE) {
            Lox.error(expr.name,
                    "cannot read local variable in it's own initializer");
        }

        resolveLocal(expr, expr.name);
        return null;
    }

    @Override
    public Void visitPrintStmt (Stmt.Print stmt) {
        resolve(stmt.expression);
        return null;
    }

    @Override
    public Void visitReturnStmt (Stmt.Return stmt) {
        if (currentFunction == FunctionType.NONE){
            Lox.error(stmt.keyword, "Cannot return from top-level code, ");
        }
        if (stmt.value != null) {
            resolve(stmt.value);
        }

        return null;
    }

    @Override
    public Void visitWhileStmt (Stmt.While stmt) {
        resolve (stmt.condition);
        resolve (stmt.body);
        return null;
    }
    @Override
    public Void visitTernaryExpr(Expr.Ternary expr) {
        resolve(expr.condition);
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }


    @Override
    public Void visitLambdaExpr(Expr.Lambda expr) {
        // Fakes a FunctionStmt so we can pass it here easily
        resolveFunction(new Stmt.Function(
                null,
                expr.params, expr.body
                ),
                FunctionType.FUNCTION
        );
        return null;

    }

    @Override
    public Void visitAssignExpr(Expr.Assign expr){
        resolve(expr.value);
        resolveLocal(expr, expr.name);
        return null;
    }

    @Override
    public Void visitBinaryExpr (Expr.Binary expr) {
        resolve(expr.left);
        resolve (expr.right);
        return null;
    }

    @Override
    public Void visitLogicalExpr (Expr.Logical expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }
    @Override
    public Void visitSetExpr(Expr.Set expr) {
        resolve(expr.value);
        resolve(expr.object);
        return null;
    }

    @Override
    public Void visitThisExpr(Expr.This expr) {
        if (currentClass == ClassType.NONE) {
            Lox.error(expr.keyword,
                    "Cannot use 'this' outside of a class.");
            return null;
        }
        resolveLocal(expr, expr.keyword);
        return null;
    }

    @Override
    public Void visitSuperExpr(Expr.Super expr) {
        resolveLocal(expr, expr.keyword);
        return null;
    }


    @Override
    public Void visitCallExpr (Expr.Call expr) {
        resolve(expr.callee);

        for (Expr argument : expr.arguments) {
            resolve(argument);
        }

        return null;
    }

    @Override
    public Void visitGetExpr(Expr.Get expr) {
        resolve(expr.object);
        return null;
    }

    @Override
    public Void visitGroupingExpr (Expr.Grouping expr) {
        resolve (expr.expression);
        return null;
    }

    @Override
    public Void visitUnaryExpr (Expr.Unary expr) {
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitLiteralExpr (Expr.Literal expr) {
        return null;
    }

    //proper things
    void resolve(List<Stmt> statements) {
        for (Stmt statement : statements) {
            resolve(statement);
        }
    }

    private void resolveLocal(Expr expr, Token name) {
        /* This is the *special* function that makes closures work
        This adds to the interpreter we inject to the resolver that
        We should add a entry in the locals: dict<Expr, int> mapping to describe how far up the chain we need the
        the entity to be

        Count down from current scope stack depth(zero-indexed)
        Check IF the current scope in iteration contains the name
        If it does contain the name, then Put into Interpreter.locals the Expr and the depth it has to go

        Thus for
        ```
        var x = "outer"; # global is equivalent to "empty scope"
        {var y = "inner"; # scope 1
            {             # scope level 2
            print x;
            }
        }
        ```
        when it gets to `print x`, it is 2 scopes deep
        Thus we have Scopes: scope1, scope2
        iteration 1: i = 1; scopes.get(1).containsKey(x) ->> FALSE
        Iteration 2: i = 0; scopes.get(0).containsKey(x) ->> TRUE
        interperter.locals <- Expr = LiteralExpr("x"), scopes.size() - 1 - i ->> 2 -1 -0 ->> 1

        Now, when interpreter calls visitVariableExpr
        */
        for (int i = scopes.size() - 1; i >= 0;i--){
            if (scopes.get(i).containsKey(name.lexeme)) {
                // If it does contain the name
                interpreter.resolve(expr, scopes.size() - 1 - i);
                return;
            }
        }
    }

    private void resolve(Expr expr) {
        expr.accept(this);
    }

    private void resolveFunction(Stmt.Function function, FunctionType type) {
        FunctionType enclosingFunction = currentFunction;
        currentFunction = type;

        beginScope();
        for (Token param : function.params) {
            declare(param);
            define(param);
        }
        resolve(function.body);
        endScope();

        currentFunction = enclosingFunction;
    }

    private void resolve(Stmt stmt) {
        stmt.accept(this);
    }

    private void beginScope() {
        scopes.push(new HashMap<String, Boolean>());
    }

    private void endScope() {
        scopes.pop();
    }

    private void declare (Token name) {
        if (scopes.isEmpty()) return;

        Map<String, Boolean> scope = scopes.peek();
        scope.put(name.lexeme, false);
    }

    private void define (Token name) {
        if (scopes.isEmpty()) return;
        scopes.peek().put(name.lexeme, true);
    }
}
