import java.util.Optional;

public class ReturnNode extends StatementNode{
Optional<Node> parseOperation;

    public ReturnNode(Optional<Node> parseOperation) {
        this.parseOperation=parseOperation;
    }
    @Override
    public String toString() {
        return "ReturnNode Node: "+ this.parseOperation;
    }
}

