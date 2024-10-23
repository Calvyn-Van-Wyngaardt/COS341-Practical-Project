import java.util.List;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeListImpl implements NodeList {
  private List<Node> nodes;

  public NodeListImpl(List<Node> nodes) { this.nodes = nodes; }

  @Override
  public Node item(int index) {
    if (index >= 0 && index < nodes.size()) {
      return nodes.get(index);
    }
    return null;
  }

  @Override
  public int getLength() {
    return nodes.size();
  }
}
