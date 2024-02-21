import java.util.HashMap;
import java.util.LinkedList;

public class Lexer {
    private StringHandler stringHandler;

    int lineNum;

    int charPos;

    private HashMap<String, Token.TokenType> keywords = new HashMap<>();
    private HashMap<String, Token.TokenType> twoSymbols = new HashMap<>();
    private HashMap<String, Token.TokenType> symbols = new HashMap<>();


    private LinkedList<Token> tokens = new LinkedList<Token>();

    public Lexer(String input) {

        this.stringHandler = new StringHandler(input);

        this.lineNum = 1;

        this.charPos = 1;

        KeywordHashmap();

    }


    /**
     * Creates a linked list of tokens.
     *
     * @return the linked list.
     */
    public LinkedList<Token> lex() {
        boolean check = false;
        while (!stringHandler.isDone()) {
            check = false;
            char next = stringHandler.Peek(0);
            if (next == '#') {
                while (!stringHandler.isDone() && stringHandler.Peek(0) != '\n') {
                    stringHandler.Swallow(1);
                    this.charPos++;
                }
                if (!stringHandler.isDone() && stringHandler.Peek(0) == '\n') {
                    tokens.add(new Token(Token.TokenType.SEPARATOR, lineNum, charPos));
                    stringHandler.Swallow(1);
                    lineNum++;
                    charPos = 1;
                }
                check = true;
            } else if (next == '"') {
                HandleStringLiteral();
                stringHandler.Swallow(1);
                this.charPos++;
                check = true;

            }
            if (next == ' ' || next == '\t') {
                stringHandler.Swallow(1);
                this.charPos++;
                check = true;

            } else if (next == '\n') {
                tokens.add(new Token(Token.TokenType.SEPARATOR, lineNum, charPos));
                stringHandler.Swallow(1);
                lineNum++;
                charPos = 1;
                check = true;

            }
            //Windows Moment.
            else if (next == '\r') {
                stringHandler.Swallow(1);
                this.charPos++;
                check = true;

            } else if (Character.isLetter(next)) {
                Token word = processWord();
                tokens.add(word);
                check = true;


            } else if (Character.isDigit(next) || next == '.') {
                Token number = processNumber();
                tokens.add(number);
                check = true;

            } else if (next == '`') {
                handelPattern();
                stringHandler.Swallow(1);
                this.charPos++;
                check = true;
            } else if (symbols.containsKey(String.valueOf(next))) {
                Token symbol = processSymbols();
                tokens.add(symbol);
                check = true;
            } else if (next == '&') {
                Token symbol = processSymbols();
                tokens.add(symbol);
                check = true;
            } else if (!check) {
                throw new RuntimeException(next + " is an ILLEGAL CHARACTER, JAIL!");
            }

        }
        return tokens;
    }

    /**
     * Deals with the Letter Characters then creates a token.
     *
     * @return a WORD token
     */
    public Token processWord() {
        String word = "";
        int Line = lineNum;
        int Char = charPos;
        while (!stringHandler.isDone()) {
            if ((Character.isLetter(stringHandler.Peek(0)) || Character.isDigit(stringHandler.Peek(0))
                    || stringHandler.Peek(0) == '_')) {
                word += stringHandler.getChar();
                charPos++;
            } else {
                break;
            }
        }
        if (keywords.containsKey(word)) {
            return new Token(keywords.get(word), Line, Char);
        }
        return new Token(Token.TokenType.WORD, Line, Char, word);
    }

    /**
     * Deals with the numerical Characters then creates a token.
     * @return a NUMBER token
     */
    public Token processNumber() {
        String number = "";
        int Line = lineNum;
        int Char = charPos;
        int decimalCheck = 0;
        //Valid Characters are digits and periods(Only one period per a number).
        while (!stringHandler.isDone() && (Character.isDigit(stringHandler.Peek(0)) || (stringHandler.Peek(0) == '.'))) {
            if (Character.isDigit(stringHandler.Peek(0))) {
                number += stringHandler.getChar();
            } else if (stringHandler.Peek(0) == '.') {
                if (decimalCheck == 1) {
                    throw new RuntimeException("Only one decimal point is allowed in a number");
                }
                number += stringHandler.getChar();
                decimalCheck++;
            } else {
                break;
            }
            charPos++;
        }

        return new Token(Token.TokenType.NUMBER, Line, Char, number);
    }

