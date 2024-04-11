package lox;

import java.util.List;
/*
expression -> literal | unary | binary | grouping ;
literal    -> Number | String |  "true" | "false" | "nul" ;
grouping   -> "(" expression ")" ;
unary      -> ( "-" | "!") expression;
binary     -> expression operator expression;
operator   -> Arithmetic ( + , - , / ,* ) and Conditionals (!=, >=, ==, etc.)
*/

abstract class Expr {
    //implement interface
    interface Visitor<R> {
        R visitLiteralExpr(Literal expr);
        R visitGroupingExpr(Grouping expr);
        R visitUnaryExpr(Unary expr);
        R visitBinaryExpr(Binary expr);
    }


    static class Literal extends Expr {
        Literal(Object value) { this.value = value; }
        @Override
        <R> R accept(Visitor<R> visitor) { return visitor.visitLiteralExpr(this); }
        final Object value;
    }
    static class Grouping extends Expr {
        Grouping(Expr expression) { this. expression = expression; }
        @Override
        <R> R accept(Visitor<R> visitor) {return visitor.visitGroupingExpr(this);}
        final Expr expression;
    }
    static class Unary extends Expr {
        Unary (Token infix, Expr expression ) {
            this.infix = infix; this.expression = expression;
        }
        @Override
        <R> R accept(Visitor<R> visitor) {return visitor.visitUnaryExpr(this);}
        final Token infix; final Expr expression;
    }
    static class Binary extends Expr {
        Binary (Expr left, Token operator, Expr right) {
                this.left = left; this.operator = operator; this.right = right;
        }
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }
        final Expr left; final Token operator; final Expr right;
    }

    abstract <R> R accept(Visitor<R> visitor);
}