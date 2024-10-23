import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TypeChecker {

  public static void main(String[] args) {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse("output/parserOutput.xml");
      NodeList rootNodes = doc.getElementsByTagName("ROOT");

      if (rootNodes.getLength() == 0) {
        System.err.println("No ROOT element found in the XML");
        return;
      }

      Node xmlRoot = rootNodes.item(0);

      SyntaxTreeNode syntaxTreeRoot =
          SyntaxTreeBuilder.buildSyntaxTree(xmlRoot);

      NewSymbolTable symbolTable = new NewSymbolTable();

      if (syntaxTreeRoot == null) {
        System.err.println("Failed to build syntax tree");
        return;
      }

      boolean typeCheckResult = syntaxTreeRoot.typeCheck(symbolTable);

      if (typeCheckResult) {
        System.out.println("Type checking completed successfully");
      } else {
        System.out.println("Type checking failed");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