    /**
     * Method to store hashmaps
     */
    public void KeywordHashmap() {
        //put for keywords
        keywords.put("while", Token.TokenType.WHILE);
        keywords.put("if", Token.TokenType.IF);
        keywords.put("do", Token.TokenType.DO);
        keywords.put("for", Token.TokenType.FOR);
        keywords.put("break", Token.TokenType.BREAK);
        keywords.put("continue", Token.TokenType.CONTINUE);
        keywords.put("else", Token.TokenType.ELSE);
        keywords.put("return", Token.TokenType.RETURN);
        keywords.put("BEGIN", Token.TokenType.BEGIN);
        keywords.put("END", Token.TokenType.END);
        keywords.put("print", Token.TokenType.PRINT);
        keywords.put("printf", Token.TokenType.PRINTF);
        keywords.put("next", Token.TokenType.NEXT);
        keywords.put("in", Token.TokenType.IN);
        keywords.put("delete", Token.TokenType.DELETE);
        keywords.put("getline", Token.TokenType.GETLINE);
        keywords.put("exit", Token.TokenType.EXIT);
        keywords.put("nextfile", Token.TokenType.NEXTFILE);
        keywords.put("function", Token.TokenType.FUNCTION);


        //put for  symbols
        symbols.put("{", Token.TokenType.OPENCURLY);
        symbols.put("}", Token.TokenType.CLOSECURLY);
        symbols.put("[", Token.TokenType.OPENBRAC);
        symbols.put("]", Token.TokenType.CLOSEBRAC);
        symbols.put("(", Token.TokenType.OPENPAR);
        symbols.put(")", Token.TokenType.CLOSEPAR);
        symbols.put("$", Token.TokenType.DOLLAR);
        symbols.put("~", Token.TokenType.TILDE);
        symbols.put("=", Token.TokenType.EQUAL);
        symbols.put("<", Token.TokenType.LESS);
        symbols.put(">", Token.TokenType.GREATER);
        symbols.put("!", Token.TokenType.EXPLANATION);
        symbols.put("+", Token.TokenType.PLUS);
        symbols.put("^", Token.TokenType.EXPO);
        symbols.put("-", Token.TokenType.MINUS);
        symbols.put("?", Token.TokenType.QUESTION);
        symbols.put(":", Token.TokenType.COLON);
        symbols.put("*", Token.TokenType.STAR);
        symbols.put("/", Token.TokenType.SLASH);
        symbols.put("%", Token.TokenType.PERCENT);
        symbols.put(";", Token.TokenType.SEPARATOR);
        symbols.put("\n", Token.TokenType.SEPARATOR);
        symbols.put("|", Token.TokenType.BAR);
        symbols.put(",", Token.TokenType.COMMA);

        //put for symbols
        twoSymbols.put(">=", Token.TokenType.GREATEROREQUAL);
        twoSymbols.put("++", Token.TokenType.PLUSX2);
        twoSymbols.put("--", Token.TokenType.MINUSX2);
        twoSymbols.put("<=", Token.TokenType.LESSOREQUAL);
        twoSymbols.put("==", Token.TokenType.EQUALX2);
        twoSymbols.put("!=", Token.TokenType.NOTEQUAL);
        twoSymbols.put("^=", Token.TokenType.EXPOMATH);
        twoSymbols.put("%=", Token.TokenType.PERCENTMATH);
        twoSymbols.put("*=", Token.TokenType.STARMATH);
        twoSymbols.put("/=", Token.TokenType.DIVIDEMATH);
        twoSymbols.put("+=", Token.TokenType.SUM);
        twoSymbols.put("-=", Token.TokenType.MINUSMATH);
        twoSymbols.put("!~", Token.TokenType.NOTMATCH);
        twoSymbols.put("&&", Token.TokenType.AND);
        twoSymbols.put(">>", Token.TokenType.APPEND);
        twoSymbols.put("||", Token.TokenType.OR);

    }

    /**
     * Deals with String Literals.
     */
    public void HandleStringLiteral() {
        String word = "";
        int LineOfHSL = lineNum;
        int startOfHSL = charPos;

        stringHandler.Swallow(1);
        charPos++;

        while (!stringHandler.isDone()) {
            char next = stringHandler.Peek(0);
            if (next == '\\') {
                word += stringHandler.getChar();
                charPos++;
                if (!stringHandler.isDone() && stringHandler.Peek(0) == '"') {
                    word += stringHandler.getChar();
                    charPos++;
                }
            } else if (next == '"') {
                stringHandler.Swallow(1);
                charPos++;
                tokens.add(new Token(Token.TokenType.STRINGLITERAL, LineOfHSL, startOfHSL, word));
                return;
            } else {
                word += stringHandler.getChar();
                charPos++;
            }
        }
        throw new RuntimeException("MISSING QUOTE AT THE END");
    }

    /**
     * Deals with patterns
     */
    public void handelPattern() {
        String word = "";
        int LineOfHSL = lineNum;
        int startOfHSL = charPos;

        stringHandler.Swallow(1);
        charPos++;

        while (!stringHandler.isDone()) {
            char next = stringHandler.Peek(0);
            if (next == '\\') {
                word += stringHandler.getChar();
                charPos++;
                if (!stringHandler.isDone() && stringHandler.Peek(0) == '`') {
                    word += stringHandler.getChar();
                    charPos++;
                }
            } else if (next == '`') {
                stringHandler.Swallow(1);
                charPos++;
                tokens.add(new Token(Token.TokenType.PATTERN, LineOfHSL, startOfHSL, word));
                return;
            } else {
                word += stringHandler.getChar();
                charPos++;
            }
        }
        throw new RuntimeException("MISSING BACK TICK AT THE END");
    }

    /**
     * Process symbols.
     *
     * @return token with symbol or null if there's no symbol.
     */
    public Token processSymbols() {
        char next = stringHandler.Peek(0);
        String Stringsymbols;
        if (stringHandler.reminader().length() >= 2) {
            if (twoSymbols.containsKey(stringHandler.PeektoString(2))) {
                Stringsymbols = stringHandler.PeektoString(2);
                stringHandler.Swallow(2);
                charPos += 2;
                return new Token(twoSymbols.get(Stringsymbols), lineNum, charPos);
            }

        }
        if (next == '&') {
            throw new RuntimeException("ONE & IS NOT ALLOWED");
        }
        if (symbols.containsKey(String.valueOf(next))) {
            String Stringsymbol = String.valueOf(next);
            stringHandler.Swallow(1);
            charPos++;
            return new Token(symbols.get(String.valueOf(next)), lineNum, charPos);

        }
        return null;
    }


}
