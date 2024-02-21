import java.util.Optional;

public class VariableReferenceNode extends Node {

    String name;
    Optional<Node> Expression;

    /**
     * Creates Variable Reference Node
     * @param name that were saving
     */
    public VariableReferenceNode(String name) {
        this.name = name;
        this.Expression = Optional.empty();
    }

    /**
     *
     * @param name being saved
     * @param Expression being saved
     */
    public VariableReferenceNode(String name, Optional<Node> Expression) {
        this.name = name;
        this.Expression = Expression;
    }

    @Override
    public String toString() {
        //Doesn't Print Expression if there isn't one
        if (Expression.isEmpty()) {
            return name;
        } else {
            return "Name: "+ name + " Expression: " + Expression;
        }
    }
    public Optional<Node> getExpression(){
        return this.Expression;
    }
}