package lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static lox.TokenType.*; // importing the enum

class Scanner{
    private final String source; //full source code sent from LOX
    private final List<Token> tokens = new ArrayList<>(); //Tokens will be added it recieves source text below
    private int lexStart = 0;  private int lexCurrent = 0;
    private int line = 1; //line of code
    private static final Map<String, TokenType> keywords;
    static {
        keywords = new HashMap<>();
        keywords.put("and", AND);
        keywords.put("class", CLASS);
        keywords.put("else", ELSE);
        keywords.put("false", FALSE);
        keywords.put("true", TRUE);
        keywords.put("fun", FUNC);
        keywords.put("for", FOR);
        keywords.put("nul", NUL);
        keywords.put("if", IF);
        keywords.put("or", OR);
        keywords.put("print", PRINT);
        keywords.put("return", RETURN);
        keywords.put("super", SUPER);
        keywords.put("this", THIS);
        keywords.put("var", VAR);
        keywords.put("while", WHILE);
    }
    Scanner (String source) {
        this.source = source;
    } //Source code getter func

    List<Token> scanTokens() { // loop over characters in the source code until it reaches the end
        while (!isAtEnd()) { //if not the end of source code continue looping
            //We are at the beginning of the next lex
            lexStart = lexCurrent;
            scanToken();//count starts at 1
        }
        tokens.add(new Token(EOF, "", null, line)); // marks end of source code
        return tokens;
    }

    private void scanToken() {
        char c = advance(); // gets char following the one we processed earlier
        switch (c) {
            //Single character tokens
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
            //Compound Tokens
            case '!': addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=': addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<': addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>': addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            //Ingnorable characters
            case '/':
                if(match('/')) { //checking if the next char is / = a comment
                    while(peek() != '\n' && !isAtEnd()) //true consume chars until it reaches \n = end of line
                        advance();} //skip ahead
                else { addToken(SLASH); }
                break;
            //White Spaces
            case ' ':
            case '\r':
            case '\t':
                break;
            //Line terminator
            case '\n': line++;
                break;
            //Literals
            case '"': string(); //comsume next characters of the string
                break;
            default:
                if(isDigit(c)){
                  number();
                } else if (isAlpha(c)){
                  identifier(); // assume there are more words to the identifier
                } else {
                  Lox.error(line, " Unexpected character."); // Handle lexical errors
                }
                break;
        }
    }
    //region Helper Methods
    private boolean isAtEnd() { // Check if scanner is at the end of source code text
        return lexCurrent >= source.length();
    }
    private char advance() { return source.charAt(lexCurrent++); } //returns the next char from the source
    private boolean match(char expected) { //checks what the next char is
        if(isAtEnd()) return false; //if at the end of the file return false
        // get char from current index; if not expected character = false
        if(source.charAt(lexCurrent) != expected) return false;
        lexCurrent++; //move on to the next character
        return true;
    }
    private char peek() { //Look ahead once
        if(isAtEnd())
            return '\0';
        return source.charAt(lexCurrent);
    }
    private char peekNext() { //Look ahead a second time
        if (lexCurrent + 1 >= source.length()) return '\0';
        return source.charAt(lexCurrent + 1);
    }
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
    private void addToken(TokenType type) {addToken(type, null);}
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(lexStart, lexCurrent);
        tokens.add(new Token(type, text, literal, line));
    }
    //endregion

    //region Adding Tokens Literals and Numbers
    private void string() {
        while (peek() != '"' && !isAtEnd()) { //Not end of string or end of file = true continue advancing
            // If a newline character is encountered within the string, increment the line number
            if(peek() == '\n')
                line++;
            advance(); // increment lexCurrent to next character
        }
        if(isAtEnd()) {  // If the end of the file is reached before the string is terminated, report an error
            Lox.error(line, "Unterminated string.");
            return;
        }
        advance(); // One more advance right after the closing qoute "
        String value = source.substring(lexStart + 1, lexCurrent - 1); //get the words right after starting " to the closing "
        addToken(STRING, value); // add that string as a token
    }
    private void number() {
        while (isDigit(peek()))
            advance();
        //Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance();
            while (isDigit(peek())) advance();
        }
        addToken(NUMBER,
                Double.parseDouble(source.substring(lexStart, lexCurrent)));
    }
    //endregion
    private void identifier() {
        while  (isAlphaNumeric(peek()))
            advance(); //peek to the next char check if number or character then increment up
        String text = source.substring(lexStart, lexCurrent); //build the new word
        TokenType type = keywords.get(text); //return text that matches an option in the hash map contianer
        if (type == null) // true -> value is a user defined indentifier
            type = IDENTIFIER;
        addToken(IDENTIFIER);
    }
    private boolean isAlpha(char c) { //checks value using ASCII character chart
        return  (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }
    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }
}