import java.util.LinkedList;
import java.util.Optional;

public class TokenManager {
private LinkedList<Token> tokens;

    /**
     * Makes token Manger
     * @param tokens
     */
    public TokenManager(LinkedList<Token> tokens){
    this.tokens=tokens;

}

    /**
     * Peeks token at index
     * @param j index we are peeking
     * @return token if it exists, empty if it doesnt
     */
    public Optional<Token> Peek(int j){
    if(j<tokens.size()){
        return Optional.of((tokens.get(j)));
    }
    else{
        return Optional.empty();
    }
}

    /**
     * @return true if there are more tokens, false if there is not
     */
    public boolean MoreTokens(){
    if(tokens.isEmpty()){
        return false;
    }
    else{
        return true;
    }
}

    /**
     * If token type is the same it removes it.
     * @param type
     * @return optional of token if it was matched and removed, if not return empty
     */
    public Optional<Token> MatchAndRemove(Token.TokenType type) {
        if (MoreTokens()) {
            Token token = (Token) tokens.getFirst();
            if (token.tokenType == type) {
                tokens.removeFirst();
                return Optional.of(token);
            }
        }
        return Optional.empty();
    }

}
