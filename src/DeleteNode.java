import java.util.ArrayList;
import java.util.List;

public class DeleteNode extends StatementNode {
    private Node arrayName;
    private List<String> indices;

    // Constructor for deleting the entire array
    public DeleteNode(Node arrayName) {
        this.arrayName = arrayName;
        this.indices = new ArrayList<>();
    }

    // Constructor for deleting specific indices
    public DeleteNode(Node arrayName, List<String> indices) {
        this.arrayName = arrayName;
        this.indices = indices;
    }

    public String getArrayName() {
        return arrayName.toString();
    }

    public List<String> getIndices() {
        return indices;
    }

    @Override
    public String toString() {
        if (indices.isEmpty()) {
            return "delete: " + arrayName;
        } else {
            String result = "delete: " + arrayName + "[";

            // Loop through indices and concatenate
            for (int i = 0; i < indices.size(); i++) {
                result += indices.get(i);
                if (i < indices.size() - 1) {
                    result += ",";
                }
            }

            result += "]";
            return result;
        }
    }

}
