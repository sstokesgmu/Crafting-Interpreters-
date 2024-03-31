package lox;

class Token {
    final TokenType type; // ref to the enum
    final String lexeme; // group of characters that make the token
    final Object literal; //
    final int line; //line where the token is located

    // Token Constructor
    Token(TokenType type, String lexeme, Object literal, int line)  {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}