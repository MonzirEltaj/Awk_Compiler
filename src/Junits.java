import static org.junit.Assert.*;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

public class Junits {
    //this is my hard coded file path!!
    String filePath = null;


    @Test
    public void test_Peek() {
        String test = "Words type stuff to test 1 2 3.2_\n";
        StringHandler str = new StringHandler(test);
        assertEquals('W', str.Peek(0));
        assertEquals('o', str.Peek(1));
        assertEquals(' ', str.Peek(5));
        assertEquals('1', str.Peek(25));
        assertEquals('.', str.Peek(30));
        assertEquals('_', str.Peek(32));

        assertEquals('\n', str.Peek(33));


    }

    @Test
    public void test_PeektoString() {
        String test = "Bob ross is a cool guy 2_31\n";
        StringHandler str = new StringHandler(test);
        //Normal Case with WORD
        assertEquals("Bob ", str.PeektoString(4));
        //Edge Case of entire string
        assertEquals("Bob ross is a cool guy 2_31\n", str.PeektoString(test.length()));
        //Normal case NUM
        assertEquals("Bob ross is a cool guy 2", str.PeektoString((24)));
    }

    @Test
    public void test_getChar() {
        String test = "I love candy, its sweet";
        StringHandler str = new StringHandler(test);
        assertEquals('I', str.getChar());
        assertEquals(' ', str.getChar());
        assertEquals('l', str.getChar());
        assertEquals('o', str.getChar());
        assertEquals('v', str.getChar());
        assertEquals('e', str.getChar());

    }

    @Test
    public void test_Swallow() {
        String test = "thanks for grading my project homie";
        StringHandler str = new StringHandler(test);
        assertEquals('t', str.getChar());
        str.Swallow(1);
        assertEquals('a', str.getChar());


        String test2 = "thanks for grading my project homie";
        StringHandler str2 = new StringHandler(test2);
        str2.Swallow(7);
        assertEquals("for grading my project homie", str2.reminader());
    }

    @Test
    public void test_Remainder() {
        String test = "i do be vibin";
        StringHandler str = new StringHandler(test);
        str.Swallow(2);
        assertEquals("do be vibin", str.reminader());
        str.Swallow(10);
        assertEquals("n", str.reminader());
        str.Swallow(1);
        assertEquals("", str.reminader());
    }

    @Test
    public void test_IsDone() {
        String test = "the pirate sales the seven seas";
        StringHandler str = new StringHandler(test);
        assertFalse(str.isDone());
        str.Swallow(31);
        assertTrue(str.isDone());
    }

    @Test
    public void test_Lexer_and_Token() {
        String test = "Bob the builder 123415.2\n";
        Lexer lexer = new Lexer(test);
        LinkedList tokens = new LinkedList<Token>();
        tokens = lexer.lex();
        assertEquals("[Type: WORD Value: Bob, Type: WORD Value: the, Type: WORD Value: builder, " + "Type: NUMBER Value: 123415.2, Type: SEPARATOR]", tokens.toString());
        String test2 = "2.2bob3\n 0000023.2\n rob";
        Lexer lexer2 = new Lexer(test2);
        LinkedList tokens2 = new LinkedList<Token>();
        tokens2 = lexer2.lex();
        assertEquals("[Type: NUMBER Value: 2.2, Type: WORD Value: bob3, Type: SEPARATOR, " + "Type: NUMBER Value: 0000023.2, Type: SEPARATOR, Type: WORD Value: rob]", tokens2.toString());
    }

    @Test
    public void test_edge_Cases() {
        //assert throws is part of jUnits, used to insure exeception is thrown when bad things happen
        //Only one decimal per number!
        String test = "1.23415.2\n";
        Lexer lexer = new Lexer(test);
        assertThrows(Exception.class, () -> {
            lexer.lex();
        });

        String test2 = "bobross.2..2";
        Lexer lexer2 = new Lexer(test2);
        assertThrows(Exception.class, () -> {
            lexer2.lex();
        });

        String test4 = "bobross@";
        Lexer lexer4 = new Lexer(test4);
        assertThrows(Exception.class, () -> {
            lexer4.lex();
        });
    }


    @Test
    public void testStringLiteral() {
        String quote = "She said \"Hello There\" and then she left";
        Lexer lexer = new Lexer(quote);
        LinkedList<Token> tokens = lexer.lex();
        assertEquals("[Type: WORD Value: She, Type: WORD Value: said, Type: STRINGLITERAL Value: Hello There, " + "Type: WORD Value: and, Type: WORD Value: then, Type: WORD Value: she, Type: WORD Value: left]", tokens.toString());

    }

    @Test
    public void testStringLiteralEmptyString() {
        String quote = "\"\"";
        Lexer lexer = new Lexer(quote);
        LinkedList<Token> tokens = lexer.lex();
        assertEquals("[Type: STRINGLITERAL Value: ]", tokens.toString());
    }

    @Test
    public void testMissingEndQuote() {
        Lexer lexer = new Lexer("\"Incomplete string");
        assertThrows(RuntimeException.class, lexer::HandleStringLiteral);
        Lexer lexer2 = new Lexer("\'Incomplete string");
        assertThrows(RuntimeException.class, lexer2::HandleStringLiteral);
    }

    @Test
    public void testPattern() {
        String quote = "`apple` I love potatoes 23.41";

        Lexer lexer = new Lexer(quote);
        LinkedList<Token> tokens = lexer.lex();
        assertEquals("[Type: PATTERN Value: apple, Type: WORD Value: I, Type: WORD Value: love, Type: WORD Value: potatoes, Type: NUMBER Value: 23.41]", tokens.toString());
    }


    @Test
    public void testOneSymbol() {
        String quote = "bob makes $5000 and <= to how much randy makes 23.1 % of that";
        Lexer lexer = new Lexer(quote);
        LinkedList<Token> tokens = lexer.lex();
        assertEquals("[Type: WORD Value: bob, Type: WORD Value: makes, Type: DOLLAR, Type: NUMBER Value: 5000, Type: WORD Value: and, " +
                "Type: LESSOREQUAL, Type: WORD Value: to, Type: WORD Value: how, Type: WORD Value: much, Type: WORD Value: randy, " + "" +
                "Type: WORD Value: makes, Type: NUMBER Value: 23.1, Type: PERCENT, Type: WORD Value: of, Type: WORD Value: that]", tokens.toString());

        String quote2 = "Im testing all the symbols here {bob} (231.2) [square] $21 ~ bpb=2 <W> ! 3+1 5^2 bbo-1 really? 5: 2*2 | 1,and 2 ; \n";
        Lexer lexer2 = new Lexer(quote2);
        LinkedList<Token> tokens2 = lexer2.lex();

        assertEquals("[Type: WORD Value: Im, Type: WORD Value: testing, " +
                "Type: WORD Value: all, Type: WORD Value: the, Type: WORD Value: symbols, Type: WORD Value: here, Type: OPENCURLY, " +
                "Type: WORD Value: bob, Type: CLOSECURLY, Type: OPENPAR, Type: NUMBER Value: 231.2, Type: CLOSEPAR, Type: OPENBRAC, " +
                "Type: WORD Value: square, Type: CLOSEBRAC, Type: DOLLAR, Type: NUMBER Value: 21, Type: TILDE, Type: WORD Value: bpb, " +
                "Type: EQUAL, Type: NUMBER Value: 2, Type: LESS, Type: WORD Value: W, Type: GREATER, Type: EXPLANATION, Type: NUMBER Value: 3, " +
                "Type: PLUS, Type: NUMBER Value: 1, Type: NUMBER Value: 5, Type: EXPO, Type: NUMBER Value: 2, Type: WORD Value: bbo, Type: MINUS, " +
                "Type: NUMBER Value: 1, Type: WORD Value: really, Type: QUESTION, Type: NUMBER Value: 5, Type: COLON, Type: NUMBER Value: 2, " +
                "Type: STAR, Type: NUMBER Value: 2, Type: BAR, Type: NUMBER Value: 1, Type: COMMA, Type: WORD Value: and, Type: NUMBER Value: 2, " +
                "Type: SEPARATOR, Type: SEPARATOR]", tokens2.toString());
    }

