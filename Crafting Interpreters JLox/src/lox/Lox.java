/**
Goal of this script is to sketch out the basic shape of our interpreter
**/
package lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox
{
    private static final Interpreter interpreter = new Interpreter();
    static boolean hadError = false;
    static boolean hadRuntimeError = false;
    public static void main(String[] args) throws IOException{
        if(args.length > 1){
            System.out.println("Usage: jlox [script]");
            System.exit(0); // code exited with no error
        } else if (args.length == 1){
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }
    //Running from Command line with file argument
    private static void runFile(String path) throws IOException{
        byte[] bytes = Files.readAllBytes(Paths.get(path)); // convert string to bytes by getting its path in memory
        run(new String(bytes, Charset.defaultCharset()));
        //Indicate an error in the exit code return 1
        if(hadError) System.exit(1);
        if(hadRuntimeError) System.exit(2);
    }
    //Running jlox from Command line with no file arguments
    private static void runPrompt() throws IOException{
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for(;;){
            System.out.print(">");
            String line = reader.readLine();
            if(line == null) break;
            run(line);
            hadError = false;
        }
    }

    //Core function
    private static void run(String source){
        Scanner scanner = new Scanner(source); //create instance of scanner from the source file
        List<Token> tokens = scanner.scanTokens(); // find tokens
        Parser parser = new Parser(tokens);
        Expr expression = parser.parse();

        if (hadError) return;

        interpreter.interpret(expression);
        System.out.println(new AstPrinter().print(expression));
    }

    //Simple Error handling
    static void error(int line, String message){
        report(line, "", message);
    }
    private static void report(int line, String location, String message){
        System.err.println("[line " + line + "] Error" + location + ": " + message);
        hadError = true;
    }

    static void error(Token token, String message) {
        if (token.type == TokenType.EOF)
            report(token.line, " at end", message);
        else
            report(token.line, " at ' " + token.lexeme + "'", message);
    }

    static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() + "\n[line " + error.token.line + "]");
        hadRuntimeError = true;
    }
}

