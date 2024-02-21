public class TernaryNode extends StatementNode {
    Node condition;
    Node trueCase;
    Node falseCase;

    /**
     * Creates ternary Node
     * @param condition
     * @param trueCase
     * @param falseCase
     */
    public TernaryNode(Node condition, Node trueCase, Node falseCase) {
        this.condition = condition;
        this.trueCase = trueCase;
        this.falseCase = falseCase;
    }

    @Override
    public String toString() {
        return "Condition: "+this.condition +" Truecase:" + trueCase +" Falsecase:" +falseCase;
    }

}
