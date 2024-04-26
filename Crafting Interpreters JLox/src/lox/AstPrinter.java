package lox;

 class AstPrinter implements Expr.Visitor<String> {
    String print(Expr expr) { return expr.accept(this); }
    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if(expr.value == null) return "nul";
        return expr.value.toString();
    }
    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return parenthesize("group", expr.expression);
    }
    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(expr.operator.toString(), expr.right);
    }
    @Override
    public String visitBinaryExpr (Expr.Binary expr) {
        return parenthesizeNUM(expr.left, expr.operator, expr.right);
    }

    //helper method
    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("Begin: ").append(name);
        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(" :End");
        return builder.toString();
    }

    // Binary Operation structure to print the syntax tree correctly
     private String parenthesizeNUM(Expr left, Token op, Expr right) {
         return String.format("Begin: %s %s %s :End", left.accept(this), op.lexeme, right.accept(this));
     }


     public static void main(String[] args) {
        Expr expression = new Expr.Binary (new Expr.Unary
                (new Token(TokenType.MINUS, "-", null, 1),
                        new Expr.Literal(123)),
                new Token (TokenType.STAR, "*", null, 1),
                new Expr.Grouping(new Expr.Literal(45.67)));
        System.out.println(new AstPrinter().print(expression));
    }

}