    @Test
    public void testTwoSymbol() {
        //Insure im not peaking 2 if there's the doc ends before
        String quote = "bob+";
        Lexer lexer = new Lexer(quote);
        LinkedList<Token> tokens = lexer.lex();
        assertEquals("[Type: WORD Value: bob, Type: PLUS]", tokens.toString());

        String quote2 = "john makes >= to sarah who makes <= to sam who is; ++23131.2 but --13131.5 6^=4 3%=nine 3*=4 2-=2 3+=bob + + - - /=2 bob && sam are chill >> 2 || ";
        Lexer lexer2 = new Lexer(quote2);
        LinkedList<Token> tokens2 = lexer2.lex();

        assertEquals("[Type: WORD Value: john, Type: WORD Value: makes, Type: GREATEROREQUAL, Type: WORD Value: to, Type: WORD Value: sarah, " + "Type: WORD Value: who, Type: WORD Value: makes, Type: LESSOREQUAL, Type: WORD Value: to, Type: WORD Value: sam, Type: WORD Value: who, " +
                "Type: WORD Value: is, Type: SEPARATOR, Type: PLUSX2, Type: NUMBER Value: 23131.2, Type: WORD Value: but, Type: MINUSX2, Type: NUMBER Value: 13131.5, " +
                "Type: NUMBER Value: 6, Type: EXPOMATH, Type: NUMBER Value: 4, Type: NUMBER Value: 3, Type: PERCENTMATH, Type: WORD Value: nine, Type: NUMBER Value: 3, " +
                "Type: STARMATH, Type: NUMBER Value: 4, Type: NUMBER Value: 2, Type: MINUSMATH, Type: NUMBER Value: 2, Type: NUMBER Value: 3, Type: SUM, Type: WORD Value: bob, " +
                "Type: PLUS, Type: PLUS, Type: MINUS, Type: MINUS, Type: DIVIDEMATH, Type: NUMBER Value: 2, Type: WORD Value: bob, Type: AND, Type: WORD Value: sam, Type: WORD Value: are, " +
                "Type: WORD Value: chill, Type: APPEND, Type: NUMBER Value: 2, Type: OR]", tokens2.toString());
        //This fails bc its only one &
        String quote3 = "bob&";
        Lexer lexer3 = new Lexer(quote2);
        LinkedList<Token> tokens3 = lexer3.lex();
        assertThrows(RuntimeException.class, lexer3::processSymbols);


    }

    @Test
    public void testKeywords() {
        String quote = "while, if do for break continue else return BEGIN END random word print printf, next in delete getline exit nextfile function 1213.13 bob\n  WHILE ENDING door";
        Lexer lexer = new Lexer(quote);
        LinkedList<Token> tokens = lexer.lex();
        assertEquals("[Type: WHILE, Type: COMMA, Type: IF, Type: DO, Type: FOR, Type: BREAK, Type: CONTINUE, Type: ELSE, Type: RETURN, " +
                "Type: BEGIN, Type: END, Type: WORD Value: random, Type: WORD Value: word, Type: PRINT, Type: PRINTF, Type: COMMA, Type: NEXT, " +
                "Type: IN, Type: DELETE, Type: GETLINE, Type: EXIT, Type: NEXTFILE, Type: FUNCTION, Type: NUMBER Value: 1213.13, Type: WORD Value: bob, " +
                "Type: SEPARATOR, Type: WORD Value: WHILE, Type: WORD Value: ENDING, Type: WORD Value: door]", tokens.toString());

        //These should NOT be keywords
        String keywordsSpeltWrong = "prints WhILe GetLineg";
        Lexer lexer2 = new Lexer(keywordsSpeltWrong);
        LinkedList<Token> tokens2 = lexer2.lex();
        assertEquals("[Type: WORD Value: prints, Type: WORD Value: WhILe, Type: WORD Value: GetLineg]", tokens2.toString());
    }

    @Test
    public void testComments() {
        String quote = "john walked to the store for 2.5% milk #he forgot about it \n he then bought candy";
        Lexer lexer = new Lexer(quote);
        LinkedList<Token> tokens = lexer.lex();
        assertEquals("[Type: WORD Value: john, Type: WORD Value: walked, Type: WORD Value: to, Type: WORD Value: the, Type: WORD Value: store, Type: FOR, " +
                "Type: NUMBER Value: 2.5, Type: PERCENT, Type: WORD Value: milk, Type: SEPARATOR, Type: WORD Value: he, Type: WORD Value: then, Type: WORD Value: bought, Type: WORD Value: candy]", tokens.toString());
    }

    //PARSER TESTING BELLOW
    // Note To Self:
    //index will be off by one since lexer starts char pos 1 instead of 0 '
    // but im testing with a linked list which starts at 0
    //I am not testing with lex since I cant be 100% sure lexer 2 is correct.

    @Test
    public void testAcceptSeparators() {
        LinkedList<Token> tokens = new LinkedList<>();
        //Testing Functionality outside of parser.
        tokens.add(new Token(Token.TokenType.SEPARATOR, 1, 1));
        tokens.add(new Token(Token.TokenType.SEPARATOR, 1, 2));
        tokens.add(new Token(Token.TokenType.SEPARATOR, 1, 3));
        tokens.add(new Token(Token.TokenType.SEPARATOR, 1, 4));
        tokens.add(new Token(Token.TokenType.SEPARATOR, 1, 5));
        tokens.add(new Token(Token.TokenType.COMMA, 1, 6));
        tokens.add(new Token(Token.TokenType.WORD, 1, 7));

        Parser parser = new Parser(tokens);

        boolean result = parser.AcceptSeparators();
        assertTrue(result);
        assertEquals("[Type: COMMA, Type: WORD]", tokens.toString());

    }

    @Test
    public void testAcceptSeparatorInParseFunction() {
        LinkedList tokens = new LinkedList<Token>();
        tokens.add(new Token(Token.TokenType.FUNCTION, 1, 1));
        tokens.add(new Token(Token.TokenType.WORD, 1, 2, "wow"));
        tokens.add(new Token(Token.TokenType.OPENPAR, 1, 3));
        tokens.add(new Token(Token.TokenType.WORD, 1, 4, "cow"));
        tokens.add(new Token(Token.TokenType.COMMA, 1, 5));
        tokens.add(new Token(Token.TokenType.WORD, 1, 6, "steep"));
        tokens.add(new Token(Token.TokenType.CLOSEPAR, 1, 7));
        tokens.add(new Token(Token.TokenType.SEPARATOR, 1, 8));
        tokens.add(new Token(Token.TokenType.SEPARATOR, 1, 9));


        Parser parser = new Parser(tokens);
        ProgramNode programNode = parser.Parse();
        assertEquals("[FunctionDefinitionNode: \n" +
                " Name:wow\n" +
                " parameters:[Type: WORD Value: cow, Type: WORD Value: steep]\n" +
                " statements:[]\n" +
                "]", programNode.getFunctionDefinitionNodes().toString());

        //THIS SHOULD THROW AN EXCEPTION SINCE SEPARATORS ARE ONLY ALLOWED AFTER COMMAS OR CLOSE PAR.
        LinkedList tokens2 = new LinkedList<Token>();
        tokens2.add(new Token(Token.TokenType.FUNCTION, 1, 1));
        tokens2.add(new Token(Token.TokenType.WORD, 1, 2, "rabbit"));
        tokens2.add(new Token(Token.TokenType.OPENPAR, 1, 3));
        tokens2.add(new Token(Token.TokenType.SEPARATOR, 1, 4));
        tokens2.add(new Token(Token.TokenType.WORD, 1, 5));
        tokens2.add(new Token(Token.TokenType.CLOSEPAR, 1, 7));


        Parser parser2 = new Parser(tokens2);
        assertThrows(RuntimeException.class, parser2::Parse);
    }


    @Test
    public void testAcceptSeparatorsNoSeparators() {
        LinkedList<Token> tokens = new LinkedList<>();
        tokens.add(new Token(Token.TokenType.WORD, 1, 1));
        tokens.add(new Token(Token.TokenType.WORD, 1, 2));

        Parser parser = new Parser(tokens);

        boolean result = parser.AcceptSeparators();

        assertFalse(result);
    }

    @Test
    public void testParseFunction() {
        LinkedList<Token> tokens = new LinkedList<>();
        tokens.add(new Token(Token.TokenType.FUNCTION, 1, 1));
        tokens.add(new Token(Token.TokenType.WORD, 1, 2));
        tokens.add(new Token(Token.TokenType.OPENPAR, 1, 3));
        //Comma without word, results in exception!
        tokens.add(new Token(Token.TokenType.COMMA, 1, 4));
        tokens.add(new Token(Token.TokenType.CLOSEPAR, 1, 5));

        Parser parser = new Parser(tokens);
        ProgramNode programNode = new ProgramNode();

        assertThrows(RuntimeException.class, () -> {
            parser.parseFunction(programNode);
        });
    }


