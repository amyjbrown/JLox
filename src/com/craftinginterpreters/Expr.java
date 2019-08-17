package com.craftinginterpreters;

import java.util.List;

abstract class Expr {
    interface Visitor<R>{
        R visitTernaryExpr(Ternary expr);
        R visitAssignExpr(Assign expr);
        R visitBinaryExpr(Binary expr);
        R visitCallExpr(Call expr);
        R visitGroupingExpr(Grouping expr);
        R visitLiteralExpr(Literal expr);
        R visitLogicalExpr(Logical expr);
        R visitUnaryExpr(Unary expr);
        R visitVariableExpr(Variable expr);
        R visitLambdaExpr(Lambda expr);
    }
 static class Ternary extends Expr {
    Ternary(Expr condition, Expr left, Expr right) {
        this.condition = condition;
        this.left = left;
        this.right = right;
    }

    <R> R accept(Visitor<R> visitor){
        return visitor.visitTernaryExpr(this);
        }

    final Expr condition;
    final Expr left;
    final Expr right;
 }
 static class Assign extends Expr {
    Assign(Token name, Expr value) {
        this.name = name;
        this.value = value;
    }

    <R> R accept(Visitor<R> visitor){
        return visitor.visitAssignExpr(this);
        }

    final Token name;
    final Expr value;
 }
 static class Binary extends Expr {
    Binary(Expr left, Token operator, Expr right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    <R> R accept(Visitor<R> visitor){
        return visitor.visitBinaryExpr(this);
        }

    final Expr left;
    final Token operator;
    final Expr right;
 }
 static class Call extends Expr {
    Call(Expr callee, Token paren, List<Expr> arguments) {
        this.callee = callee;
        this.paren = paren;
        this.arguments = arguments;
    }

    <R> R accept(Visitor<R> visitor){
        return visitor.visitCallExpr(this);
        }

    final Expr callee;
    final Token paren;
    final List<Expr> arguments;
 }
 static class Grouping extends Expr {
    Grouping(Expr expression) {
        this.expression = expression;
    }

    <R> R accept(Visitor<R> visitor){
        return visitor.visitGroupingExpr(this);
        }

    final Expr expression;
 }
 static class Literal extends Expr {
    Literal(Object value) {
        this.value = value;
    }

    <R> R accept(Visitor<R> visitor){
        return visitor.visitLiteralExpr(this);
        }

    final Object value;
 }
 static class Logical extends Expr {
    Logical(Expr left, Token operator, Expr right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    <R> R accept(Visitor<R> visitor){
        return visitor.visitLogicalExpr(this);
        }

    final Expr left;
    final Token operator;
    final Expr right;
 }

 // This is the function literal expression syntax, noted as "Lambda"
 static class Lambda extends Expr {
        Lambda(List<Token> params, List<Stmt> body) {
            this.params = params;
            this.body = body;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLambdaExpr(this);
        }
        final List<Token> params;
        final List<Stmt> body;
 }

 static class Unary extends Expr {
    Unary(Token operator, Expr right) {
        this.operator = operator;
        this.right = right;
    }

    <R> R accept(Visitor<R> visitor){
        return visitor.visitUnaryExpr(this);
        }

    final Token operator;
    final Expr right;
 }
 static class Variable extends Expr {
    Variable(Token name) {
        this.name = name;
    }

    <R> R accept(Visitor<R> visitor){
        return visitor.visitVariableExpr(this);
        }

    final Token name;
 }

    abstract <R> R accept(Visitor<R> visitor);
}
