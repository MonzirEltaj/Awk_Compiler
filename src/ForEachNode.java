import java.util.Optional;

public class ForEachNode extends StatementNode {
    Optional<Node> arrayReference;
    BlockNode body;

    /**
     * makes for each node
     * @param arrayReference
     * @param body
     */
    public ForEachNode( Optional<Node> arrayReference, BlockNode body) {
        this.arrayReference = arrayReference;
        this.body = body;
    }


    @Override
    public String toString() {
        return "ForEachNode: {\n" + "Condition:" + arrayReference.toString()  + "\nBody: " + body.toString() +
                "\n}";
    }
}
