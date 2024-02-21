import java.util.LinkedList;
import java.util.Optional;

public class FunctionCallNode extends StatementNode {
    String functionName;
    private LinkedList<Node> parameters;

    /**
     * @param functionName
     * @param parameters
     */
    public FunctionCallNode(String functionName, LinkedList<Node> parameters) {
        this.functionName = functionName;
        this.parameters = parameters;
    }
    public int numofParms(){
   return parameters.size();
    }
    public FunctionCallNode(String functionName) {
        this.functionName = functionName;
        this.parameters = null;
    }
    public LinkedList<Node> getParameters(){
        return  this.parameters;
    }


    @Override
    public String toString() {
        if(parameters!=null) {
            return "FunctionCallNode: {" + "Function Name: " + functionName + " Parameters: " + parameters.toString() ;
        }
        return "FunctionCallNode: {" + "Function Name: " + functionName;

    }

}
