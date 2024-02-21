import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

public class
Main {
    public static void main(String[] args) throws IOException {
        LinkedList tokens = new LinkedList<Token>();
            //Avoids out-of-bounds error.
            if (args.length > 0) {
                Path myPath = Paths.get(args[0]);
                String content = new String(Files.readAllBytes(myPath));
                Lexer lexer = new Lexer(content);
                LinkedList<Token> lexedTokens = lexer.lex();
                Parser parser = new Parser(lexedTokens);
                ProgramNode programNode = parser.Parse();
                Interpreter interpreter =new Interpreter(programNode,null);
                interpreter.InterpretProgram(programNode);
            }
        }

    }






