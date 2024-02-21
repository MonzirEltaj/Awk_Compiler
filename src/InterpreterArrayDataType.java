import java.util.HashMap;

public class InterpreterArrayDataType extends InterpreterDataType{
     HashMap<String, InterpreterDataType> IADT ;

    public InterpreterArrayDataType(String s) {
        IADT = new HashMap<>();
    }
    public InterpreterArrayDataType() {
        IADT = new HashMap<>();
    }

    public HashMap getArrayData(){
        return IADT;
    }

    public HashMap<String, InterpreterDataType> addElement (String s, InterpreterDataType idt){
        IADT.put(s,idt);
        return IADT;
    }

}
