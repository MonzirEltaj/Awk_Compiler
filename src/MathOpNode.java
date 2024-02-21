public class MathOpNode extends Node {
    public enum Operator {
        PLUS, MINUS, STAR, SLASH
    }

    private Node left;
    private Node right;
    private Operator operator;

    public MathOpNode(Node left, Operator operator, Node right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public Operator getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        return "(" + left.toString() + " " + operatorToString() + " " + right.toString() + ")";
    }

    public String operatorToString() {
        return switch (operator) {
            case PLUS -> "+";
            case MINUS -> "-";
            case STAR -> "*";
            case SLASH -> "/";
            default -> "";
        };
    }
}
