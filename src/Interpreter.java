import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Interpreter {
    LineManager lineManager;
    HashMap<String, InterpreterDataType> globalVariables;
    HashMap<String, FunctionDefintionNode> functioncall;

    /**
     * @param program
     * @param filePath
     * @throws IOException
     */
    public Interpreter(ProgramNode program, String filePath) throws IOException {
        globalVariables = new HashMap<String, InterpreterDataType>();
        functioncall = new HashMap<String, FunctionDefintionNode>();
        globalVariables.put("FILENAME", new InterpreterDataType(filePath));
        globalVariables.put("FS", new InterpreterDataType(" "));
        globalVariables.put("OFMT", new InterpreterDataType("%.6g"));
        globalVariables.put("OFS", new InterpreterDataType(" "));
        globalVariables.put("ORS", new InterpreterDataType("\n"));

        if (filePath != null) {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            lineManager = new LineManager(lines);
        } else {
            lineManager = new LineManager(new ArrayList<>()); // Empty list
        }
        // Define and add the "print" built-in function
        BuiltInFunctionDefinitionNode printFunction = new BuiltInFunctionDefinitionNode(
                "print", true, parameters -> {
            InterpreterDataType arrayDataType = parameters.get("0");
            for (InterpreterDataType param : parameters.values()) {
                System.out.print(param.toString() + " ");
            }
            return new InterpreterArrayDataType("");
        }
        );
        functioncall.put("print", printFunction);

        // Define and add the "printf" built-in function
        BuiltInFunctionDefinitionNode printfFunction = new BuiltInFunctionDefinitionNode(
                "printf", true, parameters -> {
            if (parameters.containsKey("0")) {
                String format = parameters.get("0").toString();
                Object[] values = new Object[parameters.size() - 1];
                for (int i = 1; parameters.containsKey(Integer.toString(i)); i++) {
                    values[i - 1] = parameters.get(Integer.toString(i)).toString();
                }

                System.out.printf(format, values);
            }
            return new InterpreterDataType("");
        }
        );
        functioncall.put("printf", printfFunction);


        // Define and add the "getline" built-in function
        BuiltInFunctionDefinitionNode getlineFunction = new BuiltInFunctionDefinitionNode(
                "getline", false, parameters -> {
            boolean success = lineManager.SplitAndAssign();
            if (success) {
                return new InterpreterDataType("0");
            } else {
                return new InterpreterDataType("1");
            }
        }
        );
        functioncall.put("getline", getlineFunction);

        // Define and add the "next" built-in function
        BuiltInFunctionDefinitionNode nextFunction = new BuiltInFunctionDefinitionNode(
                "next", false, parameters -> {
            lineManager.SplitAndAssign();
            return new InterpreterDataType("");
        }
        );
        functioncall.put("next", nextFunction);
// Define and add the "exit" built-in function
        BuiltInFunctionDefinitionNode exitFunction = new BuiltInFunctionDefinitionNode(
                "exit", false, parameters -> {
            if (parameters.containsKey("0")) {
                String exitCode = parameters.get("0").toString();
                int code;
                try {
                    code = Integer.parseInt(exitCode);
                } catch (NumberFormatException e) {
                    // Handle parsing error
                    throw new RuntimeException("exit code must be integers");
                }
                System.out.println("Exiting with code "+ code);
                System.exit(code);
            } else {
                System.out.println("Exiting");

                System.exit(0); // Default exit code if not specified
            }
            // This return statement is just for the lambda function syntax
            return new InterpreterDataType("");
        });
        functioncall.put("exit", exitFunction);

        // Define and add the "gsub" built-in function
        BuiltInFunctionDefinitionNode gsubFunction = new BuiltInFunctionDefinitionNode(
                "gsub", false, parameters -> {
            if (parameters.containsKey("0") && parameters.containsKey("1") && parameters.containsKey("2")) {
                String input = parameters.get("0").toString();
                String pattern = parameters.get("1").toString();
                String replacement = parameters.get("2").toString();
                String result = input.replaceAll(pattern, replacement);
                globalVariables.put("$0", new InterpreterDataType(result));
                return new InterpreterDataType("1");
            }
            return new InterpreterDataType("0"); // Return 0 for failure
        }
        );
        functioncall.put("gsub", gsubFunction);

        // Define and add the "match" built-in function
        BuiltInFunctionDefinitionNode matchFunction = new BuiltInFunctionDefinitionNode(
                "match", false, parameters -> {
            if (parameters.containsKey("0") && parameters.containsKey("1")) {
                String input = parameters.get("0").toString();
                String pattern = parameters.get("1").toString();
                Pattern regex = Pattern.compile(pattern);
                Matcher matcher = regex.matcher(input);
                if (matcher.find()) {
                    int startIndex = matcher.start();
                    globalVariables.put("$0", new InterpreterDataType(matcher.group()));
                    return new InterpreterDataType(Integer.toString(startIndex + 1));
                }
            }
            return new InterpreterDataType("0"); // Return 0 for failure
        }
        );
        functioncall.put("match", matchFunction);
        // Define and add the "sub" built-in function
        BuiltInFunctionDefinitionNode subFunction = new BuiltInFunctionDefinitionNode(
                "sub", false, parameters -> {
            if (parameters.containsKey("0") && parameters.containsKey("1") && parameters.containsKey("2")) {
                String input = parameters.get("0").toString();
                String pattern = parameters.get("1").toString();
                String replacement = parameters.get("2").toString();
                Pattern regex = Pattern.compile(pattern);
                Matcher matcher = regex.matcher(input);
                if (((Matcher) matcher).find()) {
                    String result = matcher.replaceFirst(replacement);
                    globalVariables.put("$0", new InterpreterDataType(result));
                    return new InterpreterDataType("1");
                }
            }
            return new InterpreterDataType("0"); // Return 0 for failure
        }
        );
        functioncall.put("sub", subFunction);

        // Define and add the "index" built-in function
        BuiltInFunctionDefinitionNode indexFunction = new BuiltInFunctionDefinitionNode(
                "index", false, parameters -> {
            if (parameters.containsKey("input") && parameters.containsKey("substring")) {
                String input = parameters.get("input").toString();
                String substring = parameters.get("substring").toString();
                int index = input.indexOf(substring);
                globalVariables.put("result", new InterpreterDataType(Integer.toString(index + 1)));
                return new InterpreterDataType(Integer.toString(index + 1));
            }
            return new InterpreterDataType("0"); // Return 0 for failure
        }
        );
        functioncall.put("index", indexFunction);

// Define and add the "length" built-in function
        BuiltInFunctionDefinitionNode lengthFunction = new BuiltInFunctionDefinitionNode(
                "length", false, parameters -> {
            if (parameters.containsKey("input")) {
                String input = parameters.get("input").toString();
                int length = input.length();
                globalVariables.put("result", new InterpreterDataType(Integer.toString(length)));
                return new InterpreterDataType(Integer.toString(length));
            }
            return new InterpreterDataType("0"); // Return 0 for failure
        }
        );
        functioncall.put("length", lengthFunction);

// Define and add the "split" built-in function
        BuiltInFunctionDefinitionNode splitFunction = new BuiltInFunctionDefinitionNode(
                "split", false, parameters -> {
            if (parameters.containsKey("input") && parameters.containsKey("array") && parameters.containsKey("delimiter")) {
                String input = parameters.get("input").toString();
                String[] parts = input.split(parameters.get("delimiter").toString());
                int numParts = parts.length;
                for (int i = 0; i < numParts; i++) {
                    globalVariables.put("$" + i, new InterpreterDataType(parts[i]));
                }
                globalVariables.put("result", new InterpreterDataType(Integer.toString(numParts)));
                return new InterpreterDataType(Integer.toString(numParts));
            }
            return new InterpreterDataType("0"); // Return 0 for failure
        }
        );
        functioncall.put("split", splitFunction);

// Define and add the "substr" built-in function
        BuiltInFunctionDefinitionNode substrFunction = new BuiltInFunctionDefinitionNode(
                "substr", false, parameters -> {
            if (parameters.containsKey("input") && parameters.containsKey("start") && parameters.containsKey("length")) {
                String input = parameters.get("input").toString();
                int start = Integer.parseInt(parameters.get("start").toString()) - 1; // AWK indices start at 1 bc eww
                int length = Integer.parseInt(parameters.get("length").toString());
                if (start >= 0 && start < input.length() && length > 0) {
                    String result = input.substring(start, start + length);
                    globalVariables.put("result", new InterpreterDataType(result));
                    return new InterpreterDataType(result);
                }
            }
            return new InterpreterDataType(""); // Return an empty string for failure
        }
        );
        functioncall.put("substr", substrFunction);

        BuiltInFunctionDefinitionNode tolowerFunction = new BuiltInFunctionDefinitionNode(
                "tolower", false, parameters -> {
            if (parameters.containsKey("input")) {
                String input = parameters.get("input").toString();
                String result = input.toLowerCase();
                globalVariables.put("result", new InterpreterDataType(result));
                return new InterpreterDataType(result);
            }
            return new InterpreterDataType(""); // Return an empty string for failure
        }
        );        // Define and add the "tolower" built-in function

        functioncall.put("tolower", tolowerFunction);

        // Define and add the "toupper" built-in function
        BuiltInFunctionDefinitionNode toupperFunction = new BuiltInFunctionDefinitionNode(
                "toupper", false, parameters -> {
            if (parameters.containsKey("input")) {
                String input = parameters.get("input").toString();
                String result = input.toUpperCase();
                globalVariables.put("result", new InterpreterDataType(result));
                return new InterpreterDataType(result);
            }
            return new InterpreterDataType(""); // Return an empty string for failure
        }
        );
        functioncall.put("toupper", toupperFunction);

    }


    /**
     * Method hadels function call
     * @return empty string
     */
    public String RunFunctionCall(HashMap<String, InterpreterDataType> locals, FunctionCallNode functionCallNode) throws IOException {
        // Find the function definition
        FunctionDefintionNode func = functioncall.get(functionCallNode.functionName);

        if (func == null) {
            throw new RuntimeException("Function not found: " + functionCallNode.functionName);
        }

        // Handle built-in functions
        if (func instanceof BuiltInFunctionDefinitionNode) {
            BuiltInFunctionDefinitionNode builtInFunction = (BuiltInFunctionDefinitionNode) func;

                HashMap<String, InterpreterDataType> map = new HashMap<>();
                // Map parameters to their values
                int i = 0;
                if(functionCallNode.getParameters()==null){
                    builtInFunction.execute(map);
                }
                for (Node parameterNode : functionCallNode.getParameters()) {
                    String paramName = String.valueOf(i++);
                    map.put(paramName, GetIDT(parameterNode, locals));
                }

                // Call the built-in function
              return String.valueOf(builtInFunction.execute(map));



        }

        // Check parameters
        if (func.getParameters().size() != functionCallNode.getParameters().size()) {
            throw new RuntimeException("Wrong number of parameters for function: " + functionCallNode.functionName);
        }

        // Create a mapping for parameters
        HashMap<String, InterpreterDataType> map = new HashMap<>();

        int i = 0; // Counter for iterating

        // Map parameters to their values
        for (Token parameter : func.getParameters()) {
            String paramName = parameter.value;

            if (paramName.equals("...")) {
                StringBuilder vString = new StringBuilder();
                while (i < functionCallNode.numofParms()) {
                    InterpreterDataType vValue = GetIDT(functionCallNode.getParameters().get(i++), locals);
                    vString.append(vValue.toString()).append(" ");
                }
                map.put(paramName, new InterpreterArrayDataType(vString.toString().trim()));
            } else {
                InterpreterDataType parameterValue = GetIDT(functionCallNode.getParameters().get(i++), locals);
                if (parameterValue instanceof InterpreterArrayDataType) {
                    map.put(paramName, parameterValue);
                } else {
                    throw new ClassCastException("Expected InterpreterArrayDataType, but got: " + parameterValue.getClass().getSimpleName());
                }
            }
        }

        // Call the function
        InterpreterDataType result;
        if (func instanceof BuiltInFunctionDefinitionNode) {
            result = ((BuiltInFunctionDefinitionNode) func).execute(map);
        } else {
            InterpretListOfStatements(func.getStatements(), map);
        }
return null;
    }







    // Helper method to get variadic arguments as an array
    public Object[] getVariadicArguments(HashMap<String, InterpreterDataType> parameters) {
        List<InterpreterDataType> args = parameters.values().stream().toList();
        return args.toArray();
    }


    /**
     * Creates an IDT Depending on what is given, Comments below explain the weird cases
     *
     * @param node
     * @param hashMap
     * @return IDT
     */
    public InterpreterDataType GetIDT(Node node, HashMap<String, InterpreterDataType> hashMap) throws IOException {
        //Handles possible Assignment Node targets.
        if (node instanceof AssignmentNode) {
            Node target = ((AssignmentNode) node).target;

            if (target instanceof VariableReferenceNode) {
                VariableReferenceNode variableReferenceNode = (VariableReferenceNode) target;
                InterpreterDataType idt = GetIDT(((AssignmentNode) node).expression, hashMap);
                hashMap.put(variableReferenceNode.name, idt);
                return idt;
            }

            if (target instanceof OperationNode) {
                OperationNode operationNode = (OperationNode) target;
                if (operationNode.getType() == OperationNode.type.DOLLAR) {
                    InterpreterDataType idt = GetIDT(((AssignmentNode) node).expression, hashMap);
                    hashMap.put(String.valueOf(target), idt);
                    return idt;
                }
            }
        }

        // Handle other cases or throw an exception if needed

    //If its a ConstantNode add to hashmap and move on
        else if (node instanceof ConstantNode) {
            InterpreterDataType idt = new InterpreterDataType(String.valueOf(node));
            hashMap.put(String.valueOf(((ConstantNode) node)), idt);
            return idt;
            //If its a FunctionCallNode call RunFunctionCall
        } else if (node instanceof FunctionCallNode) {
            FunctionCallNode functionCallNode = (FunctionCallNode) node;
              RunFunctionCall(hashMap, functionCallNode);

            //Pattern Nodes are not allowed
        } else if (node instanceof PatternNode) {
            throw new RuntimeException("NO, you cant pass a pattern to a function or an assignment");
            //For ternary call get IDT on condition if its null its false, if its not null its true
        } else if (node instanceof TernaryNode) {
            TernaryNode ternaryNode = (TernaryNode) node;
            InterpreterDataType conditionResult = GetIDT(ternaryNode.condition, hashMap);
            if (conditionResult != null) {
                return GetIDT(ternaryNode.trueCase, hashMap);
            } else {
                return GetIDT(ternaryNode.falseCase, hashMap);
            }
            //Search local hash map first then global.
        } else if (node instanceof VariableReferenceNode) {
            String name = ((VariableReferenceNode) node).name;

            if (((VariableReferenceNode) node).getExpression().isEmpty()) {
                if (hashMap.containsKey(name)) {
                    return hashMap.get(name);
                }
                if (globalVariables.containsKey(name)) {
                    return globalVariables.get(name);
                } else if (!hashMap.containsKey(name) && !globalVariables.containsKey(name)) {
                    globalVariables.put(name, new InterpreterDataType(null));
                    return new InterpreterDataType(name);
                }
            } else {
                InterpreterDataType index = GetIDT(((VariableReferenceNode) node).getExpression().get(), hashMap);
                InterpreterArrayDataType bola = (InterpreterArrayDataType) globalVariables.get(name);
                if (bola instanceof InterpreterArrayDataType) {
                    if (index instanceof InterpreterDataType) {
                        return bola.IADT.get(index.toString());
                    } else {
                        throw new RuntimeException("Index should be an InterpreterDataType.");
                    }
                } else {
                    throw new RuntimeException("Variable " + name + " is not an array.");
                }
            }
        }


        //In case Of Operation Node.
        else if (node instanceof OperationNode) {
            boolean rightCheck = false;
            InterpreterDataType left = GetIDT(((OperationNode) node).getleft(), hashMap);
            InterpreterDataType right = null;
            if (((OperationNode) node).getRight() != null && !(((OperationNode) node).getRight() instanceof PatternNode)) {
                right = GetIDT(((OperationNode) node).getRight(), hashMap);
            }
            //This is a weird set up but it works
            //In this try i convert left and right to floats and use a switch statement to go through the cases.
            //If it fails to convert go to catch where you run tests on strings.
            try {
                float leftF = Float.parseFloat(String.valueOf(((OperationNode) node).getleft()));
                float rightF = 0;
                if (((OperationNode) node).getRight() != null) {
                    rightF = Float.parseFloat(String.valueOf(((OperationNode) node).getRight()));
                }
                switch (((OperationNode) node).getType()) {
                    //IN case
                    case IN:
                        if (((OperationNode) node).getRight() instanceof VariableReferenceNode) {
                            String name = ((VariableReferenceNode) node).name;
                            InterpreterDataType idtL = hashMap.get(name);
                            InterpreterDataType idtG = globalVariables.get(name);
                            if (idtL == null) {
                                return idtG;
                            } else {
                                return idtL;

                            }
                        } else {
                            throw new RuntimeException("has to be a var ref node");
                        }
                        //Handels Basic Math
                    case UNARYNEG:
                        return new InterpreterDataType(String.valueOf(-leftF));

                    case UNARYPOS:
                        return new InterpreterDataType(String.valueOf(+leftF));

                    case PREDEC:
                        return new InterpreterDataType(String.valueOf(--leftF));

                    case POSTDEC:
                        leftF--;
                        return new InterpreterDataType(String.valueOf(leftF));

                    case PREINC:
                        return new InterpreterDataType(String.valueOf(++leftF));

                    case POSTINC:
                        leftF++;
                        return new InterpreterDataType(String.valueOf(leftF));


                    case DOLLAR:
                        InterpreterDataType leftD = GetIDT(((OperationNode) node).getleft(), hashMap);
                        String name = "$" + leftD.toString();
                        return new InterpreterDataType(name);

                    case AND, OR, NOT:
                        if (String.valueOf(leftF).equals("0.0")) {
                            return new InterpreterDataType(String.valueOf(0));
                        }
                        return new InterpreterDataType(String.valueOf(1));

                    //More math
                    case LT:
                        return new InterpreterDataType(String.valueOf(leftF < rightF));


                    case LE:
                        return new InterpreterDataType(String.valueOf(leftF <= rightF));

                    case GT:
                        return new InterpreterDataType(String.valueOf(leftF > rightF));

                    case GE:
                        return new InterpreterDataType(String.valueOf(leftF >= rightF));


                    case NE:
                        return new InterpreterDataType(String.valueOf(leftF != rightF));

                    case EQ:
                        return new InterpreterDataType(String.valueOf(leftF == rightF));

                    case ADD:
                        return new InterpreterDataType(String.valueOf(leftF + rightF));

                        case SUBTRACT:
                        return new InterpreterDataType(String.valueOf(leftF - rightF));


                    case MULTIPLY:
                        return new InterpreterDataType(String.valueOf(leftF * rightF));



                    case DIVIDE:
                        //Edge case
                        if (rightF == 0) {
                            throw new RuntimeException("Division by zero");
                        }
                        return new InterpreterDataType(String.valueOf(leftF / rightF));


                    case MODULO:
                        //Edge Case
                        if (rightF == 0) {
                            throw new RuntimeException("Modulus by zero");
                        }
                        return new InterpreterDataType(String.valueOf(leftF % rightF));


                    case EXPONENT:
                        return new InterpreterDataType(String.valueOf((float) Math.pow(leftF, rightF)));


                }
            } catch (NumberFormatException exception) {
                //Repeat but strings bc float conversion failed :(
                switch (((OperationNode) node).getType()) {
                    case AND, OR, NOT:
                        if (!((OperationNode) node).getleft().toString().isEmpty()) {
                            return new InterpreterDataType(String.valueOf(1));
                        }
                        return new InterpreterDataType(String.valueOf(0));

                    //combines strings
                    case CONCATENATION:
                        return new InterpreterDataType(String.valueOf(left) + String.valueOf(right));
                    case MATCH, NOTMATCH:
                        boolean check = false;
                        InterpreterDataType leftIdt = GetIDT(((OperationNode) node).getleft(), hashMap);
                        if (((OperationNode) node).getRight() instanceof PatternNode) {
                            Pattern pattern = Pattern.compile(((OperationNode) node).getRight().toString());
                            Matcher matcher = pattern.matcher(leftIdt.toString());
                            check = matcher.find();
                        }
                        if (((OperationNode) node).getType() == OperationNode.type.MATCH) {
                            if (check) {
                                return new InterpreterDataType(String.valueOf(1));
                            }
                            return new InterpreterDataType(String.valueOf(0));

                        } else if (((OperationNode) node).getType() == OperationNode.type.NOTMATCH) {
                            if (check) {
                                return new InterpreterDataType(String.valueOf(0));
                            }
                            return new InterpreterDataType(String.valueOf(1));
                        }
                    case LT:
                        return new InterpreterDataType(String.valueOf(left.value.compareTo(right.value) < 0));


                    case LE:
                        return new InterpreterDataType(String.valueOf(left.value.compareTo(right.value) <= 0));

                    case GT:
                        return new InterpreterDataType(String.valueOf(left.value.compareTo(String.valueOf(right.value)) > 0));

                    case GE:
                        return new InterpreterDataType(String.valueOf(left.value.compareTo(String.valueOf(right.value)) >= 0));


                    case NE:
                        return new InterpreterDataType(String.valueOf(!left.value.equals(right.value)));

                    case EQ:
                        return new InterpreterDataType(String.valueOf(left.value.equals(right.value)));

                    case ASSIGN:
                        return new InterpreterDataType(String.valueOf((right.value)));


                    case DOLLAR:
                        InterpreterDataType leftD = GetIDT(((OperationNode) node).getleft(), hashMap);
                        String name = "$" + leftD.toString();
                        return new InterpreterDataType(name);
                }
            }

        }
        //you sent something weird
        return null;

    }

    /**
     * goes throught many if statemetns to determine type of node if none fit call getIdt, if that fails throw exception.
     *
     * @param local         hashmap
     * @param statementNode can be many things
     * @return ReturnType node
     * @throws IOException if bad sytnax
     */
    public ReturnType ProcessStatement(HashMap<String, InterpreterDataType> local, StatementNode statementNode) throws IOException {
        //Break
        if (statementNode instanceof BreakNode) {
            return new ReturnType(ReturnType.Type.BREAK);
        }
        //Continue
        else if (statementNode instanceof ContinueNode) {
            return new ReturnType(ReturnType.Type.CONTINUE);
        } else if (statementNode instanceof DeleteNode) {
            DeleteNode deleteNode = (DeleteNode) statementNode;
            String arrayName = deleteNode.getArrayName();
            //search local hash map for array
            if (local.containsKey(arrayName)) {
                InterpreterArrayDataType array = (InterpreterArrayDataType) local.get(arrayName);
                if(deleteNode.getIndices().isEmpty()){
                    local.remove(arrayName);

                }
                Deletes(array, deleteNode.getIndices());
                //search global hash map for array
            } else if (globalVariables.containsKey(arrayName)) {
                InterpreterArrayDataType array = (InterpreterArrayDataType) globalVariables.get(arrayName);

                if(deleteNode.getIndices().isEmpty()) {
                    local.remove(arrayName);
                    }
                Deletes(array, deleteNode.getIndices());
            }
            //No need for runtime exception, awk is weird and allows you to delete arrays that arent real.
            return new ReturnType(ReturnType.Type.DELETE);
        }
        //handles do while
        else if (statementNode instanceof DoWhileNode) {
            DoWhileNode doWhileNode = (DoWhileNode) statementNode;
            InterpreterDataType conditionResult = null;

            do {
                ReturnType result = InterpretListOfStatements(doWhileNode.body.getStatements(), local);
                //incase of break/continue
                if (result.getType() == ReturnType.Type.BREAK) {
                    break;
                }
                if (result.getType() == ReturnType.Type.CONTINUE) {
                    continue;
                }

                conditionResult = GetIDT(doWhileNode.condition, local);

            }
            // If the condition is false, break;
            //conditionResult and value cant be null
            while (conditionResult != null && conditionResult.value != null);

            return new ReturnType(ReturnType.Type.NORMAL);
        }
        //handles for loops
        else if (statementNode instanceof ForNode) {
            ForNode forNode = (ForNode) statementNode;
            // Process the initialization
            ProcessStatement(local, (StatementNode) forNode.initialization);

            while (true) {
                InterpreterDataType conditionResult = GetIDT(forNode.condition, local);

                // If the condition is false, break;
                if (conditionResult != null && conditionResult.value != null) {
                    break;
                }

                ReturnType result = InterpretListOfStatements(forNode.body.getStatements(), local);
                //Check for break and continue in loop
                if (result.getType() == ReturnType.Type.BREAK) {
                    break;
                } else if (result.getType() == ReturnType.Type.CONTINUE) {
                    continue;
                }
                // Process the increment
                ProcessStatement(local, (StatementNode) forNode.increment);

            }
            // Return from ProcessStatement
            return new ReturnType(ReturnType.Type.NORMAL);
            //Handles for each loops
        } else if (statementNode instanceof ForEachNode) {
            ForEachNode forEachNode = (ForEachNode) statementNode;
            //arrayReference has to be present
            if (forEachNode.arrayReference.isPresent()) {
                Node arrayReference = forEachNode.arrayReference.get();
                //Has to be a var ref node(since its an array)
                if (arrayReference instanceof VariableReferenceNode) {
                    //save the name
                    String arrayName = ((VariableReferenceNode) arrayReference).name;
                    InterpreterArrayDataType arrayData = (InterpreterArrayDataType) GetIDT(arrayReference, local);
                    //enhanced for loop to go through the key set
                    for (Object key : arrayData.getArrayData().keySet()) {
                        local.put(arrayName, (InterpreterDataType) arrayData.getArrayData().get(key));
                        ReturnType result = InterpretListOfStatements(forEachNode.body.getStatements(), local);
                        //in case of break and continue
                        if (result.getType() == ReturnType.Type.BREAK) {
                            break;
                        } else if (result.getType() == ReturnType.Type.CONTINUE) {
                            continue;
                        }
                    }

                    return new ReturnType(ReturnType.Type.NORMAL);
                }
            } else {
                throw new RuntimeException("Array reference is not present.");
            }
        } else if (statementNode instanceof IfNode) {
            IfNode ifNode = (IfNode) statementNode;
            while (ifNode != null) {
                InterpreterDataType conditionResult = GetIDT(ifNode.condition.get(), local);
                if (ifNode.condition.isEmpty() || (conditionResult != null && conditionResult.value != null)) {
                    ReturnType result = InterpretListOfStatements(ifNode.block.getStatements(), local);
                    if (result.getType() != ReturnType.Type.NONE) {
                        return result;
                    }
                }

                // Move to the next IfNode in the linked list
                ifNode = ifNode.nextIfNode;
            }
            return new ReturnType(ReturnType.Type.NORMAL);
        } else if (statementNode instanceof ReturnNode) {
            ReturnNode returnNode = (ReturnNode) statementNode;
            //Evaluate with parseOperation
            Optional<Node> evaluate = returnNode.parseOperation;
            if (evaluate.isPresent()) {
                InterpreterDataType value = GetIDT(evaluate.get(), local);
                return new ReturnType(value.toString(), ReturnType.Type.RETURN);
            } else {
                return new ReturnType(ReturnType.Type.RETURN);
            }
            //Handles the while nodes
        } else if (statementNode instanceof WhileNode) {
            WhileNode whileNode = (WhileNode) statementNode;
            InterpreterDataType conditionResult;
            while (true) {
                conditionResult = GetIDT(whileNode.condition, local);
                if (conditionResult != null && conditionResult.value != null) {
                    ReturnType result = InterpretListOfStatements(whileNode.body.getStatements(), local);
                    if (result.getType() == ReturnType.Type.BREAK) {
                        break;
                    } else if (result.getType() == ReturnType.Type.CONTINUE) {
                        continue;
                    }
                } else {
                    break;
                }
            }

            return new ReturnType(ReturnType.Type.NORMAL);
            //Handles AssignmentNode and FunctionCallNode as per email change.
        } else {
            InterpreterDataType result = GetIDT(statementNode, local);
            return new ReturnType(ReturnType.Type.NORMAL);
        }
        return null;

    }

    /**
     * @param statements linked list
     * @param locals     hashmap
     * @return same type if not NONE
     * @throws IOException
     */
    public ReturnType InterpretListOfStatements(LinkedList<StatementNode> statements, HashMap<String, InterpreterDataType> locals) throws IOException {
        ReturnType result = new ReturnType(ReturnType.Type.NONE);

        for (StatementNode statement : statements) {
            result = ProcessStatement(locals, statement);
            if (result.getType() != ReturnType.Type.NONE) {
                return result;
            }
        }
        //statement was type NONE
        return result;
    }

    /**
     * Does the Deleting
     * @param array
     * @param indices of array
     */
    public void Deletes(InterpreterArrayDataType array, List<String> indices) {
        if (indices.isEmpty()) {
            // No indes so whole array is deleted
            array.getArrayData().clear();
        } else {
            // Delete specific index
            for (String index : indices) {
                array.getArrayData().remove(index);
            }
        }
    }
    public void InterpretProgram(ProgramNode programNode) throws IOException {
        for (BlockNode begin : programNode.getBeginBlockNodes()) {
            InterpretBlock(begin);
        }
        while(lineManager.SplitAndAssign()) {
            for (BlockNode other : programNode.getOtherBlockNodes()) {
                InterpretBlock(other);

            }
        }
        for (BlockNode end : programNode.getEndBlockNodes()) {
            InterpretBlock(end);

        }
    }

    public void  InterpretBlock(BlockNode blockNode ) throws IOException {
        HashMap local = new HashMap<String, InterpreterDataType>();
        if(blockNode.getCondition().isPresent()){
          InterpreterDataType condtion = GetIDT(blockNode.getCondition().get(),local);
          if(condtion.toString() == "1"){
              for(StatementNode statement : blockNode.getStatements()){
             ProcessStatement(local,statement);
              }
          }
        }
        else{
                for(StatementNode statement : blockNode.getStatements()){
                    ProcessStatement(local,statement);
            }
        }

    }


    //Line Manager Class
    public class LineManager {
        private List<String> input;
        private int lineNum;
        private int NR;
        private int FNR;

        public LineManager(List<String> input) {
            this.input = input;
            this.lineNum = 0;
            this.NR = 0;
            this.FNR = 0;
        }

        //Return true if split and assign was possible
        public boolean SplitAndAssign() {
            if (lineNum >= input.size()) {
                // No more lines to split
                return false;
            }
            String currentLine = input.get(lineNum);
            lineNum++;
            String[] fields = currentLine.split(globalVariables.get("FS").toString());
            for (int i = 0; i < fields.length; i++) {
                globalVariables.put("$" + i, new InterpreterDataType(fields[i]));
            }
            globalVariables.put("NF", new InterpreterDataType(Integer.toString(fields.length)));
            globalVariables.put("NR", new InterpreterDataType(Integer.toString(lineNum)));
            globalVariables.put("FNR", new InterpreterDataType(Integer.toString(lineNum)));

            return true;
        }
    }
}