    @Test
    public void testParseFunctionWithCommas() {
        LinkedList<Token> tokens = new LinkedList<>();
        tokens.add(new Token(Token.TokenType.FUNCTION, 1, 1));
        tokens.add(new Token(Token.TokenType.WORD, 1, 2));
        tokens.add(new Token(Token.TokenType.OPENPAR, 1, 3));
        tokens.add(new Token(Token.TokenType.WORD, 1, 4));
        tokens.add(new Token(Token.TokenType.COMMA, 1, 5));
        tokens.add(new Token(Token.TokenType.WORD, 1, 6));
        tokens.add(new Token(Token.TokenType.COMMA, 1, 7));
        tokens.add(new Token(Token.TokenType.WORD, 1, 8));
        tokens.add(new Token(Token.TokenType.CLOSEPAR, 1, 9));

        Parser parser = new Parser(tokens);
        ProgramNode programNode = new ProgramNode();

        boolean result = parser.parseFunction(programNode);

        assertTrue(result);
    }

    @Test
    public void testParseFunctionWithoutCommas() {
        LinkedList<Token> tokens = new LinkedList<>();
        tokens.add(new Token(Token.TokenType.FUNCTION, 1, 1));
        tokens.add(new Token(Token.TokenType.WORD, 1, 2));
        tokens.add(new Token(Token.TokenType.OPENPAR, 1, 3));
        tokens.add(new Token(Token.TokenType.WORD, 1, 4));
        tokens.add(new Token(Token.TokenType.CLOSEPAR, 1, 5));

        Parser parser = new Parser(tokens);
        ProgramNode programNode = new ProgramNode();

        boolean result = parser.parseFunction(programNode);

        assertTrue(result);
    }

    @Test
    public void testParseFunctionEmpty() {
        LinkedList<Token> tokens = new LinkedList<>();
        tokens.add(new Token(Token.TokenType.FUNCTION, 1, 1));
        tokens.add(new Token(Token.TokenType.WORD, 1, 2));
        tokens.add(new Token(Token.TokenType.OPENPAR, 1, 3));
        //Empty is valid
        tokens.add(new Token(Token.TokenType.CLOSEPAR, 1, 4));

        Parser parser = new Parser(tokens);
        ProgramNode programNode = new ProgramNode();

        boolean result = parser.parseFunction(programNode);

        assertTrue(result);
    }

    public void testParseFunctionNotAFunction() {
        LinkedList<Token> tokens = new LinkedList<>();
        tokens.add(new Token(Token.TokenType.WORD, 1, 1));
        tokens.add(new Token(Token.TokenType.WORD, 1, 2));
        tokens.add(new Token(Token.TokenType.OPENPAR, 1, 3));
        tokens.add(new Token(Token.TokenType.CLOSEPAR, 1, 4));
        //If function is missing then it is not a function,
        // This should result in return of false NOT AN EXCEPTION
        Parser parser = new Parser(tokens);
        ProgramNode programNode = new ProgramNode();

        boolean result = parser.parseFunction(programNode);

        assertFalse(result);
    }

    @Test
    public void testTokenManger() {
        LinkedList tokens = new LinkedList<Token>();
        tokens.add(new Token(Token.TokenType.FUNCTION, 1, 1));
        tokens.add(new Token(Token.TokenType.WORD, 1, 2, "bob"));
        tokens.add(new Token(Token.TokenType.OPENPAR, 1, 3));
        tokens.add(new Token(Token.TokenType.WORD, 1, 4, "justin"));
        tokens.add(new Token(Token.TokenType.COMMA, 1, 5));
        tokens.add(new Token(Token.TokenType.OPENPAR, 1, 6));

        TokenManager tokenManager = new TokenManager(tokens);
        assertEquals("Optional[Type: FUNCTION]", tokenManager.Peek(0).toString());
        assertEquals("Optional[Type: WORD Value: bob]", tokenManager.Peek(1).toString());
        assertEquals("Optional[Type: OPENPAR]", tokenManager.Peek(2).toString());
        assertEquals("Optional[Type: WORD Value: justin]", tokenManager.Peek(3).toString());
        assertEquals("Optional[Type: COMMA]", tokenManager.Peek(4).toString());
        assertEquals("Optional[Type: OPENPAR]", tokenManager.Peek(5).toString());
        //peeking something that doesn't exist
        assertEquals(tokenManager.Peek(11).toString(), "Optional.empty");


        assertTrue(tokenManager.MoreTokens());

        assertEquals(tokenManager.MatchAndRemove(Token.TokenType.FUNCTION).toString(), "Optional[Type: FUNCTION]");
        assertEquals(tokenManager.MatchAndRemove(Token.TokenType.WORD).toString(), "Optional[Type: WORD Value: bob]");
        assertEquals(tokenManager.MatchAndRemove(Token.TokenType.OPENPAR).toString(), "Optional[Type: OPENPAR]");
        assertEquals(tokenManager.MatchAndRemove(Token.TokenType.WORD).toString(), "Optional[Type: WORD Value: justin]");
        assertEquals(tokenManager.MatchAndRemove(Token.TokenType.COMMA).toString(), "Optional[Type: COMMA]");
        //Tests if they were actually removed
        assertEquals(tokens.toString(), "[Type: OPENPAR]");
        assertEquals(tokenManager.MatchAndRemove(Token.TokenType.OPENPAR).toString(), "Optional[Type: OPENPAR]");
        //All Tokens should be removed.
        assertFalse(tokenManager.MoreTokens());


    }

    @Test
    public void testParseAction() {
        LinkedList tokens = new LinkedList<Token>();
        tokens.add(new Token(Token.TokenType.BEGIN, 1, 1));
        tokens.add(new Token(Token.TokenType.END, 1, 2));
        tokens.add(new Token(Token.TokenType.WORD, 1, 3));

        Parser parser = new Parser(tokens);
        ProgramNode programNode = new ProgramNode();
        //Begin token
        boolean result = parser.parseAction(programNode);
        assertTrue(result);

        ;

    }

    @Test
    public void testProgramNode() {
        LinkedList tokens = new LinkedList<Token>();
        tokens.add(new Token(Token.TokenType.FUNCTION, 1, 1));
        tokens.add(new Token(Token.TokenType.WORD, 1, 2, "bob"));
        tokens.add(new Token(Token.TokenType.OPENPAR, 1, 3));
        tokens.add(new Token(Token.TokenType.WORD, 1, 4, "jack"));
        tokens.add(new Token(Token.TokenType.SEPARATOR, 1, 5));
        tokens.add(new Token(Token.TokenType.COMMA, 1, 6));
        tokens.add(new Token(Token.TokenType.WORD, 1, 7, "tom"));
        tokens.add(new Token(Token.TokenType.CLOSEPAR, 1, 8));
        tokens.add(new Token(Token.TokenType.SEPARATOR, 1, 9));

        Parser parser = new Parser(tokens);
        ProgramNode programNode = parser.Parse();
        assertEquals("[FunctionDefinitionNode: \n" +
                " Name:bob\n" +
                " parameters:[Type: WORD Value: jack, Type: WORD Value: tom]\n" +
                " statements:[]\n" +
                "]", programNode.getFunctionDefinitionNodes().toString());


    }

    //TESTING FOR PARSER 2
    @Test
    public void testParseOperationGeneralUse() {
        Lexer lexer = new Lexer("$7");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var pars = parser.ParseOperation();
        assertEquals("Optional[Left:7 Operation Type: DOLLAR]", pars.toString());
    }

    @Test
    public void testParseOperationInc() {
        Lexer lexer = new Lexer("++a");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var pars = parser.ParseOperation();
        assertEquals("Optional[a Left:a Operation Type: PREINC]", pars.toString());
    }

    @Test
    public void testParseOperationDollarInc() {
        Lexer lexer = new Lexer("++$b");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var pars = parser.ParseOperation();
        assertEquals("Optional[Left:b Operation Type: DOLLAR Left:Left:b Operation Type: DOLLAR Operation Type: PREINC]", pars.toString());
    }

