import java.util.LinkedList;

public class ProgramNode extends Node {
    private LinkedList<BlockNode> BeginBlockNodes;
    private LinkedList<BlockNode> EndBlockNodes;
    private LinkedList<BlockNode> OtherBlockNodes;
    private LinkedList<FunctionDefintionNode> FunctionDefinitionNodes;


    /**
     * Creates A program node
     * @param BeginBlockNodes
     * @param EndBlockNodes
     * @param OtherBlockNodes
     * @param FunctionDefinitionNodes
     */
    public ProgramNode(LinkedList BeginBlockNodes, LinkedList EndBlockNodes, LinkedList OtherBlockNodes, LinkedList FunctionDefinitionNodes){
        this.BeginBlockNodes=BeginBlockNodes;
        this.EndBlockNodes=EndBlockNodes;
        this.OtherBlockNodes=OtherBlockNodes;
        this.FunctionDefinitionNodes=FunctionDefinitionNodes;

    }

    /**
     * Creates a program node
     * without parameters
     */
    public ProgramNode(){
        this.BeginBlockNodes = new LinkedList<BlockNode>();
        this.EndBlockNodes = new LinkedList<BlockNode>();
        this.OtherBlockNodes = new LinkedList<BlockNode>();
        this.FunctionDefinitionNodes = new LinkedList<FunctionDefintionNode>();

    }

    /**
     * @return begin block nodes
     */
    public LinkedList<BlockNode> getBeginBlockNodes(){
        return BeginBlockNodes;
    }

    /**
      * @return end block nodes
     */
    public LinkedList<BlockNode>  getEndBlockNodes(){
        return EndBlockNodes;
    }

    /**
     * @return get other block nodes
     */
    public LinkedList<BlockNode>  getOtherBlockNodes(){
        return OtherBlockNodes;
    }

    /**
     * @return get function definition nodes
     */
    public LinkedList<FunctionDefintionNode>  getFunctionDefinitionNodes(){
        return FunctionDefinitionNodes;
    }

    /**
     * add begin blocks nodes
      * @param block
     */
    public void addBeginBlocksNodes(BlockNode block){
        BeginBlockNodes.add(block);
    }

    /**
     * add end block nodes
     * @param block
     */
    public void addEndBlockNodes(BlockNode block){
        EndBlockNodes.add(block);
    }

    /**
     * add other block nodes
     * @param block
     */
    public void addOtherBlockNodes(BlockNode block){
        OtherBlockNodes.add(block);
    }

    /**
     * add function definition
     * @param block
     */
    public void addFunctionDefinitionNodes(FunctionDefintionNode block){
        FunctionDefinitionNodes.add(block);
    }


    @Override
    public String toString() {

            return "Program Node:" + FunctionDefinitionNodes + "\n" +
                    BeginBlockNodes + "\n" + EndBlockNodes + "\n" + OtherBlockNodes;

    }
}
