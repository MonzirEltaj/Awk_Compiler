import javax.swing.text.html.Option;
import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.locks.Condition;

public class Parser {
    private LinkedList<Token> tokens;
    private TokenManager tokenManager;


    /**
     * Creates a Parser
     *
     * @param tokens
     */
    public Parser(LinkedList<Token> tokens) {
        this.tokens = tokens;
        this.tokenManager = new TokenManager(tokens);
    }

    /**
     * Accepts multiple separators and matches and removes them
     *
     * @return true if a separator was found
     */
    public boolean AcceptSeparators() {
        boolean isSeparator = false;

        while (tokenManager.MoreTokens()) {
            Optional<Token> nextToken = tokenManager.MatchAndRemove(Token.TokenType.SEPARATOR);
            if (nextToken.isPresent()) {
                isSeparator = true;
            } else {
                break;
            }
        }

        return isSeparator;
    }


    /**
     * Calls Parse function and Parse action. if both are false the grammar is wrong,throws exception
     *
     * @return program Node
     */
    public ProgramNode Parse() {
        ProgramNode programNode = new ProgramNode();
        while (tokenManager.MoreTokens()) {
            if (!parseFunction(programNode) && !parseAction(programNode)) {
                throw new RuntimeException("Wrong Grammar!");
            }
            AcceptSeparators();
        }
        return programNode;
    }

