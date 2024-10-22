
import java.util.*;

public class TreeNode {
    private String uid;
    private String symbol;
    private List<TreeNode> children;

    public TreeNode(String uid, String symbol) {
        this.uid = uid;
        this.symbol = symbol;
        this.children = new ArrayList<>();
    }

    public void addChild(TreeNode child) { 
        children.add(child);
    }

    public String getSymbol() {
        return symbol;
    }

    public String getUId() {
        return uid;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        String outputString = String.format("NODE: %s \tSYMBOL: %s\n", uid, symbol);
        
        // for (TreeNode node : children) {
            // outputString += String.format("[%s]-%s",node.getUId(),node.getSymbol());
        // }
        // outputString += "\n===============\n";
        
        return outputString;
    }
}
