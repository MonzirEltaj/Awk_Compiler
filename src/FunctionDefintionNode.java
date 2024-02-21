import java.util.LinkedList;

public class FunctionDefintionNode extends Node {
    private String functionName;
    private LinkedList<Token> parameters = new LinkedList<>();
    private LinkedList<StatementNode>statements;



    /**
     * Constructor for function definition node(This one does not have linked list of statements
     * since its not a part of parser 1
     * @param functionName
     * @param parameters
     */
    public FunctionDefintionNode(String functionName, LinkedList<Token>parameters) {
        this.functionName = functionName;
        this.parameters = parameters;
        statements = new LinkedList<StatementNode>();
    }

    /**
     * Official Constructor for function definition node, will most likely be used in future parser.
     * @param functionName
     * @param parameters
     * @param statements
     */
    public FunctionDefintionNode(String functionName, LinkedList<Token>parameters,LinkedList<StatementNode>statements) {
        this.functionName = functionName;
        this.parameters = parameters;
        this.statements = statements;
    }

    public FunctionDefintionNode(String functionName) {
        this.functionName = functionName;

    }


    /**
     * May be used in future parser.
     * Adds statement to linked list of statements
     * @param statement
     */
    public void addStatement(StatementNode statement) {
        statements.add(statement);
    }

    /**
     * May be used in future parser.
     * @return function name
     */
    public String getFunctionName() {
        return functionName;
    }

    /**
     * May be used in future parser.
     *
     * @return parameters
     */
    public LinkedList<Token> getParameters() {
     return this.parameters;
    }


    /**
     * @return numParms
     */
    public int getNumParm(){
        return parameters.size();
    }

    /**
     *  may be used in future parser.
     * @return Statements
     */
    public LinkedList getStatements() {
        return statements;
    }

    @Override
    public String toString() {
        return "FunctionDefinitionNode: \n Name:"+functionName+"\n parameters:"+ parameters.toString()+"\n statements:"+ statements.toString()+"\n";
    }
}