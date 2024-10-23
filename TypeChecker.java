import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TypeChecker {

  public static void main(String[] args) {
    try {
      // Parse the XML syntax tree
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse("parserOutput.xml");
      NodeList rootNodes = doc.getElementsByTagName("ROOT");

      if (rootNodes.getLength() == 0) {
        System.err.println("No ROOT element found in the XML.");
        return;
      }

      Node xmlRoot = rootNodes.item(0);

      // Build the syntax tree
      SyntaxTreeNode syntaxTreeRoot =
          SyntaxTreeBuilder.buildSyntaxTree(xmlRoot);

      // Initialize the symbol table
      RecSPLSymbolTable symbolTable = new RecSPLSymbolTable();

      // Perform type checking
      if (syntaxTreeRoot == null) {
        System.err.println("Failed to build syntax tree.");
        return;
      }

      boolean typeCheckResult = syntaxTreeRoot.typeCheck(symbolTable);


      if (typeCheckResult) {
        System.out.println("Type checking passed.");
      } else {
        System.out.println("Type checking failed.");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
