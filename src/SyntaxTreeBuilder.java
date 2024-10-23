import java.util.*;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SyntaxTreeBuilder {

  private static Map<String, Node> nodeMap = new HashMap<>();

  public static SyntaxTreeNode buildSyntaxTree(Node xmlNode) {
    nodeMap.clear();
    buildNodeMap(xmlNode);

    String symb = getSymbol(xmlNode);

    if (symb == null) {
      return null;
    }

    switch (symb) {
    case "PROG":
      return buildProgNode(xmlNode);
    case "GLOBVARS":
      return buildGlobVarsNode(xmlNode);
    case "ALGO":
      return buildAlgoNode(xmlNode);
    case "INSTRUC":
      return buildInstrucNode(xmlNode);
    case "COMMAND":
      return buildCommandNode(xmlNode);
    case "ASSIGN":
      return buildAssignNode(xmlNode);
    case "ATOMIC":
      return buildAtomicNode(xmlNode);
    case "COND":
      return buildCondNode(xmlNode);
    case "SIMPLE":
      return buildSimpleNode(xmlNode);
    case "BINOP":
      return buildBinOpNode(xmlNode);
    case "CONST":
      return buildConstNode(xmlNode);
    case "VNAME":
      return buildVNameNode(xmlNode);
    case "BRANCH":
      return buildBranchNode(xmlNode);
    default:
      String terminal = getTerminal(xmlNode);
      if (terminal != null) {
        return new AtomicNode(terminal, 'u');
      }
      return null;
    }
  }

  private static void buildNodeMap(Node node) {
    if (node == null)
      return;

    String unid = null;

    NodeList childNodes = node.getChildNodes();
    for (int i = 0; i < childNodes.getLength(); i++) {
      Node child = childNodes.item(i);
      if ("UNID".equals(child.getNodeName())) {
        unid = child.getTextContent().trim();
        nodeMap.put(unid, node);
        break;
      }
    }

    if ("LEAF".equals(node.getNodeName())) {
      return;
    }

    for (int i = 0; i < childNodes.getLength(); i++) {
      Node child = childNodes.item(i);
      buildNodeMap(child);
    }
  }

  private static String getSymbol(Node node) {
    Node symbAttr = node.getAttributes() != null
                        ? node.getAttributes().getNamedItem("SYMB")
                        : null;
    if (symbAttr != null) {
      return symbAttr.getNodeValue();
    }

    NodeList childNodes = node.getChildNodes();
    for (int i = 0; i < childNodes.getLength(); i++) {
      Node child = childNodes.item(i);
      if ("SYMB".equals(child.getNodeName())) {
        return child.getTextContent().trim();
      }
    }

    return null;
  }

  private static NodeList getChildren(Node node) {
    NodeList childNodes = node.getChildNodes();
    for (int i = 0; i < childNodes.getLength(); i++) {
      Node child = childNodes.item(i);
      if ("CHILDREN".equals(child.getNodeName())) {
        return getNodesByIds(child);
      }
    }
    return null;
  }

  private static NodeList getNodesByIds(Node childrenNode) {
    NodeList idNodes = childrenNode.getChildNodes();
    List<Node> nodes = new ArrayList<>();

    for (int i = 0; i < idNodes.getLength(); i++) {
      Node idNode = idNodes.item(i);
      if ("ID".equals(idNode.getNodeName())) {
        String id = idNode.getTextContent().trim();
        Node actualNode = nodeMap.get(id);
        if (actualNode != null) {
          nodes.add(actualNode);
        }
      }
    }

    return new NodeListImpl(nodes);
  }

  private static String getTerminal(Node node) {
    if ("LEAF".equals(node.getNodeName())) {
      NodeList children = node.getChildNodes();
      for (int i = 0; i < children.getLength(); i++) {
        Node terminalNode = children.item(i);
        if ("TERMINAL".equals(terminalNode.getNodeName())) {
          return terminalNode.getTextContent().trim();
        }
      }
    }
    return null;
  }

  private static SyntaxTreeNode buildProgNode(Node xmlNode) {
    NodeList children = getChildren(xmlNode);
    SyntaxTreeNode globVars = null, algo = null, functions = null;

    if (children != null) {
      for (int i = 0; i < children.getLength(); i++) {
        Node child = children.item(i);
        String symb = getSymbol(child);
        if ("GLOBVARS".equals(symb)) {
          globVars = buildGlobVarsNode(child);
        } else if ("ALGO".equals(symb)) {
          algo = buildAlgoNode(child);
        } else if ("FUNCTIONS".equals(symb)) {
          functions = buildFunctionsNode(child);
        }
      }
    }
    return new ProgNode(globVars, algo, functions);
  }

  private static SyntaxTreeNode buildGlobVarsNode(Node xmlNode) {
    NodeList children = getChildren(xmlNode);
    List<VariableDeclaration> declarations = new ArrayList<>();

    if (children != null) {
      for (int i = 0; i < children.getLength(); i++) {
        Node child = children.item(i);
        String symb = getSymbol(child);

        if ("VARDECL".equals(symb)) {
          VariableDeclaration decl = buildVarDeclNode(child);
          if (decl != null) {
            System.out.println("Parsed variable declaration: " +
                               decl.getName() + " of type " + decl.getType());
            declarations.add(decl);
          }
        }
      }
    }

    if (declarations.isEmpty()) {
      System.out.println("No variable declarations found in GLOBVARS.");
    }

    return new GlobVarsNode(declarations);
  }

  private static VariableDeclaration buildVarDeclNode(Node xmlNode) {
    NodeList children = getChildren(xmlNode);
    char type = 'u';
    String name = null;

    if (children != null) {
      for (int i = 0; i < children.getLength(); i++) {
        Node child = children.item(i);
        String symb = getSymbol(child);

        if ("VTYP".equals(symb)) {
          type = getTypeFromVTYP(child);
        } else if ("VNAME".equals(symb)) {
          name = getVName(child);
        }
      }
    }

    if (name != null && type != 'u') {
      return new VariableDeclaration(type, name);
    } else {
      System.err.println("Error parsing variable declaration.");
      return null;
    }
  }

  private static char getTypeFromVTYP(Node vtypNode) {
    NodeList children = getChildren(vtypNode);
    for (int i = 0; i < children.getLength(); i++) {
      Node leaf = children.item(i);
      String terminal = getTerminal(leaf);
      if ("num".equals(terminal)) {
        return 'n';
      } else if ("text".equals(terminal)) {
        return 't';
      }
    }
    return 'u';
  }

  private static Node getSibling(Node node, String symb) {
    Node parent = node.getParentNode();
    NodeList siblings = getChildren(parent);
    if (siblings != null) {
      for (int i = 0; i < siblings.getLength(); i++) {
        Node sibling = siblings.item(i);
        if (sibling != node && symb.equals(getSymbol(sibling))) {
          return sibling;
        }
      }
    }
    return null;
  }

  private static String getVName(Node vnameNode) {
    NodeList children = getChildren(vnameNode);
    if (children != null) {
      for (int i = 0; i < children.getLength(); i++) {
        Node leaf = children.item(i);
        String terminal = getTerminal(leaf);
        if (terminal != null) {
          return terminal;
        }
      }
    }
    return null;
  }

  private static SyntaxTreeNode buildAlgoNode(Node xmlNode) {
    NodeList children = getChildren(xmlNode);
    SyntaxTreeNode instruc = null;

    if (children != null) {
      for (int i = 0; i < children.getLength(); i++) {
        Node child = children.item(i);
        String symb = getSymbol(child);
        if ("INSTRUC".equals(symb)) {
          instruc = buildInstrucNode(child);
        }
      }
    }
    return new AlgoNode(instruc);
  }

  private static SyntaxTreeNode buildInstrucNode(Node xmlNode) {
    NodeList children = getChildren(xmlNode);
    List<SyntaxTreeNode> commands = new ArrayList<>();

    if (children != null) {
      for (int i = 0; i < children.getLength(); i++) {
        Node child = children.item(i);
        String symb = getSymbol(child);
        if ("COMMAND".equals(symb)) {
          SyntaxTreeNode commandNode = buildCommandNode(child);
          if (commandNode != null) {
            commands.add(commandNode);
          }
        }
      }
    }
    return new InstrucNode(commands);
  }

  private static SyntaxTreeNode buildCommandNode(Node xmlNode) {
    NodeList children = getChildren(xmlNode);
    if (children != null) {
      for (int i = 0; i < children.getLength(); i++) {
        Node child = children.item(i);
        String symb = getSymbol(child);
        if ("ASSIGN".equals(symb)) {
          return buildAssignNode(child);
        } else if ("BRANCH".equals(symb)) {
          return buildBranchNode(child);
        } else {
          String terminal = getTerminal(child);
          if ("print".equals(terminal)) {
            return buildPrintNode(xmlNode);
          }
        }
      }
    }
    return null;
  }

  private static SyntaxTreeNode buildAssignNode(Node xmlNode) {
    NodeList children = getChildren(xmlNode);
    String varName = null;
    SyntaxTreeNode expression = null;
    boolean isInput = false;

    if (children != null) {
      for (int i = 0; i < children.getLength(); i++) {
        Node child = children.item(i);
        String symb = getSymbol(child);

        if ("VNAME".equals(symb)) {
          varName = getVName(child);
        } else if ("input".equals(getTerminal(child))) {
          isInput = true;
        } else if ("ATOMIC".equals(symb) || "TERM".equals(symb)) {
          expression = buildSyntaxTree(child);
        }
      }
    }
    return new AssignNode(varName, expression, isInput);
  }

  private static SyntaxTreeNode buildPrintNode(Node xmlNode) {
    NodeList children = getChildren(xmlNode);
    SyntaxTreeNode atomic = null;

    if (children != null) {
      for (int i = 0; i < children.getLength(); i++) {
        Node child = children.item(i);
        if ("ATOMIC".equals(getSymbol(child))) {
          atomic = buildAtomicNode(child);
          break;
        }
      }
    }
    return new PrintNode(atomic);
  }

  private static SyntaxTreeNode buildBranchNode(Node xmlNode) {
    NodeList children = getChildren(xmlNode);
    SyntaxTreeNode condition = null;
    SyntaxTreeNode thenAlgo = null;
    SyntaxTreeNode elseAlgo = null;

    int algoCount = 0;

    if (children != null) {
      for (int i = 0; i < children.getLength(); i++) {
        Node child = children.item(i);
        String symb = getSymbol(child);

        if ("COND".equals(symb)) {
          condition = buildCondNode(child);
        } else if ("ALGO".equals(symb)) {
          if (algoCount == 0) {
            thenAlgo = buildAlgoNode(child);
            algoCount++;
          } else {
            elseAlgo = buildAlgoNode(child);
          }
        }
      }
    }
    return new BranchNode(condition, thenAlgo, elseAlgo);
  }

  private static SyntaxTreeNode buildCondNode(Node xmlNode) {
    NodeList children = getChildren(xmlNode);
    SyntaxTreeNode condition = null;

    if (children != null) {
      for (int i = 0; i < children.getLength(); i++) {
        Node child = children.item(i);
        String symb = getSymbol(child);
        if ("SIMPLE".equals(symb)) {
          condition = buildSimpleNode(child);
        }
      }
    }
    return condition;
  }

  private static SyntaxTreeNode buildSimpleNode(Node xmlNode) {
    NodeList children = getChildren(xmlNode);
    String operator = null;
    SyntaxTreeNode left = null;
    SyntaxTreeNode right = null;

    if (children != null) {
      for (int i = 0; i < children.getLength(); i++) {
        Node child = children.item(i);
        String symb = getSymbol(child);

        if ("BINOP".equals(symb)) {
          operator = getOperator(child);
        } else if ("ATOMIC".equals(symb)) {
          if (left == null) {
            left = buildAtomicNode(child);
          } else {
            right = buildAtomicNode(child);
          }
        }
      }
    }

    if (operator != null && left != null && right != null) {
      return new BinOpNode(operator, left, right);
    } else {
      System.err.println("Incomplete simple condition.");
      return null;
    }
  }

  private static String getOperator(Node binOpNode) {
    String operator = null;
    NodeList children = getChildren(binOpNode);
    if (children != null) {
      for (int i = 0; i < children.getLength(); i++) {
        Node leaf = children.item(i);
        operator = getTerminal(leaf);
        if (operator != null) {
          break;
        }
      }
    }
    if (operator == null) {
      operator = getTerminal(binOpNode);
    }
    return operator;
  }

  private static SyntaxTreeNode buildAtomicNode(Node xmlNode) {
    NodeList children = getChildren(xmlNode);
    if (children != null) {
      for (int i = 0; i < children.getLength(); i++) {
        Node child = children.item(i);
        String symb = getSymbol(child);
        if ("VNAME".equals(symb)) {
          return buildVNameNode(child);
        } else if ("CONST".equals(symb)) {
          return buildConstNode(child);
        }
      }
    }
    return null;
  }

  private static SyntaxTreeNode buildVNameNode(Node xmlNode) {
    String varName = getVName(xmlNode);
    if (varName != null) {
      return new AtomicNode(varName, 'v');
    } else {
      System.err.println("VNAME node without a variable name.");
      return null;
    }
  }

  private static SyntaxTreeNode buildConstNode(Node xmlNode) {
    String value = null;
    char type = 'u';

    NodeList children = getChildren(xmlNode);
    if (children != null) {
      for (int i = 0; i < children.getLength(); i++) {
        Node leaf = children.item(i);
        value = getTerminal(leaf);
        if (value != null) {
          if (value.matches("\".*\"")) {
            type = 't';
          } else if (value.matches("\\d+")) {
            type = 'n';
          }
          break;
        }
      }
    }
    return new AtomicNode(value, type);
  }

  private static SyntaxTreeNode buildBinOpNode(Node xmlNode) {
    String operator = getOperator(xmlNode);
    SyntaxTreeNode left = null;
    SyntaxTreeNode right = null;

    NodeList children = getChildren(xmlNode);
    List<SyntaxTreeNode> args = new ArrayList<>();

    if (children != null) {
      for (int i = 0; i < children.getLength(); i++) {
        Node child = children.item(i);
        String symb = getSymbol(child);
        if ("ATOMIC".equals(symb) || "ARG".equals(symb)) {
          SyntaxTreeNode argNode = buildSyntaxTree(child);
          if (argNode != null) {
            args.add(argNode);
          }
        }
      }
    }

    if (args.size() >= 2) {
      left = args.get(0);
      right = args.get(1);
      return new BinOpNode(operator, left, right);
    } else {
      System.err.println("Incomplete binary operation.");
      return null;
    }
  }

  private static SyntaxTreeNode buildFunctionsNode(Node xmlNode) {
    return null;
  }
}
