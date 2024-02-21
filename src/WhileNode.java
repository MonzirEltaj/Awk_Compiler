import java.util.Optional;

public class WhileNode extends StatementNode {
    Node condition;
    BlockNode body;

    public WhileNode(Node condition, BlockNode body) {
        this.condition = condition;
        this.body = body;
    }


    @Override
    public String toString() {
        return "WhileNode: {\n" + "Condition: " + condition.toString() + "\n" + "Body: " + body.toString() + "\n" + "}";
    }
}
