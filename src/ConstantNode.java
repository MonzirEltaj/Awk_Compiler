
public class ConstantNode extends StatementNode {
    String token;

    public ConstantNode(String token){
        this.token=token;
    }

    @Override
    public String toString() {
        return this.token;
    }

}