    @Test
    public void testEqMath() {
        Lexer lexer = new Lexer("b^=3");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var pars = parser.ParseOperation();
        assertEquals("Optional[Left:b Operation Type: EXPONENT Right: Optional[3] 3]", pars.toString());
    }

    @Test
    public void testParseOperationDec() {
        Lexer lexer = new Lexer("--a");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var pars = parser.ParseOperation();
        assertEquals("Optional[a Left:a Operation Type: PREDEC]", pars.toString());
    }

    @Test
    public void testParseOperationDollarDec() {
        Lexer lexer = new Lexer("--$b");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var pars = parser.ParseOperation();
        assertEquals("Optional[Left:b Operation Type: DOLLAR Left:Left:b Operation Type: DOLLAR Operation Type: PREDEC]", pars.toString());
    }

    @Test
    public void testParseOperationParentheses() {
        Lexer lexer = new Lexer("(++d)");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var pars = parser.ParseOperation();
        assertEquals("Optional[d Left:d Operation Type: PREINC]", pars.toString());
    }

    @Test
    public void testParseOperationNegativeNum() {
        Lexer lexer = new Lexer("-5");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var pars = parser.ParseOperation();
        assertEquals("Optional[Left:5 Operation Type: UNARYNEG]", pars.toString());
    }

    @Test
    public void testParseOperationPattern() {
        Lexer lexer = new Lexer("\"[abc]\"");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var pars = parser.ParseOperation();
        assertEquals("Optional[[abc]]", pars.toString());
    }

    @Test
    public void testParseOperationVarAndIncrement() {
        Lexer lexer = new Lexer("e[++b]");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var pars = parser.ParseOperation();
        assertEquals("Optional[Name: e Expression: Optional[b Left:b Operation Type: PREINC]]", pars.toString());
    }

    @Test
    public void testParseOperationDollarNumber() {
        Lexer lexer = new Lexer("$7");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var pars = parser.ParseOperation();
        assertEquals("Optional[Left:7 Operation Type: DOLLAR]", pars.toString());
    }

    @Test
    public void testParseOperationNestedIncrement() {
        Lexer lexer = new Lexer("++(++d)");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var pars = parser.ParseOperation();
        assertEquals("Optional[d Left:d Operation Type: PREINC Left:d Left:d Operation Type: PREINC Operation Type: PREINC]", pars.toString());
    }

    @Test
    public void testParseOperationAlotOfParenthesis() {
        Lexer lexer = new Lexer("(((abc)))");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var pars = parser.ParseOperation();
        assertEquals("Optional[abc]", pars.toString());
    }

    @Test
    public void testParseOperationNot() {
        Lexer lexer = new Lexer("!abc");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var pars = parser.ParseOperation();
        assertEquals("Optional[Left:abc Operation Type: NOT]", pars.toString());
    }

    @Test
    public void testParseOperationDoubleNOT() {
        Lexer lexer = new Lexer("!!abc");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var pars = parser.ParseOperation();
        assertEquals("Optional[Left:Left:abc Operation Type: NOT Operation Type: NOT]", pars.toString());
    }

    @Test
    public void testParseOperationsThrows() {
        //missing parenthesis should throw error
        Lexer lexer = new Lexer("(abc");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        assertThrows(Exception.class, () -> {
                    var pars = parser.ParseOperation();

                }
        );

    }

    @Test
    public void testParseOperationsExtreme() {
        Lexer lexer = new Lexer("$--t[++f]");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var pars = parser.ParseOperation();
        assertEquals("Optional[Left:Name: t Expression: Optional[f Left:f Operation Type: PREINC] Left:Name: t Expression: Optional[f Left:f Operation Type: PREINC] Operation Type: PREDEC Operation Type: DOLLAR]", pars.toString());
    }

    @Test
    public void testAdditionI() {
        Lexer lexer = new Lexer("a + b");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.ParseOperation();
        assertEquals("Optional[Left:a Operation Type: ADD Right: Optional[b]]", result.toString());
    }

    @Test
    public void testSubtraction() {
        Lexer lexer = new Lexer("x - y");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.ParseOperation();
        assertEquals("Optional[Left:x Operation Type: SUBTRACT Right: Optional[y]]", result.toString());
    }


    @Test
    public void testMultiplication() {
        Lexer lexer = new Lexer("m * n");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.ParseOperation();
        assertEquals("Optional[Left:m Operation Type: MULTIPLY Right: Optional[n]]", result.toString());
    }

    @Test
    public void testDivision() {
        Lexer lexer = new Lexer("p / q");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.ParseOperation();
        assertEquals("Optional[Left:p Operation Type: DIVIDE Right: Optional[q]]", result.toString());
    }

    @Test
    public void testExponentiation() {
        Lexer lexer = new Lexer("base ^ exponent");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.ParseOperation();
        assertEquals("Optional[Left:base Operation Type: EXPONENT Right: Optional[exponent]]", result.toString());
    }

    @Test
    public void testConcatenation() {
        Lexer lexer = new Lexer("str1 str2");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.ParseOperation();
        assertEquals("Optional[Left:str1 Operation Type: CONCATENATION Right: Optional[str2]]", result.toString());
    }

    @Test
    public void testTernaryConditionalExpression() {
        Lexer lexer = new Lexer("x ? t : z");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.ParseOperation();
        assertEquals("Optional[Condition: x Truecase:t Falsecase:z]", result.toString());
    }

    @Test
    public void testMatchOperation() {
        Lexer lexer = new Lexer("text ~ pattern");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.ParseOperation();
        assertEquals("Optional[Left:text Operation Type: MATCH Right: Optional[pattern]]", result.toString());
    }

    @Test
    public void testLogicalAnd() {
        Lexer lexer = new Lexer("x && y");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.ParseOperation();
        assertEquals("Optional[Left:x Operation Type: AND Right: Optional[y]]", result.toString());
    }

    @Test
    public void testLogicalOr() {
        Lexer lexer = new Lexer("a || b");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.ParseOperation();
        assertEquals("Optional[Left:a Operation Type: OR Right: Optional[b]]", result.toString());
    }

    @Test
    public void testArrayMembership() {
        Lexer lexer = new Lexer("s[2+4]");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.ParseOperation();
        assertEquals("Optional[Name: s Expression: Optional[Left:2 Operation Type: ADD Right: Optional[4]]]", result.toString());
    }

    @Test
    public void testEdgeCaseParser3() {
        Lexer lexer = new Lexer("(a%-x) + (4*2)^5");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.ParseOperation();
        assertEquals("Optional[Left:Left:a Operation Type: MODULO Right: Optional[Left:x Operation Type: UNARYNEG] Operation Type: ADD Right: Optional[Left:Left:4 Operation Type: MULTIPLY Right: Optional[2] Operation Type: EXPONENT Right: Optional[5]]]", result.toString());
    }

    @Test
    public void testEdgeCaseParser3pt2() {
        Lexer lexer = new Lexer("(a%-x)?(4*2)^5:2");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.ParseOperation();
        assertEquals("Optional[Condition: Left:a Operation Type: MODULO Right: Optional[Left:x Operation Type: UNARYNEG] Truecase:Left:Left:4 Operation Type: MULTIPLY Right: Optional[2] Operation Type: EXPONENT Right: Optional[5] Falsecase:2]", result.toString());
    }

    @Test
    public void testEdgeCaseParserpt3() {
        Lexer lexer = new Lexer("a + b * (c - d) / (e ^ f)");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.ParseOperation();
        assertEquals("Optional[Left:a Operation Type: ADD Right: Optional[Left:Left:b Operation Type: MULTIPLY Right: Optional[Left:c Operation Type: SUBTRACT Right: Optional[d]] Operation Type: DIVIDE Right: Optional[Left:e Operation Type: EXPONENT Right: Optional[f]]]]", result.toString());
    }

    @Test
    public void testUnaryNegation() {
        Lexer lexer = new Lexer("-x");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.ParseOperation();
        assertEquals("Optional[Left:x Operation Type: UNARYNEG]", result.toString());
    }

    @Test
    public void PreDec() {
        Lexer lexer = new Lexer("--x");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.ParseOperation();
        assertEquals("Optional[x Left:x Operation Type: PREDEC]", result.toString());
    }

    @Test
    public void PreTest() {
        Lexer lexer = new Lexer("++x");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.ParseOperation();
        assertEquals("Optional[x Left:x Operation Type: PREINC]", result.toString());
    }

