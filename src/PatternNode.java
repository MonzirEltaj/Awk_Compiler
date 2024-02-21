
public class PatternNode extends StatementNode {
    Token token;

    /**
     * Creates a pattern node
     * @param token
     */
    public PatternNode(Token token){
        this.token=token;
    }

    @Override
    public String toString() {
        //Prints Value.
        return this.token.value.toString();
    }

}
