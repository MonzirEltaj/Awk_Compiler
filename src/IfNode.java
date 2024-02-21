import java.util.Optional;

public class IfNode extends StatementNode{
    Optional<Node> condition;
    BlockNode block;
    IfNode nextIfNode;

    public IfNode(Optional<Node> condition, BlockNode block) {
        this.condition = condition;
        this.block = block;
        this.nextIfNode = null;
    }
    public void setIfNode(IfNode nextIfNode) {
        this.nextIfNode = nextIfNode;
    }


    @Override
    public String toString() {
        if(this.nextIfNode==null) {
            return "If: " + this.condition + " \n" +
                    this.block.toString();
        }

        return "If: " + this.condition + " \n" +
                this.block.toString() + "\n else: " + this.nextIfNode.toString();

    }
}