    @Test
    public void testAdditionAndMultiplication() {
        Lexer lexer = new Lexer("a + b * c");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.ParseOperation();
        assertEquals("Optional[Left:a Operation Type: ADD Right: Optional[Left:b Operation Type: MULTIPLY Right: Optional[c]]]", result.toString());
    }

    @Test
    public void testParentheses() {
        Lexer lexer = new Lexer("(a + b) * c");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.ParseOperation();
        assertEquals("Optional[Left:Left:a Operation Type: ADD Right: Optional[b] Operation Type: MULTIPLY Right: Optional[c]]", result.toString());
    }

    @Test
    public void testNestedParentheses() {
        Lexer lexer = new Lexer("((a + b) * (c - d)) / (e ^ f)");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.ParseOperation();
        assertEquals("Optional[Left:Left:Left:a Operation Type: ADD Right: Optional[b] Operation Type: MULTIPLY Right: Optional[Left:c Operation Type: SUBTRACT Right: Optional[d]] Operation Type: DIVIDE Right: Optional[Left:e Operation Type: EXPONENT Right: Optional[f]]]", result.toString());
    }

    @Test
    public void testComplexExpression() {
        Lexer lexer = new Lexer("a + (b * c - (d / e) ^ f)");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.ParseOperation();
        assertEquals("Optional[Left:a Operation Type: ADD Right: Optional[Left:Left:b Operation Type: MULTIPLY Right: Optional[c] Operation Type: SUBTRACT Right: Optional[Left:Left:d Operation Type: DIVIDE Right: Optional[e] Operation Type: EXPONENT Right: Optional[f]]]]", result.toString());

    }


    @Test
    public void testIfStatements() {
        Lexer lexer = new Lexer(" if(1){" +
                "x^=2;" +
                "} "

        );
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.parseStatement();
    }

    @Test
    public void testContinue() {
        Lexer lexer = new Lexer("continue;");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.parseStatement();
        assertEquals("Optional[continue]", result.toString());
    }

    @Test
    public void testBreak() {
        Lexer lexer = new Lexer("break;");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.parseStatement();
        assertEquals("Optional[break]", result.toString());
    }

    @Test
    public void testDelete() {
        Lexer lexer = new Lexer("DELETE a[1,2,3];");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.ParseAssignment();
        assertEquals("Optional[Left:DELETE Operation Type: CONCATENATION Right: Optional[Name: a Expression: Optional[1]]]", result.toString());
    }

    @Test
    public void dotestWhile() {
        Lexer lexer = new Lexer("do{" +
                "y++" +
                "}" +
                "while (x<1)");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.parseStatement();
        assertEquals(result.toString(), "Optional[DoWhileNode: {Condition: xBody: [y Left:y Operation Type: POSTINC]}]");
    }

    @Test
    public void parsereturn() {
        Lexer lexer = new Lexer("return x");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.parseStatement();
        assertEquals(result.toString(), "Optional[ReturnNode Node: Optional[x]]");
    }

    @Test
    public void parseDelete() {
        Lexer lexer = new Lexer("delete x[4]");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.parseStatement();
        assertEquals(result.toString(), "Optional[delete: Name: x Expression: Optional[4]]");
    }

    @Test
    public void leftvsright() {
        Lexer lexer = new Lexer("2^2^4");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.ParseOperation();
    }

    @Test
    public void testParseFunctioncall() {
        Lexer lexer = new Lexer("method(x,y,z,2);");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.ParseOperation();
        assertEquals("Optional[FunctionCallNode: {Function Name: method Parameters: [x, y, z, 2]]", result.toString());
    }

    @Test
    public void testParseFunctioncallPrintWithPar() {
        Lexer lexer = new Lexer("print(\"bob\");");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.ParseOperation();
        assertEquals("Optional[FunctionCallNode: {Function Name: print Parameters: [bob]]", result.toString());
    }

    @Test
    public void testParseFunctioncallPrint() {
        Lexer lexer = new Lexer("print\"bob\";");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.ParseOperation();
        assertEquals("Optional[FunctionCallNode: {Function Name: print Parameters: [bob]]", result.toString());
    }

    @Test
    public void testParseFunctioncallPrintF() {
        Lexer lexer = new Lexer("printf(\"this is a string\" , $1);");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.ParseOperation();
        assertEquals("Optional[FunctionCallNode: {Function Name: printf Parameters: [this is a string, Left:1 Operation Type: DOLLAR]]", result.toString());
    }

    @Test
    public void testParseFunctioncallPrintFVar() {
        Lexer lexer = new Lexer("printf(\"this is a string\" , x,y,z);");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.ParseOperation();
        assertEquals("Optional[FunctionCallNode: {Function Name: printf Parameters: [this is a string, x, y, z]]", result.toString());
    }

    @Test
    public void testFunctionCallGetlineNoPar() {
        Lexer lexer = new Lexer("getline x   ");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.ParseOperation();
        assertEquals("Optional[FunctionCallNode: {Function Name: getline Parameters: [x]]", result.toString());
    }

    @Test
    public void testFunctionCallGetlinewithPar() {
        Lexer lexer = new Lexer("getline(x)");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.ParseOperation();
        assertEquals("Optional[FunctionCallNode: {Function Name: getline Parameters: [x]]", result.toString());
    }

    @Test
    public void testSplitAndAssign() throws IOException {
        // Create a test input
        List<String> input = new ArrayList<>();
        input.add("John Doe 25");
        input.add("Jane Smith 30");
        input.add("Alice Johnson 28");
        Interpreter interpreter = new Interpreter(new ProgramNode(), filePath);
        interpreter.lineManager = interpreter.new LineManager(input);
        boolean success = interpreter.lineManager.SplitAndAssign();
        assertTrue(success);
        assertEquals("John", interpreter.globalVariables.get("$0").toString());
        assertEquals("Doe", interpreter.globalVariables.get("$1").toString());
        assertEquals("25", interpreter.globalVariables.get("$2").toString());
        assertEquals("3", interpreter.globalVariables.get("NF").toString());
        assertEquals("1", interpreter.globalVariables.get("NR").toString());
        assertEquals("1", interpreter.globalVariables.get("FNR").toString());
        success = interpreter.lineManager.SplitAndAssign();
        assertTrue(success);
        assertEquals("Jane", interpreter.globalVariables.get("$0").toString());
        assertEquals("Smith", interpreter.globalVariables.get("$1").toString());
        assertEquals("30", interpreter.globalVariables.get("$2").toString());
        assertEquals("3", interpreter.globalVariables.get("NF").toString());
        assertEquals("2", interpreter.globalVariables.get("NR").toString());
        assertEquals("2", interpreter.globalVariables.get("FNR").toString());
        success = interpreter.lineManager.SplitAndAssign();
        assertTrue(success);
        assertEquals("Alice", interpreter.globalVariables.get("$0").toString());
        assertEquals("Johnson", interpreter.globalVariables.get("$1").toString());
        assertEquals("28", interpreter.globalVariables.get("$2").toString());
        assertEquals("3", interpreter.globalVariables.get("NF").toString());
        assertEquals("3", interpreter.globalVariables.get("NR").toString());
        assertEquals("3", interpreter.globalVariables.get("FNR").toString());
        success = interpreter.lineManager.SplitAndAssign();

        assertFalse(success);
        assertFalse(success);
    }

