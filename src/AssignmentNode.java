public class AssignmentNode extends StatementNode {
    Node target;
    Node expression;
    private OperationNode.type type;

    /**
     * Creates assingment Node
     * @param target
     * @param expression
     */
    public AssignmentNode(Node target, Node expression, OperationNode.type type) {
        this.target = target;
        this.expression = expression;
        this.type=type;
    }

    public AssignmentNode(Node target, Node expression) {
        this.target = target;
        this.expression = expression;
    }


    @Override
    public String toString() {

        return this.target.toString()+ " " + this.expression.toString();

    }
}
