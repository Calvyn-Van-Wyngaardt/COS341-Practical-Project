import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;

public class RecSPLParser {
  private static final String INPUT_FILE = "output/lexerOutput.xml";
  private static final String OUTPUT_FILE = "output/parserOutput.xml";
  private static Map<String, Map<String, String>> parseTable;
  private static Stack<Object> stack = new Stack<>();
  private static List<Token> tokens = new ArrayList<>();
  private static int tokenIndex = 0;
  private static int unidCounter = 1;
  private static List<InnerNode> innerNodes = new ArrayList<>();
  private static List<LeafNode> leafNodes = new ArrayList<>();

  public static void main(String[] args) {
    try {
      parseTable = SLRParseTable.initializeParseTable();
      readTokens();
      parse();
    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private static void readTokens() throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.parse(new File(INPUT_FILE));

    NodeList tokenNodes = document.getElementsByTagName("TOK");
    for (int i = 0; i < tokenNodes.getLength(); i++) {
      Element tokenElement = (Element)tokenNodes.item(i);
      String id =
          tokenElement.getElementsByTagName("ID").item(0).getTextContent();
      String tokenClass =
          tokenElement.getElementsByTagName("CLASS").item(0).getTextContent();
      String word =
          tokenElement.getElementsByTagName("WORD").item(0).getTextContent();

      if (word.equals("< input")) {
        tokens.add(new Token(id, "KEYWORD", "input"));
      } else {
        tokens.add(new Token(id, tokenClass, word));
      }
    }
  }

  private static void parse() throws Exception {
    stack.push("0");

    while (true) {
      Token currentToken = tokens.get(tokenIndex);
      String state = (String)stack.peek();

      if (state.startsWith("g")) {
        state = state.substring(1);
      }

      Map<String, String> stateActions = parseTable.get(state);
      if (stateActions == null) {
        throw new Exception("No actions found for state " + state);
      }

      String action = null;

      if (currentToken.word.startsWith("V_")) {
        action = stateActions.get("V_");
      } else if (currentToken.word.startsWith("N_")) {
        action = stateActions.get("N_");
      } else if (currentToken.word.matches("-?[0-9]+(\\.[0-9]+)?")) {
        action = stateActions.get("N_");
      } else if (currentToken.word.startsWith("T_")) {
        action = stateActions.get("T_");
      } else if (currentToken.word.startsWith("\"") &&
                 currentToken.word.endsWith("\"")) {
        action = stateActions.get("T_");
      } else if (currentToken.word.startsWith("F_")) {
        action = stateActions.get("F_");
      } else if (currentToken.word.matches("F_[a-z][a-z0-9]*")) {
        action = stateActions.get("F_");
      }

      if (action == null) {
        action = stateActions.get(currentToken.word);
        if (action == null) {
          action = stateActions.get(currentToken.tokenClass);
        }
      }

      if (action == null) {
        throw new Exception(
            "Parsing error: No action found for token " + currentToken.word +
            " (class: " + currentToken.tokenClass + ") in state " + state);
      }

      if (action.startsWith("s")) {
        int nextState = Integer.parseInt(action.substring(1));
        LeafNode leafNode = new LeafNode(currentToken.word);
        leafNode.token = currentToken;
        leafNode.parent = null;
        leafNodes.add(leafNode);
        stack.push(leafNode);
        stack.push(String.valueOf(nextState));
        tokenIndex++;
      } else if (action.startsWith("r")) {
        reduce(Integer.parseInt(action.substring(1)));
      } else if (action.equals("acc")) {
        System.out.println("Parsing completed successfully");

        InnerNode root = null;
        for (Object obj : stack) {
          if (obj instanceof InnerNode) {
            root = (InnerNode)obj;
            break;
          }
        }

        if (root != null) {
          writeSyntaxTreeToXML(root, OUTPUT_FILE);
          System.out.println("Syntax tree written to " + OUTPUT_FILE);
        } else {
          System.err.println("Error: Syntax tree root is null.");
        }

        break;
      } else {
        throw new Exception("Invalid action in parse table: " + action);
      }
    }
  }

  private static class Token {
    @SuppressWarnings("unused") String id;
    String tokenClass;
    String word;

    Token(String id, String tokenClass, String word) {
      this.id = id;
      this.tokenClass = tokenClass;
      this.word = word;
    }
  }

  private static abstract class Node {
    int unid;
    Node parent;
    List<Node> children = new ArrayList<>();
  }

  private static class InnerNode extends Node {
    String symbol;

    InnerNode(String symbol) {
      this.symbol = symbol;
      this.unid = getNextUnid();
    }
  }

  private static class LeafNode extends Node {
    String terminal;
    @SuppressWarnings("unused") Token token;

    LeafNode(String terminal) {
      this.terminal = terminal;
      this.unid = getNextUnid();
    }
  }

  private static int getNextUnid() { return unidCounter++; }

  private static void reduce(int ruleNumber) throws Exception {
    String leftHandSide;
    int numSymbolsToPop;

    switch (ruleNumber) {
    case 1: // PROG -> main GLOBVARS ALGO FUNCIONS
      leftHandSide = "PROG";
      numSymbolsToPop = 4;
      break;
    case 2: // GLOBVARS -> ε (empty)
      leftHandSide = "GLOBVARS";
      numSymbolsToPop = 0;
      break;
    case 3: // GLOBVARS -> VTYP VNAME , GLOBVARS
      leftHandSide = "GLOBVARS";
      numSymbolsToPop = 4;
      break;
    case 4: // VTYP -> num
      leftHandSide = "VTYP";
      numSymbolsToPop = 1;
      break;
    case 5: // VTYP -> text
      leftHandSide = "VTYP";
      numSymbolsToPop = 1;
      break;
    case 6: // VNAME -> V
      leftHandSide = "VNAME";
      numSymbolsToPop = 1;
      break;
    case 7: // ALGO -> begin INSTRUC end
      leftHandSide = "ALGO";
      numSymbolsToPop = 3;
      break;
    case 8: // INSTRUC -> ε (empty)
      leftHandSide = "INSTRUC";
      numSymbolsToPop = 0;
      break;
    case 9: // INSTRUC -> COMMAND ; INSTRUC
      leftHandSide = "INSTRUC";
      numSymbolsToPop = 3;
      break;
    case 10: // COMMAND -> skip
      leftHandSide = "COMMAND";
      numSymbolsToPop = 1;
      break;
    case 11: // COMMAND -> halt
      leftHandSide = "COMMAND";
      numSymbolsToPop = 1;
      break;
    case 12: // COMMAND -> print ATOMIC
      leftHandSide = "COMMAND";
      numSymbolsToPop = 2;
      break;
    case 13: // COMMAND -> return ATOMIC
      leftHandSide = "COMMAND";
      numSymbolsToPop = 2;
      break;
    case 14: // COMMAND -> ASSIGN
      leftHandSide = "COMMAND";
      numSymbolsToPop = 1;
      break;
    case 15: // COMMAND -> CALL
      leftHandSide = "COMMAND";
      numSymbolsToPop = 1;
      break;
    case 16: // COMMAND -> BRANCH
      leftHandSide = "COMMAND";
      numSymbolsToPop = 1;
      break;
    case 17: // ATOMIC -> VNAME
      leftHandSide = "ATOMIC";
      numSymbolsToPop = 1;
      break;
    case 18: // ATOMIC -> CONST
      leftHandSide = "ATOMIC";
      numSymbolsToPop = 1;
      break;
    case 19: // CONST -> N
      leftHandSide = "CONST";
      numSymbolsToPop = 1;
      break;
    case 20: // CONST -> T
      leftHandSide = "CONST";
      numSymbolsToPop = 1;
      break;
    case 21: // ASSIGN -> VNAME < input
      leftHandSide = "ASSIGN";
      numSymbolsToPop = 2;
      break;
    case 22: // ASSIGN -> VNAME = TERM
      leftHandSide = "ASSIGN";
      numSymbolsToPop = 3;
      break;
    case 23: // CALL -> FNAME ( ATOMIC , ATOMIC , ATOMIC )
      leftHandSide = "CALL";
      numSymbolsToPop = 8;
      break;
    case 24: // BRANCH -> if COND then ALGO else ALGO
      leftHandSide = "BRANCH";
      numSymbolsToPop = 6;
      break;
    case 25: // TERM -> ATOMIC
      leftHandSide = "TERM";
      numSymbolsToPop = 1;
      break;
    case 26: // TERM -> CALL
      leftHandSide = "TERM";
      numSymbolsToPop = 1;
      break;
    case 27: // TERM -> OP
      leftHandSide = "TERM";
      numSymbolsToPop = 1;
      break;
    case 28: // OP -> UNOP ( ARG )
      leftHandSide = "OP";
      numSymbolsToPop = 4;
      break;
    case 29: // OP -> BINOP ( ARG , ARG )
      leftHandSide = "OP";
      numSymbolsToPop = 6;
      break;
    case 30: // ARG -> ATOMIC
      leftHandSide = "ARG";
      numSymbolsToPop = 1;
      break;
    case 31: // ARG -> OP
      leftHandSide = "ARG";
      numSymbolsToPop = 1;
      break;
    case 32: // COND -> SIMPLE
      leftHandSide = "COND";
      numSymbolsToPop = 1;
      break;
    case 33: // COND -> COMPOSIT
      leftHandSide = "COND";
      numSymbolsToPop = 1;
      break;
    case 34: // SIMPLE -> BINOP ( ATOMIC , ATOMIC )
      leftHandSide = "SIMPLE";
      numSymbolsToPop = 6;
      break;
    case 35: // COMPOSIT -> BINOP ( SIMPLE , SIMPLE )
      leftHandSide = "COMPOSIT";
      numSymbolsToPop = 6;
      break;
    case 36: // COMPOSIT -> UNOP ( SIMPLE )
      leftHandSide = "COMPOSIT";
      numSymbolsToPop = 4;
      break;
    case 37: // UNOP -> not
      leftHandSide = "UNOP";
      numSymbolsToPop = 1;
      break;
    case 38: // UNOP -> sqrt
      leftHandSide = "UNOP";
      numSymbolsToPop = 1;
      break;
    case 39: // BINOP -> or
      leftHandSide = "BINOP";
      numSymbolsToPop = 1;
      break;
    case 40: // BINOP -> and
      leftHandSide = "BINOP";
      numSymbolsToPop = 1;
      break;
    case 41: // BINOP -> eq
      leftHandSide = "BINOP";
      numSymbolsToPop = 1;
      break;
    case 42: // BINOP -> grt
      leftHandSide = "BINOP";
      numSymbolsToPop = 1;
      break;
    case 43: // BINOP -> add
      leftHandSide = "BINOP";
      numSymbolsToPop = 1;
      break;
    case 44: // BINOP -> sub
      leftHandSide = "BINOP";
      numSymbolsToPop = 1;
      break;
    case 45: // BINOP -> mul
      leftHandSide = "BINOP";
      numSymbolsToPop = 1;
      break;
    case 46: // BINOP -> div
      leftHandSide = "BINOP";
      numSymbolsToPop = 1;
      break;
    case 47: // FNAME -> F
      leftHandSide = "FNAME";
      numSymbolsToPop = 1;
      break;
    case 48: // FUNCTIONS -> ε (empty)
      leftHandSide = "FUNCTIONS";
      numSymbolsToPop = 0;
      break;
    case 49: // FUNCTIONS -> DECL FUNCTIONS
      leftHandSide = "FUNCTIONS";
      numSymbolsToPop = 2;
      break;
    case 50: // DECL -> HEADER BODY
      leftHandSide = "DECL";
      numSymbolsToPop = 2;
      break;
    case 51: // HEADER -> FTYP FNAME ( VNAME , VNAME , VNAME )
      leftHandSide = "HEADER";
      numSymbolsToPop = 9;
      break;
    case 52: // FTYP -> num
      leftHandSide = "FTYP";
      numSymbolsToPop = 1;
      break;
    case 53: // FTYP -> void
      leftHandSide = "FTYP";
      numSymbolsToPop = 1;
      break;
    case 54: // BODY -> PROLOG LOCVARS ALGO EPILOG SUBFUNCS end
      leftHandSide = "BODY";
      numSymbolsToPop = 6;
      break;
    case 55: // PROLOG -> {
      leftHandSide = "PROLOG";
      numSymbolsToPop = 1;
      break;
    case 56: // EPILOG -> }
      leftHandSide = "EPILOG";
      numSymbolsToPop = 1;
      break;
    case 57: // LOCVARS -> VTYP VNAME , VTYP VNAME , VTYP VNAME ,
      leftHandSide = "LOCVARS";
      numSymbolsToPop = 9;
      break;
    case 58: // SUBFUNCS -> FUNCTIONS
      leftHandSide = "SUBFUNCS";
      numSymbolsToPop = 1;
      break;
    default:
      throw new Exception("Invalid rule number: " + ruleNumber);
    }

    int itemsToPop = numSymbolsToPop * 2;
    List<Node> children = new ArrayList<>();

    for (int i = 0; i < itemsToPop; i += 2) {
      stack.pop();
      Object nodeObj = stack.pop();
      if (nodeObj instanceof Node) {
        Node node = (Node)nodeObj;
        children.add(0, node);
      } else {
        throw new Exception("Unexpected object on stack: " + nodeObj);
      }
    }

    InnerNode innerNode = new InnerNode(leftHandSide);
    for (Node child : children) {
      child.parent = innerNode;
      innerNode.children.add(child);
    }
    innerNodes.add(innerNode);

    String state = (String)stack.peek();
    stack.push(innerNode);

    Map<String, String> stateActions = parseTable.get(state);
    if (stateActions == null) {
      throw new Exception("No actions found for state " + state);
    }

    String nextState = stateActions.get(leftHandSide);
    if (nextState == null) {
      throw new Exception("No valid transition for " + leftHandSide +
                          " in state " + state);
    }

    stack.push(nextState);
  }

  private static void writeSyntaxTreeToXML(InnerNode root,
                                           String outputFilePath)
      throws Exception {
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    Document xmlDocument = docBuilder.newDocument();

    Element rootElement = xmlDocument.createElement("SYNTREE");
    xmlDocument.appendChild(rootElement);

    Element rootNode = xmlDocument.createElement("ROOT");
    rootElement.appendChild(rootNode);

    Element unidElement = xmlDocument.createElement("UNID");
    unidElement.appendChild(
        xmlDocument.createTextNode(String.valueOf(root.unid)));
    rootNode.appendChild(unidElement);

    Element symbElement = xmlDocument.createElement("SYMB");
    symbElement.appendChild(xmlDocument.createTextNode(root.symbol));
    rootNode.appendChild(symbElement);

    Element childrenElement = xmlDocument.createElement("CHILDREN");
    rootNode.appendChild(childrenElement);
    for (Node child : root.children) {
      Element idElement = xmlDocument.createElement("ID");
      idElement.appendChild(
          xmlDocument.createTextNode(String.valueOf(child.unid)));
      childrenElement.appendChild(idElement);
    }

    Element innerNodesElement = xmlDocument.createElement("INNERNODES");
    rootElement.appendChild(innerNodesElement);
    for (InnerNode inNode : innerNodes) {
      if (inNode != root) {
        Element inElement = xmlDocument.createElement("IN");
        innerNodesElement.appendChild(inElement);

        Element inUnidElement = xmlDocument.createElement("UNID");
        inUnidElement.appendChild(
            xmlDocument.createTextNode(String.valueOf(inNode.unid)));
        inElement.appendChild(inUnidElement);

        Element inSymbElement = xmlDocument.createElement("SYMB");
        inSymbElement.appendChild(xmlDocument.createTextNode(inNode.symbol));
        inElement.appendChild(inSymbElement);

        Element parentElement = xmlDocument.createElement("PARENT");
        parentElement.appendChild(
            xmlDocument.createTextNode(String.valueOf(inNode.parent.unid)));
        inElement.appendChild(parentElement);

        Element inChildrenElement = xmlDocument.createElement("CHILDREN");
        inElement.appendChild(inChildrenElement);
        for (Node child : inNode.children) {
          Element idElement = xmlDocument.createElement("ID");
          idElement.appendChild(
              xmlDocument.createTextNode(String.valueOf(child.unid)));
          inChildrenElement.appendChild(idElement);
        }
      }
    }

    Element leafNodesElement = xmlDocument.createElement("LEAFNODES");
    rootElement.appendChild(leafNodesElement);
    for (LeafNode leafNode : leafNodes) {
      Element leafElement = xmlDocument.createElement("LEAF");
      leafNodesElement.appendChild(leafElement);

      Element parentElement = xmlDocument.createElement("PARENT");
      parentElement.appendChild(
          xmlDocument.createTextNode(String.valueOf(leafNode.parent.unid)));
      leafElement.appendChild(parentElement);

      Element unidLeafElement = xmlDocument.createElement("UNID");
      unidLeafElement.appendChild(
          xmlDocument.createTextNode(String.valueOf(leafNode.unid)));
      leafElement.appendChild(unidLeafElement);

      Element terminalElement = xmlDocument.createElement("TERMINAL");
      terminalElement.appendChild(
          xmlDocument.createTextNode(leafNode.terminal));
      leafElement.appendChild(terminalElement);
    }

    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
                                  "2");
    DOMSource source = new DOMSource(xmlDocument);
    StreamResult result = new StreamResult(new File(outputFilePath));
    transformer.transform(source, result);
  }
}