    @Test
    public void testPrintFunction() throws IOException {
        ByteArrayOutputStream Output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(Output));
        ProgramNode program = new ProgramNode();
        HashMap<String, InterpreterDataType> parameters = new HashMap<>();
        parameters.put("param1", new InterpreterDataType("testing"));
        parameters.put("param2", new InterpreterDataType("print"));
        Interpreter interpreter = new Interpreter(program, filePath);
        BuiltInFunctionDefinitionNode printFunction = (BuiltInFunctionDefinitionNode) interpreter.functioncall.get("print");
        printFunction.execute(parameters);
        System.setOut(System.out);
        String printedOutput = Output.toString().trim();
        assertEquals("testing print", printedOutput);
    }

    @Test
    public void testPrintfFunction() throws IOException {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
        Interpreter interpreter = new Interpreter(new ProgramNode(), filePath); // Initialize the interpreter as needed
        BuiltInFunctionDefinitionNode printfFunction = (BuiltInFunctionDefinitionNode) interpreter.functioncall.get("printf");
        HashMap<String, InterpreterDataType> parameters = new HashMap<>();
        parameters.put("0", new InterpreterDataType("this is a %s cool project %s"));
        parameters.put("1", new InterpreterDataType("very"));
        parameters.put("2", new InterpreterDataType("100"));
        printfFunction.execute(parameters);
        System.setOut(originalOut);
        String printedOutput = output.toString().trim();
        assertEquals("this is a very cool project 100", printedOutput);
    }

    @Test
    public void testgetLineWithParameters() throws IOException {
        // Mute output and then store it for testing.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
        Interpreter interpreter = new Interpreter(new ProgramNode(), filePath); // Initialize the interpreter as needed
        BuiltInFunctionDefinitionNode getlineFunction = (BuiltInFunctionDefinitionNode) interpreter.functioncall.get("getline");
        // Define parameters
        HashMap<String, InterpreterDataType> parameters = new HashMap<>();
        parameters.put("param1", new InterpreterDataType("value1"));
        parameters.put("param2", new InterpreterDataType("value2"));
        InterpreterDataType result = getlineFunction.execute(parameters);
        System.setOut(System.out);
        String printedOutput = output.toString().trim();
        assertEquals("1", result.toString());
    }

    @Test
    public void testNextFunction() throws IOException {
        Interpreter interpreter = new Interpreter(new ProgramNode(), filePath); // Initialize the interpreter as needed
        BuiltInFunctionDefinitionNode nextFunction = (BuiltInFunctionDefinitionNode) interpreter.functioncall.get("next");
        HashMap<String, InterpreterDataType> parameters = new HashMap<>();
        InterpreterDataType result = nextFunction.execute(parameters);
        assertEquals("", result.toString());
    }

    @Test
    public void testSubFunction() throws IOException {
        Interpreter interpreter = new Interpreter(new ProgramNode(), filePath); // Initialize the interpreter as needed
        BuiltInFunctionDefinitionNode subFunction = (BuiltInFunctionDefinitionNode) interpreter.functioncall.get("sub");
        HashMap<String, InterpreterDataType> parameters = new HashMap<>();
        parameters.put("0", new InterpreterDataType("abcabcabc"));
        parameters.put("1", new InterpreterDataType("ab"));
        parameters.put("2", new InterpreterDataType("xy"));
        InterpreterDataType result = subFunction.execute(parameters);
        assertEquals("1", result.toString());
    }

    @Test
    public void testIndexFunction() throws IOException {
        Interpreter interpreter = new Interpreter(new ProgramNode(), filePath); // Initialize the interpreter as needed
        BuiltInFunctionDefinitionNode indexFunction = (BuiltInFunctionDefinitionNode) interpreter.functioncall.get("index");
        HashMap<String, InterpreterDataType> parameters = new HashMap<>();
        parameters.put("input", new InterpreterDataType("Hello, World"));
        parameters.put("substring", new InterpreterDataType("World"));
        InterpreterDataType result = indexFunction.execute(parameters);
        //My index starts at 1 bc awk is dumb and starts at 1.
        assertEquals("8", result.toString());
    }

    @Test
    public void testToLowerFunction() throws IOException {
        Interpreter interpreter = new Interpreter(new ProgramNode(), filePath);
        BuiltInFunctionDefinitionNode tolowerFunction = (BuiltInFunctionDefinitionNode) interpreter.functioncall.get("tolower");
        HashMap<String, InterpreterDataType> parameters = new HashMap<>();
        parameters.put("input", new InterpreterDataType("Hello World"));
        InterpreterDataType result = tolowerFunction.execute(parameters);
        assertEquals("hello world", result.toString());
        assertEquals("hello world", interpreter.globalVariables.get("result").toString());
    }

    @Test
    public void testToUpperFunction() throws IOException {
        Interpreter interpreter = new Interpreter(new ProgramNode(), filePath);
        BuiltInFunctionDefinitionNode toupperFunction = (BuiltInFunctionDefinitionNode) interpreter.functioncall.get("toupper");
        HashMap<String, InterpreterDataType> parameters = new HashMap<>();
        parameters.put("input", new InterpreterDataType("Hello World"));
        InterpreterDataType result = toupperFunction.execute(parameters);
        assertEquals("HELLO WORLD", result.toString());
        assertEquals("HELLO WORLD", interpreter.globalVariables.get("result").toString());
    }

    @Test
    public void testLengthFunctionWithEmptyInput() throws IOException {
        Interpreter interpreter = new Interpreter(new ProgramNode(), filePath);
        BuiltInFunctionDefinitionNode lengthFunction = (BuiltInFunctionDefinitionNode) interpreter.functioncall.get("length");
        HashMap<String, InterpreterDataType> parameters = new HashMap<>();
        parameters.put("input", new InterpreterDataType(""));
        InterpreterDataType result = lengthFunction.execute(parameters);
        assertEquals("0", result.toString());
        assertEquals("0", interpreter.globalVariables.get("result").toString());
    }

    @Test
    public void testGsubFunction() throws IOException {
        Interpreter interpreter = new Interpreter(new ProgramNode(), filePath);
        BuiltInFunctionDefinitionNode gsubFunction = (BuiltInFunctionDefinitionNode) interpreter.functioncall.get("gsub");
        HashMap<String, InterpreterDataType> parameters = new HashMap<>();
        parameters.put("0", new InterpreterDataType("abcabcabc"));
        parameters.put("1", new InterpreterDataType("ab"));
        parameters.put("2", new InterpreterDataType("xy"));
        InterpreterDataType result = gsubFunction.execute(parameters);
        assertEquals("1", result.toString());
        assertEquals("xycxycxyc", interpreter.globalVariables.get("$0").toString());
    }

    @Test
    public void testMatchFunction() throws IOException {
        Interpreter interpreter = new Interpreter(new ProgramNode(), filePath);
        BuiltInFunctionDefinitionNode matchFunction = (BuiltInFunctionDefinitionNode) interpreter.functioncall.get("match");
        HashMap<String, InterpreterDataType> parameters = new HashMap<>();
        parameters.put("0", new InterpreterDataType("Hello, World"));
        parameters.put("1", new InterpreterDataType("World"));
        InterpreterDataType result = matchFunction.execute(parameters);
        assertEquals("8", result.toString());
        assertEquals("World", interpreter.globalVariables.get("$0").toString());
    }

    @Test
    public void testWhile2() {
        Lexer lexer = new Lexer("while(x<y){" +
                "x++" +
                "}");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.parseStatement();
        assertEquals("Optional[WhileNode: {\n" +
                "Condition: x\n" +
                "Body: [x Left:x Operation Type: POSTINC]\n" +
                "}]", result.toString());
    }

    @Test
    public void testIf() {
        Lexer lexer = new Lexer("if(x<y){" +
                "x=y;" +
                "}");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.parseStatement();
        assertEquals("Optional[If: Optional[x] \n" +
                "[x Left:x Operation Type: ASSIGN Right: Optional[y]]]", result.toString());
    }

    @Test
    public void testIfelse() {
        Lexer lexer = new Lexer("if(x<y){" +
                "x=y;" +
                "}" +
                "else{" +
                "y=x" +
                "}");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.parseStatement();
        assertEquals("Optional[If: Optional[x] \n" +
                "[x Left:x Operation Type: ASSIGN Right: Optional[y]]\n" +
                " else: If: Optional.empty \n" +
                "[y Left:y Operation Type: ASSIGN Right: Optional[x]]]", result.toString());
    }

    @Test
    public void testIfelseIf() {
        Lexer lexer = new Lexer("if(x<y){" +
                "x=y;" +
                "}" +
                "else if(x==y){" +
                "y=x" +
                "}" +
                "else{" +
                "x++" +
                "}");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.parseStatement();
        assertEquals("Optional[If: Optional[x] \n" +
                "[x Left:x Operation Type: ASSIGN Right: Optional[y]]\n" +
                " else: If: Optional[Left:x Operation Type: EQ Right: Optional[y]] \n" +
                "[y Left:y Operation Type: ASSIGN Right: Optional[x]]\n" +
                " else: If: Optional.empty \n" +
                "[x Left:x Operation Type: POSTINC]]", result.toString());
    }

    @Test
    public void testSuperNestedIf() {
        Lexer lexer = new Lexer("if(x>0){" +
                "if(x!=y){" +
                "if(x+y>0){" +
                "x=y;" +
                "}" +
                "}" +
                "else{" +
                "y=x" +
                "}" +
                "}");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.parseStatement();
        assertEquals("Optional[If: Optional[Left:x Operation Type: GT Right: Optional[0]] \n" +
                "[If: Optional[Left:x Operation Type: NE Right: Optional[y]] \n" +
                "[If: Optional[Left:Left:x Operation Type: ADD Right: Optional[y] Operation Type: GT Right: Optional[0]] \n" +
                "[x Left:x Operation Type: ASSIGN Right: Optional[y]]]\n" +
                " else: If: Optional.empty \n" +
                "[y Left:y Operation Type: ASSIGN Right: Optional[x]]]]", result.toString());
    }

    @Test
    public void testConstant() throws IOException {
        OperationNode node = new OperationNode(new ConstantNode("3"), OperationNode.type.GE, Optional.of(new ConstantNode("4")));
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(node, hashMap);
        assertEquals("false", data.toString());
    }

    @Test
    public void StringtestGEFalse() throws IOException {
        OperationNode constantNode = new OperationNode(new ConstantNode("apple"), OperationNode.type.GE, Optional.of(new ConstantNode("banana")));
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(constantNode, hashMap);
        assertEquals("false", data.toString());
    }

    @Test
    public void StringtestGETrue() throws IOException {
        OperationNode constantNode = new OperationNode(new ConstantNode("robbie"), OperationNode.type.GE, Optional.of(new ConstantNode("four")));
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(constantNode, hashMap);
        assertEquals("true", data.toString());
    }

    @Test
    public void StringtestLEFalse() throws IOException {
        OperationNode constantNode = new OperationNode(new ConstantNode("bob"), OperationNode.type.LE, Optional.of(new ConstantNode("aa")));
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(constantNode, hashMap);
        assertEquals("false", data.toString());
    }

    @Test
    public void StringtestLETrue() throws IOException {
        OperationNode constantNode = new OperationNode(new ConstantNode("four"), OperationNode.type.LE, Optional.of(new ConstantNode("four")));
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(constantNode, hashMap);
        assertEquals("true", data.toString());
    }

    @Test
    public void StringtestEQFalse() throws IOException {
        OperationNode constantNode = new OperationNode(new ConstantNode("four"), OperationNode.type.EQ, Optional.of(new ConstantNode("five")));
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(constantNode, hashMap);
        assertEquals("false", data.toString());
    }

    @Test
    public void StringtestEQTrue() throws IOException {
        OperationNode constantNode = new OperationNode(new ConstantNode("six"), OperationNode.type.EQ, Optional.of(new ConstantNode("six")));
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(constantNode, hashMap);
        assertEquals("true", data.toString());
    }

    @Test
    public void StringtestNEFalse() throws IOException {
        OperationNode constantNode = new OperationNode(new ConstantNode("four"), OperationNode.type.NE, Optional.of(new ConstantNode("four")));
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(constantNode, hashMap);
        assertEquals("false", data.toString());
    }

    @Test
    public void StringtestNETrue() throws IOException {
        OperationNode constantNode = new OperationNode(new ConstantNode("five"), OperationNode.type.NE, Optional.of(new ConstantNode("four")));
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(constantNode, hashMap);
        assertEquals("true", data.toString());
    }

    @Test
    public void testGEFalse() throws IOException {
        OperationNode constantNode = new OperationNode(new ConstantNode("3"), OperationNode.type.GE, Optional.of(new ConstantNode("4")));
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(constantNode, hashMap);
        assertEquals("false", data.toString());
    }

    @Test
    public void testGETrue() throws IOException {
        OperationNode constantNode = new OperationNode(new ConstantNode("9"), OperationNode.type.GE, Optional.of(new ConstantNode("4")));
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(constantNode, hashMap);
        assertEquals("true", data.toString());
    }

    @Test
    public void testLEFalse() throws IOException {
        OperationNode constantNode = new OperationNode(new ConstantNode("5"), OperationNode.type.LE, Optional.of(new ConstantNode("4")));
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(constantNode, hashMap);
        assertEquals("false", data.toString());
    }

    @Test
    public void testLETrue() throws IOException {
        OperationNode constantNode = new OperationNode(new ConstantNode("4"), OperationNode.type.LE, Optional.of(new ConstantNode("4")));
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(constantNode, hashMap);
        assertEquals("true", data.toString());
    }

    @Test
    public void testEQFalse() throws IOException {
        OperationNode constantNode = new OperationNode(new ConstantNode("5"), OperationNode.type.EQ, Optional.of(new ConstantNode("4")));
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(constantNode, hashMap);
        assertEquals("false", data.toString());
    }

    @Test
    public void testEQTrue() throws IOException {
        OperationNode constantNode = new OperationNode(new ConstantNode("6"), OperationNode.type.EQ, Optional.of(new ConstantNode("6")));
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(constantNode, hashMap);
        assertEquals("true", data.toString());
    }

    @Test
    public void testNEFalse() throws IOException {
        OperationNode constantNode = new OperationNode(new ConstantNode("4"), OperationNode.type.NE, Optional.of(new ConstantNode("4")));
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(constantNode, hashMap);
        assertEquals("false", data.toString());
    }

    @Test
    public void testNETrue() throws IOException {
        OperationNode constantNode = new OperationNode(new ConstantNode("5"), OperationNode.type.NE, Optional.of(new ConstantNode("4")));
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(constantNode, hashMap);
        assertEquals("true", data.toString());
    }


    @Test
    public void testinterpreterAdd() throws IOException {
        OperationNode constantNode = new OperationNode(new ConstantNode("9"), OperationNode.type.ADD, Optional.of(new ConstantNode("4")));
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(constantNode, hashMap);
        assertEquals("13.0", data.toString());
    }

    @Test
    public void testinterpreterSub() throws IOException {
        OperationNode constantNode = new OperationNode(new ConstantNode("9"), OperationNode.type.SUBTRACT, Optional.of(new ConstantNode("4")));
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(constantNode, hashMap);
        assertEquals("5.0", data.toString());
    }

    @Test
    public void testInterpreterTimes() throws IOException {
        OperationNode timesNode = new OperationNode(new ConstantNode("3"), OperationNode.type.MULTIPLY, Optional.of(new ConstantNode("4")));
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(timesNode, hashMap);
        assertEquals("12.0", data.toString());
    }

    @Test
    public void testInterpreterDivide() throws IOException {
        OperationNode divideNode = new OperationNode(new ConstantNode("12"), OperationNode.type.DIVIDE, Optional.of(new ConstantNode("3")));
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(divideNode, hashMap);
        assertEquals("4.0", data.toString());
    }

    @Test
    public void testInterpreterUnaryNeg() throws IOException {
        OperationNode unary = new OperationNode(new ConstantNode("12"), OperationNode.type.UNARYNEG);
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(unary, hashMap);
        assertEquals("-12.0", data.toString());
    }

    @Test
    public void testInterpreterUnaryPos() throws IOException {
        OperationNode unary = new OperationNode(new ConstantNode("12"), OperationNode.type.UNARYPOS);
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(unary, hashMap);
        assertEquals("12.0", data.toString());
    }

    @Test
    public void testPreinc() throws IOException {
        OperationNode unary = new OperationNode(new ConstantNode("12"), OperationNode.type.PREINC);
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(unary, hashMap);
        assertEquals("13.0", data.toString());
    }

    @Test
    public void testPostinc() throws IOException {
        OperationNode unary = new OperationNode(new ConstantNode("12"), OperationNode.type.POSTINC);
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(unary, hashMap);
        assertEquals("13.0", data.toString());
    }

    @Test
    public void testPreDec() throws IOException {
        OperationNode unary = new OperationNode(new ConstantNode("12"), OperationNode.type.PREDEC);
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(unary, hashMap);
        assertEquals("11.0", data.toString());
    }

    @Test
    public void testPostDec() throws IOException {
        OperationNode unary = new OperationNode(new ConstantNode("12"), OperationNode.type.POSTDEC);
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(unary, hashMap);
        assertEquals("11.0", data.toString());
    }

    @Test
    public void testInterpreterModulo() throws IOException {
        OperationNode moduloNode = new OperationNode(new ConstantNode("10"), OperationNode.type.MODULO, Optional.of(new ConstantNode("3")));
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(moduloNode, hashMap);
        assertEquals("1.0", data.toString());
    }

    @Test
    public void testInterpreterExpo() throws IOException {
        OperationNode expoNode = new OperationNode(new ConstantNode("2"), OperationNode.type.EXPONENT, Optional.of(new ConstantNode("3")));
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(expoNode, hashMap);
        assertEquals("8.0", data.toString());
    }

    @Test
    public void testInterpreterTeranry() throws IOException {
        OperationNode constantNode = new OperationNode(new ConstantNode("9"), OperationNode.type.GE, Optional.of(new ConstantNode("4")));
        ConstantNode truecase = new ConstantNode("1");
        ConstantNode falsecase = new ConstantNode("0");
        TernaryNode ternaryNode = new TernaryNode(constantNode, truecase, falsecase);
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(ternaryNode, hashMap);
        assertEquals("1", data.toString());
    }


    @Test
    public void testPatternNode() throws IOException {
        var token = new Token(Token.TokenType.PATTERN, 1, 1);
        PatternNode patternNode = new PatternNode(token);
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        //you cant pass a pattern to a function or an assignment
        assertThrows(RuntimeException.class, () -> {
            interpreter.GetIDT(patternNode, new HashMap<>());
        });
    }

    @Test
    public void testLetterWithDollarOperation() throws IOException {
        OperationNode dollarOperationNode = new OperationNode(new ConstantNode("a"), OperationNode.type.DOLLAR);
        Interpreter interpreter = new Interpreter(new ProgramNode(), null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType result = interpreter.GetIDT(dollarOperationNode, hashMap);
        assertEquals("a", hashMap.get("a").toString());
        assertEquals("$a", result.toString());
    }

    @Test
    public void testNumWithDollarOperation() throws IOException {
        OperationNode dollarOperationNode = new OperationNode(new ConstantNode("7"), OperationNode.type.DOLLAR);
        Interpreter interpreter = new Interpreter(new ProgramNode(), null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType result = interpreter.GetIDT(dollarOperationNode, hashMap);
        assertEquals("7", hashMap.get("7").toString());
        assertEquals("$7", result.toString());
    }

    @Test
    public void testIn() throws IOException {
        OperationNode andNode = new OperationNode(new ConstantNode("5"), OperationNode.type.AND, Optional.of(new ConstantNode("1")));
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(andNode, hashMap);
        assertEquals("1", data.toString());
    }

    @Test
    public void testMatchTrue() throws IOException {
        Token token = new Token(Token.TokenType.STRINGLITERAL, 1, 1, "World");
        OperationNode matchNode = new OperationNode(new ConstantNode("World"), OperationNode.type.MATCH, Optional.of(new PatternNode(token)));
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(matchNode, hashMap);
        assertEquals("1", data.toString());
    }

    @Test
    public void testMatchFalse() throws IOException {
        Token token = new Token(Token.TokenType.STRINGLITERAL, 1, 1, "jack");
        OperationNode notNode = new OperationNode(new ConstantNode("hello "), OperationNode.type.MATCH, Optional.of(new PatternNode(token)));
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(notNode, hashMap);
        assertEquals("0", data.toString());
    }

    @Test
    public void testNotMatchTrue() throws IOException {
        OperationNode notMatchNode = new OperationNode(new ConstantNode("Hello, World!"), OperationNode.type.NOTMATCH, Optional.of(new ConstantNode("bob")));
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(notMatchNode, hashMap);
        assertEquals("1", data.toString());
    }

    @Test
    public void testNotMatchFalse() throws IOException {
        OperationNode notMatchNode = new OperationNode(new ConstantNode("world"), OperationNode.type.NOTMATCH, Optional.of(new ConstantNode("world")));
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(notMatchNode, hashMap);
        assertEquals("1", data.toString());
    }


    @Test
    public void VariableReference() throws IOException {
        OperationNode andNode = new OperationNode(new ConstantNode("0"), OperationNode.type.AND, Optional.of(new ConstantNode("0")));
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(andNode, hashMap);
        assertEquals("0", data.toString());
    }


    @Test
    public void testANDZero() throws IOException {
        OperationNode andNode = new OperationNode(new ConstantNode("6"), OperationNode.type.AND, Optional.of(new ConstantNode("1")));
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(andNode, hashMap);
        assertEquals("1", data.toString());
    }


    @Test
    public void testANDNonZero() throws IOException {
        OperationNode andNode = new OperationNode(new ConstantNode("5"), OperationNode.type.AND, Optional.of(new ConstantNode("3")));
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(andNode, hashMap);
        assertEquals("1", data.toString());
    }


    @Test
    public void testORZero() throws IOException {
        OperationNode orNode = new OperationNode(new ConstantNode("0"), OperationNode.type.OR, Optional.of(new ConstantNode("0")));
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(orNode, hashMap);
        assertEquals("0", data.toString());
    }


    @Test
    public void testORNonZero() throws IOException {
        OperationNode orNode = new OperationNode(new ConstantNode("5"), OperationNode.type.OR, Optional.of(new ConstantNode("3")));
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(orNode, hashMap);
        assertEquals("1", data.toString());
    }


    @Test
    public void testNOTZero() throws IOException {
        OperationNode notNode = new OperationNode(new ConstantNode("5"), OperationNode.type.NOT);
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(notNode, hashMap);
        assertEquals("1", data.toString());
    }

    @Test
    public void testEmpty() throws IOException {
        OperationNode notNode = new OperationNode(new ConstantNode(""), OperationNode.type.NOT, Optional.of(new ConstantNode("")));
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(notNode, hashMap);
        assertEquals("0", data.toString());
    }


    @Test
    public void testNOTNonZero() throws IOException {
        OperationNode notNode = new OperationNode(new ConstantNode("5"), OperationNode.type.NOT);
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        InterpreterDataType data = interpreter.GetIDT(notNode, hashMap);
        assertEquals("1", data.toString());
    }


    @Test
    public void testConcatenationINT() throws IOException {
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        ConstantNode constantNode1 = new ConstantNode("Hello");
        ConstantNode constantNode2 = new ConstantNode(" World");
        OperationNode concatenationNode = new OperationNode(constantNode1, OperationNode.type.CONCATENATION, Optional.of(constantNode2));
        InterpreterDataType data = interpreter.GetIDT(concatenationNode, hashMap);
        assertEquals("Hello World", data.toString());
    }

    @Test
    public void testFor() {
        Lexer lexer = new Lexer("for(x=0; x<5; x++ ){" +
                "y=x+1;" +
                "}");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.parseStatement();
        assertEquals("Optional[ForNode: {\n" +
                "Initialization: x Left:x Operation Type: ASSIGN Right: Optional[0] \n" +
                "Condition: x \n" +
                "Increment: x Left:x Operation Type: POSTINC \n" +
                "Body: [y Left:y Operation Type: ASSIGN Right: Optional[Left:x Operation Type: ADD Right: Optional[1]]] \n" +
                "}]", result.toString());
    }

    @Test
    public void testForeach() {
        Lexer lexer = new Lexer("for(x in tires){" +
                "y=x+1;" +
                "}");
        var tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        var result = parser.parseStatement();
        assertEquals("Optional[ForEachNode: {\n" +
                "Condition:Optional[Left:x Operation Type: IN Right: Optional[tires]]\n" +
                "Body: [y Left:y Operation Type: ASSIGN Right: Optional[Left:x Operation Type: ADD Right: Optional[1]]]\n" +
                "}]", result.toString());
    }

    @Test
    public void testInterpeterVar() throws IOException {
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        HashMap<String, InterpreterDataType> hashMap = new HashMap<>();
        VariableReferenceNode notNode = new VariableReferenceNode("Var");
        InterpreterDataType data = interpreter.GetIDT(notNode, hashMap);
        assertEquals("Var", data.toString());

    }

    @Test
    public void testArrayVariableWithIndex() throws IOException {
        ProgramNode programNode = new ProgramNode();
        Interpreter interpreter = new Interpreter(programNode, null);
        String arrayName = "myArray";
        InterpreterArrayDataType array = new InterpreterArrayDataType();
        array.addElement("0", new InterpreterDataType("element0"));
        array.addElement("1", new InterpreterDataType("element1"));
        interpreter.globalVariables.put(arrayName, array);

        ConstantNode indexExpression = new ConstantNode("1");
        VariableReferenceNode varRefNode = new VariableReferenceNode(arrayName, Optional.of(indexExpression));
        InterpreterDataType result = interpreter.GetIDT(varRefNode, interpreter.globalVariables);

        assertEquals("element1", result.toString());
    }


}