    /**
     * @param programNode
     * @return TRUE if function parsed false if not(will throw error if function is present but syntax wrong)
     */
    public boolean parseFunction(ProgramNode programNode) {
        LinkedList<Token> saveMe = new LinkedList<Token>();
        boolean moreParameters = false;
        boolean isFunctionReal = false;
        Token name;
        Token parameter;
        if (!tokenManager.MoreTokens()) {
            return false;
        }
        if (tokenManager.MatchAndRemove(Token.TokenType.FUNCTION).isPresent()) {
            isFunctionReal = true;
            name = tokenManager.MatchAndRemove(Token.TokenType.WORD).
                    orElseThrow(() -> new RuntimeException("MISSING NAME FOR FUNCTION"));
            if (tokenManager.MatchAndRemove(Token.TokenType.OPENPAR).isPresent()) {
                if (tokenManager.MatchAndRemove(Token.TokenType.CLOSEPAR).isPresent()) {

                    AcceptSeparators();
                    programNode.addFunctionDefinitionNodes(new FunctionDefintionNode(name.value, saveMe));
                    return true;
                } else {
                    if (!isFunctionReal) {
                        throw new RuntimeException("NO OPEN PARENTHESIS HAS TO APPEAR AFTER A COMMA");
                    }
                    do {
                        if (moreParameters && !tokenManager.Peek(0).isPresent()) {
                            throw new RuntimeException("PARAMETER HAS TO APPEAR AFTER A COMMA");
                        }
                        //if there is a word save it in parameter if not throw an exception.
                        parameter = tokenManager.MatchAndRemove(Token.TokenType.WORD)
                                .orElseThrow(() -> new RuntimeException("COMMA WITHOUT A WORD"));
                        saveMe.add(parameter);
                        AcceptSeparators();

                        if (tokenManager.MatchAndRemove(Token.TokenType.COMMA).isPresent()) {
                            AcceptSeparators();
                            moreParameters = true;
                        } else {

                            moreParameters = false;
                        }


                    } while (moreParameters);

                    if (tokenManager.MatchAndRemove(Token.TokenType.CLOSEPAR).isPresent()) {

                        AcceptSeparators();
                        programNode.addFunctionDefinitionNodes(new FunctionDefintionNode(name.value, saveMe));
                        return true;
                    }
                    if (tokenManager.MatchAndRemove(Token.TokenType.DOLLAR).isPresent()) {
                        ParseLValue();
                    }
                }
            }
        }


        return false;
    }
    /**
     * takes the name of the function to call and the parameters (LinkedList<Node>).
     * @return function call
     */
    public Optional<FunctionCallNode> parseFunctionCall() {
        String paramter;
        LinkedList<Node> parameters = new LinkedList();

            if(tokenManager.MatchAndRemove(Token.TokenType.GETLINE).isPresent()){
                AcceptSeparators();
                if(tokenManager.MatchAndRemove(Token.TokenType.OPENPAR).isPresent()) {
                    parameters.add(ParseOperation().get());
                    if(tokenManager.MatchAndRemove(Token.TokenType.CLOSEPAR).isPresent());
                    {
                        return Optional.of(new FunctionCallNode("getline", parameters));
                    }

                }
                else{
                    parameters.add(ParseOperation().get());
                    return Optional.of(new FunctionCallNode("getline", parameters));

                }
            }
            if(tokenManager.MatchAndRemove(Token.TokenType.PRINT).isPresent()){
                AcceptSeparators();
                if(tokenManager.MatchAndRemove(Token.TokenType.OPENPAR).isPresent()) {
                    parameters.add(ParseOperation().get());
                    if(tokenManager.MatchAndRemove(Token.TokenType.CLOSEPAR).isPresent());
                    {
                        return Optional.of(new FunctionCallNode("print", parameters));
                    }

                }
                else{
                    parameters.add(ParseOperation().get());
                    return Optional.of(new FunctionCallNode("print", parameters));

                }
            }
            if(tokenManager.MatchAndRemove(Token.TokenType.PRINTF).isPresent()){
                AcceptSeparators();
                if(tokenManager.MatchAndRemove(Token.TokenType.OPENPAR).isPresent()) {
                    if (tokenManager.Peek(0).get().tokenType ==Token.TokenType.STRINGLITERAL) {
                        paramter = String.valueOf(tokenManager.Peek(0).get().value);
                        parameters.add(new ConstantNode(paramter));
                        tokenManager.MatchAndRemove(Token.TokenType.STRINGLITERAL);
                        while(true){
                            if(tokenManager.MatchAndRemove(Token.TokenType.COMMA).isPresent()) {
                                    Optional<Node> par = ParseBottomLevel();
                                    parameters.add(par.get());
                            }
                            else{
                                break;
                            }

                        }
                        if(tokenManager.MatchAndRemove(Token.TokenType.CLOSEPAR).isPresent());
                        {
                            return Optional.of(new FunctionCallNode("printf", parameters));
                        }


                    }
                    //add actual error cases later
                    else{
                        throw new RuntimeException("bad");
                    }
                }
                else {
                    if (tokenManager.Peek(0).get().tokenType ==Token.TokenType.STRINGLITERAL) {
                        paramter = String.valueOf(tokenManager.Peek(0).get().value);
                        parameters.add(new ConstantNode(paramter));
                        return Optional.of(new FunctionCallNode("printf", parameters));

                    }
                }

            }
            if(tokenManager.MatchAndRemove(Token.TokenType.EXIT).isPresent()){
                if(tokenManager.MatchAndRemove(Token.TokenType.OPENPAR).isPresent()) {
                    if(tokenManager.Peek(0).get().tokenType == (Token.TokenType.NUMBER)) {
                        paramter = String.valueOf(tokenManager.Peek(0).get().value);
                        parameters.add(new ConstantNode(paramter));
                        tokenManager.MatchAndRemove(Token.TokenType.NUMBER);
                        AcceptSeparators();
                        if(tokenManager.MatchAndRemove(Token.TokenType.CLOSEPAR).isPresent()) {
                            return Optional.of(new FunctionCallNode("exit", parameters));
                        }
                    }
                    else{
                        throw new RuntimeException("bad");
                    }
                }
                if(tokenManager.MoreTokens()) {
                    if (tokenManager.Peek(0).get().tokenType == (Token.TokenType.NUMBER)) {
                        paramter = String.valueOf(tokenManager.Peek(0).get().value);
                        parameters.add(new ConstantNode(paramter));
                        tokenManager.MatchAndRemove(Token.TokenType.NUMBER);
                        AcceptSeparators();
                        return Optional.of(new FunctionCallNode("exit", parameters));
                    }
                }


                return Optional.of(new FunctionCallNode("exit"));

            }
            if(tokenManager.MatchAndRemove(Token.TokenType.NEXTFILE).isPresent()){
                AcceptSeparators();
                return Optional.of(new FunctionCallNode("nextfile"));

            }
            if(tokenManager.MatchAndRemove(Token.TokenType.NEXT).isPresent()){
                AcceptSeparators();
                return Optional.of(new FunctionCallNode("next"));

            }
        if (tokenManager.MatchAndRemove(Token.TokenType.OPENPAR).isPresent()) {


        }

            if (tokenManager.Peek(0).isPresent() && tokenManager.Peek(0).get().tokenType == Token.TokenType.WORD&&tokenManager.Peek(1).isPresent() && tokenManager.Peek(1).get().tokenType == Token.TokenType.OPENPAR) {
                String functionName = tokenManager.Peek(0).get().value;
                tokenManager.MatchAndRemove(Token.TokenType.WORD); // Consume the function name
                if (tokenManager.MatchAndRemove(Token.TokenType.OPENPAR).isPresent()) {
                    while (true) {
                        AcceptSeparators();
                        Optional<Node> parameter = ParseOperation();
                        if (parameter.isPresent()) {
                            parameters.add(parameter.get());
                            AcceptSeparators();
                            if (tokenManager.MatchAndRemove(Token.TokenType.COMMA).isPresent()) {
                                // Continue parsing parameters
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    }

                    if (tokenManager.MatchAndRemove(Token.TokenType.CLOSEPAR).isPresent()) {
                        return Optional.of(new FunctionCallNode(functionName, parameters));
                    } else {
                        throw new RuntimeException("Expected closing parenthesis.");
                    }
                }
            }


        return Optional.empty();
    }


    /**
     * Parses the lowest level of expressions
     *
     * @return a node depending on what token it is
     */
    public Optional<Node> ParseBottomLevel() {
        if (tokenManager.MoreTokens()) {
            Optional<Token> token = tokenManager.Peek(0);
            if (token.isPresent()) {
                // if block checks what kind of token it is then returns the appropriate node.
                if (token.get().tokenType == Token.TokenType.STRINGLITERAL) {
                    tokenManager.MatchAndRemove(Token.TokenType.STRINGLITERAL);
                    return Optional.of(new ConstantNode(token.get().value));
                } else if (token.get().tokenType == Token.TokenType.NUMBER) {
                    tokenManager.MatchAndRemove(Token.TokenType.NUMBER);
                    return Optional.of(new ConstantNode(token.get().value));
                } else if (token.get().tokenType == Token.TokenType.PATTERN) {
                    tokenManager.MatchAndRemove(Token.TokenType.PATTERN);
                    return Optional.of(new PatternNode(token.get()));
                } else if (token.get().tokenType == Token.TokenType.OPENPAR) {
                    tokenManager.MatchAndRemove(Token.TokenType.OPENPAR);
                    Optional<Node> node = ParseOperation();

                    if (tokenManager.MatchAndRemove(Token.TokenType.CLOSEPAR).isPresent()) {
                        return node;
                    } else {
                        //Incorrect syntax
                        throw new RuntimeException("Missing closing parenthesis.");
                    }
                } else if (token.get().tokenType == Token.TokenType.EXPLANATION) {
                    tokenManager.MatchAndRemove(Token.TokenType.EXPLANATION);
                    Node node = ParseOperation().get();
                    return Optional.of(new OperationNode(node, OperationNode.type.NOT));
                } else if (token.get().tokenType == Token.TokenType.MINUS) {
                    tokenManager.MatchAndRemove(Token.TokenType.MINUS);
                    Node operationNode = ParseOperation().get();
                    return Optional.of(new OperationNode(operationNode, OperationNode.type.UNARYNEG));
                } else if (token.get().tokenType == Token.TokenType.PLUS) {
                    tokenManager.MatchAndRemove(Token.TokenType.PLUS);
                    Node operationNode = ParseOperation().get();
                    return Optional.of(new OperationNode(operationNode, OperationNode.type.UNARYPOS));
                } else if (token.get().tokenType == Token.TokenType.PLUSX2) {
                    tokenManager.MatchAndRemove(Token.TokenType.PLUSX2);
                    Optional<Node> operationNode = ParseOperation();
                    return Optional.of((new AssignmentNode(operationNode.get(),new OperationNode(operationNode.get(),OperationNode.type.PREINC),OperationNode.type.PREINC)));
                }
                else if (token.get().tokenType == Token.TokenType.MINUSX2) {
                    tokenManager.MatchAndRemove(Token.TokenType.MINUSX2);
                    Node operationNode = ParseOperation().get();
                    return Optional.of((new AssignmentNode(operationNode,new OperationNode(operationNode,OperationNode.type.PREDEC),OperationNode.type.PREDEC)));
                }
            }
            Optional<FunctionCallNode> functionCall = parseFunctionCall();
            if (functionCall.isPresent()) {
                return Optional.of(functionCall.get());
            }
            Optional<Node> parsePost = parsePost();
            if(parsePost.isPresent()){
                return parsePost;
            }
            return ParseLValue();
        }


        return Optional.empty(); // ReturnNode empty as a last resort.
    }
    public Optional<Node> parsePost() {
        Optional<Node> lValue = ParseLValue();
        if (lValue.isPresent()) {
            if (tokenManager.MoreTokens()) {
                AcceptSeparators();
                Optional<Token> token = tokenManager.Peek(0);
                if (token.isPresent()) {
                    AcceptSeparators();

                    if (token.get().tokenType == Token.TokenType.PLUSX2) {
                        tokenManager.MatchAndRemove(Token.TokenType.PLUSX2);
                        AcceptSeparators();
                        return Optional.of(new AssignmentNode(lValue.get(), new OperationNode(lValue.get(), OperationNode.type.POSTINC), OperationNode.type.POSTINC));
                    } else if (token.get().tokenType == Token.TokenType.MINUSX2) {
                        AcceptSeparators();
                        tokenManager.MatchAndRemove(Token.TokenType.MINUSX2);
                        return Optional.of(new AssignmentNode(lValue.get(), new OperationNode(lValue.get(), OperationNode.type.POSTDEC), OperationNode.type.POSTDEC));
                    }
                }
            }
        }
        return lValue;
    }
    /**
     * Parses the left side.
     * @return An optional node depending on token
     */
    public Optional ParseLValue() {
        var token = tokenManager.Peek(0);
        if (token.isPresent()) {
            if (token.get().tokenType == Token.TokenType.DOLLAR) {
                AcceptSeparators();
                tokenManager.MatchAndRemove(Token.TokenType.DOLLAR);
                Node node = ParseBottomLevel().get();
                return Optional.of(new OperationNode(node, OperationNode.type.DOLLAR));
            }
            if (token.get().tokenType == Token.TokenType.WORD) {
                tokenManager.MatchAndRemove(Token.TokenType.WORD);
                if (tokenManager.MatchAndRemove(Token.TokenType.OPENBRAC).isPresent()) {
                    Node node = ParseOperation().get();
                    tokenManager.MatchAndRemove(Token.TokenType.CLOSEBRAC);
                    //save name and node if theres a []
                    return Optional.of(new VariableReferenceNode(token.get().value, Optional.of(node)));

                } else {
                    //just saves the name.
                    return Optional.of(new VariableReferenceNode(token.get().value));
                }
            }


        }
        return Optional.empty();            //Last Resort
    }



    /**
     * Starts the recursion!
     * @return
     */
    public Optional<Node> ParseOperation() {
        Optional<Node> left = ParseAssignment();
        return left;
    }

    /**
     * @return Par  sed Concatenation
     */
    public Optional<Node> Parseconcatenation() {
        Optional<Node> left = ParseAdditionOrSubtraction();
        Optional<Node> right = ParseAdditionOrSubtraction();

        if (left.isPresent() && right.isPresent() &&
                left.get() instanceof VariableReferenceNode &&
                right.get() instanceof VariableReferenceNode){

            left = Optional.of(new OperationNode(left.get(), OperationNode.type.CONCATENATION, Optional.of(right.get())));
        }

        return left;
    }

    /**
     * @return Comparsions Parsed
     */
    public Optional<Node> ParseComparison() {
        Optional<Node> left = Parseconcatenation();
        while (true) {
            Optional<Token> equalOp = tokenManager.MatchAndRemove(Token.TokenType.EQUALX2);
            Optional<Token> notEqualOp = tokenManager.MatchAndRemove(Token.TokenType.NOTEQUAL);
            Optional<Token> lessThanOp = tokenManager.MatchAndRemove(Token.TokenType.LESS);
            Optional<Token> lessThanOrEqualOp = tokenManager.MatchAndRemove(Token.TokenType.LESSOREQUAL);
            Optional<Token> greaterThanOp = tokenManager.MatchAndRemove(Token.TokenType.GREATER);
            Optional<Token> greaterThanOrEqualOp = tokenManager.MatchAndRemove(Token.TokenType.GREATEROREQUAL);
            if (equalOp.isPresent()) {
                Optional<Node> right = ParseAdditionOrSubtraction();
                left = Optional.of(new OperationNode(left.get(), OperationNode.type.EQ, right));
            } else if (notEqualOp.isPresent()) {
                Optional<Node> right = ParseAdditionOrSubtraction();
                left = Optional.of(new OperationNode(left.get(), OperationNode.type.NE, right));
            } else if (lessThanOp.isPresent()) {
            } else if (lessThanOrEqualOp.isPresent()) {
                Optional<Node> right = ParseAssignment();
                if(right.isPresent()){
                    OperationNode assign = new OperationNode(left.get(),OperationNode.type.LE,right);
                    return Optional.of(new AssignmentNode(left.get(),assign));
                }
                else{
                    throw new RuntimeException("bad");
                }
            } else if (greaterThanOp.isPresent()) {
                Optional<Node> right = ParseAdditionOrSubtraction();
                left = Optional.of(new OperationNode(left.get(), OperationNode.type.GT, right));
            } else if (greaterThanOrEqualOp.isPresent()) {
                Optional<Node> right = ParseAdditionOrSubtraction();
                left = Optional.of(new OperationNode(left.get(), OperationNode.type.GE, right));
            } else {
                return left;
            }
        }
    }

    public Optional<Node> ParseTernary() {
        Optional<Node> condition = ParseLogicalOr();
        if (tokenManager.MatchAndRemove(Token.TokenType.QUESTION).isPresent()) {
            Optional<Node> trueCase = ParseLogicalOr();
            if (tokenManager.MatchAndRemove(Token.TokenType.COLON).isPresent()) {
                Optional<Node> falseCase = ParseTernary();
                return Optional.of(new TernaryNode(condition.get(), trueCase.get(), falseCase.get()));
            } else {
                throw new RuntimeException("Missing ':' in ternary expression.");
            }
        }
        return condition;
    }

    /**
     * @return Parsed addition / Subtraction
     */
    public Optional<Node> ParseAdditionOrSubtraction() {
        Optional<Node> left = ParseMultiplicationOrDivision();
        while (true) {
            Optional<Token> additionOp = tokenManager.MatchAndRemove(Token.TokenType.PLUS);
            Optional<Token> subtractionOp = tokenManager.MatchAndRemove(Token.TokenType.MINUS);
            if (additionOp.isPresent()) {
                Optional<Node> right = ParseMultiplicationOrDivision();
                left = Optional.of(new OperationNode(left.get(), OperationNode.type.ADD, right));
            } else if (subtractionOp.isPresent()) {
                Optional<Node> right = ParseMultiplicationOrDivision();
                left = Optional.of(new OperationNode(left.get(), OperationNode.type.SUBTRACT, right));
            } else {
                return left;
            }
        }
    }


    /**
     * @return parsed multiaciton division
     */
    public Optional<Node> ParseMultiplicationOrDivision() {
        Optional<Node> left = ParsePower();
        while (true) {
            Optional<Token> multiplicationOp = tokenManager.MatchAndRemove(Token.TokenType.STAR);
            Optional<Token> divisionOp = tokenManager.MatchAndRemove(Token.TokenType.SLASH);
            Optional<Token> modOp = tokenManager.MatchAndRemove(Token.TokenType.PERCENT);

            if (multiplicationOp.isPresent()) {
                Optional<Node> right = ParsePower();
                left = Optional.of(new OperationNode(left.get(), OperationNode.type.MULTIPLY, right));
            } else if (divisionOp.isPresent()) {
                Optional<Node> right = ParsePower();
                left = Optional.of(new OperationNode(left.get(), OperationNode.type.DIVIDE, right));
            } else if (modOp.isPresent()) {
                Optional<Node> right = ParsePower();
                left = Optional.of(new OperationNode(left.get(), OperationNode.type.MODULO, right));
            } else {
                return left;
            }
        }
    }

    /**
     * Parse logical And
     * @return parsed logical and
     */
    public Optional<Node> ParseLogicalAnd() {
        Optional<Node> left = ParseArrayMembership();
        while (true) {
            Optional op = tokenManager.MatchAndRemove(Token.TokenType.AND);
            if (op.isEmpty()) {
                return left;
            }
            Optional<Node> right = ParseArrayMembership();
            left = Optional.of(new OperationNode(left.get(), OperationNode.type.AND, right));
        }
    }

    /**
     * Parse logical or
     * @return parsed logical or
     */
    public Optional<Node> ParseLogicalOr() {
        Optional<Node> left = ParseLogicalAnd();
        while (true) {
            Optional op = tokenManager.MatchAndRemove(Token.TokenType.OR);
            if (op.isEmpty()) {
                return left;
            }
            Optional<Node> right = ParseLogicalAnd();
            left = Optional.of(new OperationNode(left.get(), OperationNode.type.OR, right));
        }
    }


    /**
     * @return parses power
     */
    public Optional<Node> ParsePower() {
        Optional<Node> left = ParseBottomLevel();
        while (true) {
            Optional op = tokenManager.MatchAndRemove(Token.TokenType.EXPO);
            if (op.isEmpty()) {
                return left;
            }
            Optional<Node> right = ParsePower();
            left = Optional.of(new OperationNode(left.get(), OperationNode.type.EXPONENT, right));
        }
    }



    /**
     * @return assingment node or operation node
     */
    public Optional<Node> ParseAssignment() {
        Optional<Node> left = ParseTernary();
        while (true) {
            boolean operatorFound = false;
            if (tokenManager.MatchAndRemove(Token.TokenType.EXPOMATH).isPresent()) {
                Optional<Node> right = ParseAssignment();
                left = Optional.of(new OperationNode(left.get(), OperationNode.type.EXPONENT, right));
                operatorFound = true;
                return Optional.of(new AssignmentNode(left.get(), right.get(), OperationNode.type.ASSIGN));
            }
            if (tokenManager.MatchAndRemove(Token.TokenType.PERCENTMATH).isPresent()) {
                Optional<Node> right = ParseAssignment();
                left = Optional.of(new OperationNode(left.get(), OperationNode.type.MODULO, right));
                operatorFound = true;
                return Optional.of(new AssignmentNode(left.get(), right.get(), OperationNode.type.ASSIGN));

            }
            if (tokenManager.MatchAndRemove(Token.TokenType.STARMATH).isPresent()) {
                Optional<Node> right = ParseAssignment();
                left = Optional.of(new OperationNode(left.get(), OperationNode.type.MULTIPLY, right));
                operatorFound = true;
                return Optional.of(new AssignmentNode(left.get(), right.get(), OperationNode.type.ASSIGN));

            }

            if (tokenManager.MatchAndRemove(Token.TokenType.DIVIDEMATH).isPresent()) {
                Optional<Node> right = ParseAssignment();
                left = Optional.of(new OperationNode(left.get(), OperationNode.type.DIVIDE, right));
                operatorFound = true;
                return Optional.of(new AssignmentNode(left.get(), right.get(), OperationNode.type.ASSIGN));

            }

            if (tokenManager.MatchAndRemove(Token.TokenType.SUM).isPresent()) {
                Optional<Node> right = ParseAssignment();
                left = Optional.of(new OperationNode(left.get(), OperationNode.type.ADD, right));
                operatorFound = true;
                return Optional.of(new AssignmentNode(right.get(),left.get(), OperationNode.type.ASSIGN));

            }

            if (tokenManager.MatchAndRemove(Token.TokenType.MINUSMATH).isPresent()) {
                Optional<Node> right = ParseAssignment();
                left = Optional.of(new OperationNode(left.get(), OperationNode.type.SUBTRACT, right));
                operatorFound = true;
                return Optional.of(new AssignmentNode(left.get(), right.get(), OperationNode.type.ASSIGN ));

            }

            if (tokenManager.MatchAndRemove(Token.TokenType.EQUAL).isPresent()) {
                Optional<Node> right = ParseAssignment();
                if(right.isPresent()){
                    OperationNode assign = new OperationNode(left.get(),OperationNode.type.ASSIGN,right);
                    return Optional.of(new AssignmentNode(left.get(),assign));
                }
               else{
                   throw new RuntimeException("bad");
                }
            }

            if (!operatorFound) {
                Optional<Node> right = ParseTernary();
                break;
            }
        }
        Optional<Node> right = ParseTernary();
        return left;
    }


    /**
     * @param programNode
     * @return true if BEGIN or END is present
     */
    public boolean parseAction(ProgramNode programNode) {
        boolean blockFound = false;
        if (tokenManager.MatchAndRemove(Token.TokenType.BEGIN).isPresent()) {
            AcceptSeparators();
            BlockNode begin = parseBlock();
            programNode.addBeginBlocksNodes(begin);
            blockFound = true;
        }

        else if (tokenManager.MatchAndRemove(Token.TokenType.END).isPresent()) {
            AcceptSeparators();
            BlockNode end = parseBlock();
            programNode.addEndBlockNodes(end);
            blockFound = true;
        }

        else {
            AcceptSeparators();
            Optional<Node> conditon=ParseOperation();
            BlockNode body = parseBlock();
            if(conditon.isPresent()){
                body.setCondition(conditon);
                programNode.addOtherBlockNodes(body);

            }
            return false;
        }

        return blockFound;
    }

    /**
     * @return a block node.
     */
    public BlockNode parseBlock() {
        BlockNode blockNode = new BlockNode();
        AcceptSeparators();
        if (tokenManager.MatchAndRemove(Token.TokenType.OPENCURLY).isPresent()) {
            AcceptSeparators();

            while (true) {
                AcceptSeparators();
                Optional<StatementNode> statement = parseStatement();
                AcceptSeparators();
                tokenManager.MatchAndRemove(Token.TokenType.CLOSEPAR);
                if (statement.isPresent()) {
                    blockNode.addToLinkedList(statement.get());
                    AcceptSeparators();
                } else {
                    break;
                }
            }
            AcceptSeparators();
            if (!tokenManager.MatchAndRemove(Token.TokenType.CLOSECURLY).isPresent()) {
                // Handle missing closing curly brace error
                throw new RuntimeException("Missing closing curly brace.");
            }
            AcceptSeparators();
        } else {
            // Handle the case where there's no opening curly brace.
            // This part is important to avoid empty blocks.
            Optional<StatementNode> statement = parseStatement();
            if (statement.isPresent()) {
                blockNode.addToLinkedList(statement.get());
                AcceptSeparators();
            }
        }

        return blockNode;
    }




    /**
     * Calls for the parsing of statements
     * @return parsed statement
     */
    public Optional<StatementNode> parseStatement() {
        if (tokenManager.MatchAndRemove(Token.TokenType.CONTINUE).isPresent()) {
            AcceptSeparators();
            return parseContinue();
        } else if (tokenManager.MatchAndRemove(Token.TokenType.BREAK).isPresent()) {
            AcceptSeparators();
            return parseBreak();
        } else if (tokenManager.MatchAndRemove(Token.TokenType.IF).isPresent()) {
            AcceptSeparators();
            return parseIf();
        } else if (tokenManager.MatchAndRemove(Token.TokenType.FOR).isPresent()) {
            AcceptSeparators();
            return parseFor();
        } else if (tokenManager.MatchAndRemove(Token.TokenType.DELETE).isPresent()) {
            AcceptSeparators();
            return parseDelete();
        } else if (tokenManager.MatchAndRemove(Token.TokenType.WHILE).isPresent()) {
            AcceptSeparators();
            return parseWhile();
        } else if (tokenManager.MatchAndRemove(Token.TokenType.DO).isPresent()) {
            AcceptSeparators();
            return parseDoWhile();
        } else if (tokenManager.MatchAndRemove(Token.TokenType.RETURN).isPresent()) {
            AcceptSeparators();
            return parseReturn();
        } else {
            Optional<Node> parseOp = ParseOperation();
            if (parseOp.isPresent()) {
                //Node operation = parseOp.get();
                if (parseOp.get() instanceof AssignmentNode ||
                        parseOp.get() instanceof FunctionCallNode) {
                    return Optional.of((StatementNode) parseOp.get());
                }
            }
        }
        return Optional.empty();
    }
    /**
     * Parse delete
     * @return statement node
     */
    public Optional<StatementNode> parseDelete() {
        // Parse the target for the delete operation
        Optional<Node> target = ParseLValue(); // Implement this method
        if (target != null) {
            return Optional.of(new DeleteNode(target.get()));
        } else {
            throw new RuntimeException("Invalid target for delete operation");
        }


    }



    /**
     * @return parsed while
     */
    public Optional<StatementNode> parseWhile() {
        Optional<Token> curr = tokenManager.Peek(0);
        if(curr.isPresent()) {
            if (curr.get().tokenType == Token.TokenType.OPENPAR) {
                tokenManager.MatchAndRemove(Token.TokenType.OPENPAR);
                AcceptSeparators();
                Optional<Node> condition = ParseOperation();
                curr = tokenManager.Peek(0);
                if (curr.get().tokenType == Token.TokenType.CLOSEPAR) {
                    AcceptSeparators();
                    tokenManager.MatchAndRemove(Token.TokenType.CLOSEPAR);
                    AcceptSeparators();
                    BlockNode body = parseBlock();
                    WhileNode whileNode = new WhileNode(condition.get(), body);
                    return Optional.of(whileNode);
                }
            } else {
                throw new RuntimeException("Missing curly brace");
            }
        }

        return Optional.empty();
    }

    /**
     * parses return
     * @return parsed return
     */
    public Optional<StatementNode> parseReturn() {
        Optional<Node> expression = ParseOperation();
        if (expression.isPresent()) {
            return Optional.of(new ReturnNode(expression));
        }

        return Optional.empty();
    }

    /**
     * Parse do While
     * @return parsed do while
     */
    public Optional<StatementNode> parseDoWhile() {
        BlockNode body = parseBlock();
        if (tokenManager.MatchAndRemove(Token.TokenType.WHILE).isPresent()) {
            AcceptSeparators();

            if (tokenManager.MatchAndRemove(Token.TokenType.OPENPAR).isPresent()) {
                AcceptSeparators();

                Optional<Node> condition = ParseOperation();

                if (condition.isPresent()) {
                    AcceptSeparators();

                    if (tokenManager.MatchAndRemove(Token.TokenType.CLOSEPAR).isPresent()) {
                        return Optional.of(new DoWhileNode(condition.get(), body));
                    } else {
                        throw new RuntimeException("Missing closing parenthesis");
                    }
                } else {
                    throw new RuntimeException("Invalid condition");
                }
            } else {
                throw new RuntimeException("Missing opening parenthesis");
            }
        } else {
            throw new RuntimeException("Missing 'while'");
        }
    }

    /**
     * Parse For and for each
     * @return parsed for method
     */
    public Optional<StatementNode> parseFor() {
        if(tokenManager.MatchAndRemove(Token.TokenType.OPENPAR).isPresent()) {
            if(tokenManager.Peek(1).get().tokenType== Token.TokenType.IN){
                return parseForEach();
            }
            Optional<Node> initialization = ParseOperation();
            AcceptSeparators();
            Optional<Node> condition = ParseOperation();
            AcceptSeparators();
            Optional<Node> increment = ParseOperation();
            if(tokenManager.MatchAndRemove(Token.TokenType.CLOSEPAR).isPresent()) {
                BlockNode body = parseBlock();
                ForNode forNode = new ForNode(initialization.get(),condition.get(),  increment.get(), body);
                return Optional.of(forNode);
            }
        }
        return Optional.empty();
    }

    /**
     * @return parsed for each
     */
    public Optional<StatementNode> parseForEach() {
        Optional<Node> condtion = ParseOperation();
        AcceptSeparators();
        if(tokenManager.MatchAndRemove(Token.TokenType.CLOSEPAR).isPresent()) {
            BlockNode body = parseBlock();
            ForEachNode forEachNode = new ForEachNode(condtion,body);
            return Optional.of(forEachNode);
        }
        return Optional.empty();
    }


    /**
     * @return parsed Continue
     */
    public Optional<StatementNode> parseContinue() {
        return Optional.of(new ContinueNode());
    }

    /**
     * @return parsed break
     */
    public Optional<StatementNode> parseBreak() {
        return Optional.of(new BreakNode());
    }

    /**
     * @return parsed if
     */
    public Optional<StatementNode> parseIf() {
        if (!tokenManager.MatchAndRemove(Token.TokenType.OPENPAR).isPresent()) {
            throw new RuntimeException("Bad Open Par");
        }
        Optional<Node> condition = ParseOperation();
        AcceptSeparators();
        if (!tokenManager.MatchAndRemove(Token.TokenType.CLOSEPAR).isPresent()) {
            throw new RuntimeException("Bad Close Par");
        }
        BlockNode ifBlock = parseBlock();
        IfNode ifNode = new IfNode(condition, ifBlock);
        IfNode currentIf = ifNode;
        // Handle multiple if-else blocks
        while (tokenManager.MatchAndRemove(Token.TokenType.ELSE).isPresent()) {

            if (tokenManager.MatchAndRemove(Token.TokenType.IF).isPresent()) {
                if (!tokenManager.MatchAndRemove(Token.TokenType.OPENPAR).isPresent()) {
                    throw new RuntimeException("Bad Open Par");
                }
                Optional<Node> elseIfCondition = ParseOperation();
                if (!tokenManager.MatchAndRemove(Token.TokenType.CLOSEPAR).isPresent()) {
                    throw new RuntimeException("Bad Close Par");
                }
                BlockNode elseIfBlock = parseBlock();
                AcceptSeparators();
                IfNode elseIfNode = new IfNode(elseIfCondition, elseIfBlock);
                currentIf.setIfNode(elseIfNode);
                currentIf = elseIfNode;
            } else {
                BlockNode elseBlock = parseBlock();
                AcceptSeparators();
                currentIf.setIfNode(new IfNode(Optional.empty(), elseBlock));
                break;
            }
        }

        return Optional.of(ifNode);
    }




    public Node Factor() {
        Optional<Token> num = tokenManager.MatchAndRemove(Token.TokenType.NUMBER);
        if (num.isPresent()) {
            return new ConstantNode(num.get().value);
        }

        if (tokenManager.MatchAndRemove(Token.TokenType.OPENPAR).isPresent()) {
            Node exp = Expression();
            if (exp == null) {
                throw new RuntimeException("Expression is null.");
            }

            if (!tokenManager.MatchAndRemove(Token.TokenType.CLOSEPAR).isPresent()) {
                throw new RuntimeException("Missing closing parenthesis.");
            }

            return exp;
        }

        throw new RuntimeException("Invalid syntax in factor.");
    }

    public Node Term() {
        Node left = Factor();

        while (true) {
            Optional<Token> op = tokenManager.MatchAndRemove(Token.TokenType.STAR);
            if (!op.isPresent()) {
                op = tokenManager.MatchAndRemove(Token.TokenType.SLASH);
            }

            if (!op.isPresent()) {
                return left;
            }

            Node right = Factor();
            left = new MathOpNode(left, tokenTypeToOperatorType(op), right);
        }
    }

    public Node Expression() {
        Node left = Term();

        while (true) {
            Optional<Token> op = tokenManager.MatchAndRemove(Token.TokenType.PLUS);
            if (!op.isPresent()) {
                op = tokenManager.MatchAndRemove(Token.TokenType.MINUS);
            }

            if (!op.isPresent()) {
                return left;
            }

            Node right = Term();
            left = new MathOpNode(left, tokenTypeToOperatorType(op), right);
        }
    }

    /**
     * Converts token to math type
     * @param opNode
     * @return math node
     */
    public MathOpNode.Operator tokenTypeToOperatorType(Optional<Token> opNode) {
        switch (opNode.get().tokenType) {
            case PLUS -> {
                return MathOpNode.Operator.PLUS;
            }
            case MINUS -> {
                return MathOpNode.Operator.MINUS;
            }
            case SLASH -> {
                return MathOpNode.Operator.SLASH;
            }
            case STAR -> {
                return MathOpNode.Operator.STAR;
            }
            default -> {
                return null;
            }
        }
    }

    /**
     * @return parssed Array membership
     */
    public Optional<Node> ParseArrayMembership() {
        Optional<Node> left = ParseEREMatchOrNonMatch();
        if (tokenManager.MatchAndRemove(Token.TokenType.IN).isPresent()) {
            Optional<Node> right = ParseLValue();
            if (right.isPresent()) {
                return Optional.of(new OperationNode(left.get(), OperationNode.type.IN, Optional.of(right).get()));
            } else {
                throw new RuntimeException("JUST S/U THE COURSE AT THIS POINT");
            }
        }

        return left;
    }



    /**
     *
     * @return parsed match
     */
    public Optional<Node> ParseEREMatchOrNonMatch() {
        Optional<Node> left = ParseComparison();

        if (tokenManager.MatchAndRemove(Token.TokenType.TILDE).isPresent()) {
            Optional<Node> right = ParseComparison();
            return Optional.of((new OperationNode(left.get(), OperationNode.type.MATCH, right)));
        }

        if (tokenManager.MatchAndRemove(Token.TokenType.NOTMATCH).isPresent()) {
            Optional<Node> right = ParseComparison();
            return Optional.of(new OperationNode(left.get(), OperationNode.type.NOTMATCH));
        }

        return left;
    }

}