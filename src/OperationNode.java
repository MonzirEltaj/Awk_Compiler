import java.util.Optional;

public class OperationNode extends Node {
    private Node  left;       //Left Node
    private   Optional<Node> right;    //Right Node

    private type type;

    /**
     * Creates a Operation Node
     * @param left
     * @param right
     * @param type
     */
    public OperationNode(Node left, type type,Optional<Node>right){
        this.left=left;
        this.right=right;
        this.type=type;
    }

    /**
     * Creates a Operation nod without right.
     * @param left
     * @param type
     */
    public OperationNode(Node left,  type type){
        this.left=left;
        this.type=type;
        this.right = Optional.empty();
    }
    public OperationNode(Node left){
        this.left=left;
        this.right = Optional.empty();
    }

    public OperationNode.type getType() {
        return type;
    }
    public Node getleft(){
        return this.left;
    }
    public Node getRight(){
        return this.right.orElse(null);
    }


    @Override
    public String toString() {
        //If right is null print without it.
        if(this.right.isEmpty()){
            return "Left:" + this.left + " Operation Type: " +this.type;

        }
        return "Left:" + this.left + " Operation Type: " +this.type +" Right: "+this.right;
    }

    enum type{
        EQ, NE, LT, LE, GT, GE, AND, OR, NOT, MATCH, NOTMATCH, DOLLAR,
        PREINC,POSTINC,PREDEC, POSTDEC,UNARYPOS, UNARYNEG, IN,
        EXPONENT, ADD, SUBTRACT,MULTIPLY, DIVIDE,MODULO, CONCATENATION,MATHEQ,ASSIGN

    }


}
