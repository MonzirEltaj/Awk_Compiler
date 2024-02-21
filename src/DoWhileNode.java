public class DoWhileNode extends StatementNode {
    Node condition;
    BlockNode body;

    public DoWhileNode(Node condition, BlockNode body) {
        this.condition = condition;
        this.body = body;
    }


    @Override
    public String toString() {
        return "DoWhileNode: {" + "Condition: " + condition.toString() + "Body: " + body.toString()  + "}";
    }
}
