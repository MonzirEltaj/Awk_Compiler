import java.util.LinkedList;
import java.util.Optional;

public class BlockNode extends Node {
    private LinkedList<StatementNode> statements;
    private Optional<Node> condition = Optional.empty();
    /**
     * Creates a block node
     *
     */
    public BlockNode() {
        this.statements = new LinkedList<StatementNode>();
        this.condition=Optional.empty();

    }
    public BlockNode(Optional<Node> condition) {
        this.condition=condition;
        this.statements = new LinkedList<StatementNode>();

    }

    public Optional<Node> getCondition(){
        return this.condition;
    }

    public void setCondition(Optional<Node> condition){
         this.condition = condition;
    }


    @Override
    public String toString() {
            return  this.statements.toString() ;

    }
    public LinkedList<StatementNode> getStatements(){
        return this.statements;
    }

    public void addToLinkedList(StatementNode statement) {
        this.statements.add(statement);
    }



}