import java.util.HashMap;
import java.util.function.Function;

public class BuiltInFunctionDefinitionNode extends FunctionDefintionNode {
    private boolean isVariadic;
    private Function<HashMap<String, InterpreterDataType>, InterpreterDataType> execute;

    public BuiltInFunctionDefinitionNode(String functionName, boolean isVariadic, Function<HashMap<String, InterpreterDataType>, InterpreterDataType> execute) {
        super(functionName);
        this.isVariadic = isVariadic;
        this.execute = execute;
    }

    public boolean isVariadic() {
        return isVariadic;
    }

    public InterpreterDataType execute(HashMap<String, InterpreterDataType> parameters) {
        return execute.apply(parameters);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
