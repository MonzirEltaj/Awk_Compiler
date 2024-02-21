
public class ForNode extends StatementNode {
    Node initialization;
    Node condition;
    Node increment;
    BlockNode body;

    /**
     * Makes a for node
     * @param initialization
     * @param condition
     * @param increment
     * @param body
     */
    public ForNode(Node initialization, Node condition, Node increment, BlockNode body) {
        this.initialization = initialization;
        this.condition = condition;
        this.increment = increment;
        this.body = body;
    }


    @Override
    public String toString() {
        return "ForNode: {\n" + "Initialization: " + initialization.toString() + " \n" +  "Condition: " + condition.toString() + " \n" + "Increment: " + increment.toString() + " \n" +
                "Body: " + body.toString() + " \n" + "}";
    }

}
