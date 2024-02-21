

public class ReturnType{

    private String name="";
    private Type type;

    /**
     * Makes a return type node with name and type
     * @param name
     * @param type
     */
    public ReturnType(String name, Type type) {

        this.type=type;
        this.name=name;
    }
    /**
     * Makes a return type node only with type
     * @param type
     */
    public ReturnType(Type type)  {
        this.type=type;
    }

    /**
      * @return type of node
     */
    public Type getType(){
        return this.type;
    }

    @Override
    public String toString() {
        if(name==""){
            return "Return: "+this.type.toString();
        }
        return "Return: "+this.name+" Type: "+this.type.toString();

    }

    enum Type{
        RETURN,BREAK,CONTINUE,DELETE,NORMAL,NONE
    }
}
