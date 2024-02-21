public class Token {
    TokenType tokenType;
    String value = null;
    private int lineNum;
    private int charPos;

    //{ } [ ] ( ) $ ~ = < > !  + ^ - ?  : * / % ; \n | ,
    public enum TokenType {
        WORD, NUMBER, SEPARATOR, WHILE, IF, DO, BREAK, CONTINUE,
        ELSE, RETURN, BEGIN, END, PRINT, PRINTF, NEXT, IN, DELETE, GETLINE, EXIT, NEXTFILE, FUNCTION, STRINGLITERAL, PATTERN, OPENCURLY, CLOSECURLY, OPENPAR, CLOSEPAR, DOLLAR, TILDE, EQUAL, LESS,
        GREATER, EXPLANATION, PLUS, EXPO, MINUS, QUESTION, COLON, STAR, SLASH, PERCENT, BAR, COMMA, OPENBRAC, CLOSEBRAC, FOR, GREATEROREQUAL, PLUSX2, MINUSX2, LESSOREQUAL, EQUALX2, NOTEQUAL, EXPOMATH, PERCENTMATH, DIVIDEMATH, SUM, NOTMATCH, AND, APPEND, OR, STARMATH, MINUSMATH
    }


    public Token(TokenType tokenType, int lineNum, int charPos) {
        this.tokenType = tokenType;
        this.lineNum = lineNum;
        this.charPos = charPos;

    }

    /**
     * Tokens hold a character and has the following values:
     *
     * @param tokenType word,num,or separator token
     * @param lineNum   line token is on
     * @param charPos   char position of token
     * @param value     value of token
     */
    public Token(TokenType tokenType, int lineNum, int charPos, String value) {
        this.tokenType = tokenType;
        this.lineNum = lineNum;
        this.charPos = charPos;
        this.value = value;
    }

    /**
     * @return toString method for token with or without value
     */
    public String toString() {
        if (this.value != null) {
            return "Type: " + this.tokenType + " Value: " + this.value;
        } else {
            return "Type: " + this.tokenType;
        }
    }




}