import java.io.File;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class TypeChecker {
  private static final String INPUT_FILE = "./output/parserOutput.xml";
  private static Map<Integer, Node> nodeMap = new HashMap<>();
  private static InnerNode root;
  private static Map<String, String> globalVariables = new HashMap<>();
  private static Map<String, FunctionInfo> functions = new HashMap<>();
  private static Document doc; // Make doc a class-level variable

  public static void main(String[] args) {
    try {
      parseSyntaxTree();
      collectFunctionDeclarations();
      typeCheck();
      System.out.println("Type checking completed successfully.");
    } catch (TypeCheckingException e) {
      System.err.println("Type checking error: " + e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // Node classes
  private static abstract class Node {
    int unid;
    Node parent;
    List<Node> children = new ArrayList<>();
  }

  private static class InnerNode extends Node {
    String symbol;

    InnerNode(String symbol, int unid) {
      this.symbol = symbol;
      this.unid = unid;
    }
  }

  private static class LeafNode extends Node {
    String terminal;

    LeafNode(String terminal, int unid) {
      this.terminal = terminal;
      this.unid = unid;
    }
  }

  // Function information
  private static class FunctionInfo {
    String returnType;
    String functionName;
    List<String> paramNames;
    List<String> paramTypes;
    Map<String, String> localVariables;

    FunctionInfo(String returnType, String functionName,
                 List<String> paramNames, List<String> paramTypes) {
      this.returnType = returnType;
      this.functionName = functionName;
      this.paramNames = paramNames;
      this.paramTypes = paramTypes;
      this.localVariables = new HashMap<>();
    }
  }

  // Exception class for type checking errors
  private static class TypeCheckingException extends Exception {
    public TypeCheckingException(String message) { super(message); }
  }

  private static void parseSyntaxTree() throws Exception {
    File inputFile = new File(INPUT_FILE);
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    doc = dBuilder.parse(inputFile);
    doc.getDocumentElement().normalize();

    // First, read all nodes and store them in a map by UNID
    // Read ROOT node
    NodeList rootList = doc.getElementsByTagName("ROOT");
    if (rootList.getLength() != 1) {
      throw new Exception("Invalid syntax tree: ROOT node missing or "
                          + "multiple ROOT nodes found.");
    }

    Element rootElement = (Element)rootList.item(0);
    root = parseInnerNodeElement(rootElement);
    nodeMap.put(root.unid, root);

    // Read all INNERNODES
    NodeList inList = doc.getElementsByTagName("IN");
    for (int i = 0; i < inList.getLength(); i++) {
      Element inElement = (Element)inList.item(i);
      InnerNode inNode = parseInnerNodeElement(inElement);
      nodeMap.put(inNode.unid, inNode);
    }

    // Read all LEAFNODES
    NodeList leafList = doc.getElementsByTagName("LEAF");
    for (int i = 0; i < leafList.getLength(); i++) {
      Element leafElement = (Element)leafList.item(i);
      LeafNode leafNode = parseLeafNode(leafElement);
      nodeMap.put(leafNode.unid, leafNode);
    }

    // After all nodes are in the map, link the children
    linkChildren();
  }

  private static InnerNode parseInnerNodeElement(Element element) {
    int unid = Integer.parseInt(
        element.getElementsByTagName("UNID").item(0).getTextContent());
    String symbol =
        element.getElementsByTagName("SYMB").item(0).getTextContent();

    InnerNode node = new InnerNode(symbol, unid);
    // Parent and children will be linked later
    return node;
  }

  private static LeafNode parseLeafNode(Element leafElement) {
    int unid = Integer.parseInt(
        leafElement.getElementsByTagName("UNID").item(0).getTextContent());
    String terminal =
        leafElement.getElementsByTagName("TERMINAL").item(0).getTextContent();
    // Parent will be linked later
    return new LeafNode(terminal, unid);
  }

  private static void linkChildren() {
    // Link children for ROOT node
    linkChildrenForNode(root);

    // Link children for INNERNODES
    for (Node node : nodeMap.values()) {
      if (node instanceof InnerNode) {
        if (node != root) {
          linkChildrenForNode((InnerNode)node);
        }
      }
    }
  }

  private static void linkChildrenForNode(InnerNode node) {
    NodeList childrenIDs = getChildrenIDs(node.unid);
    if (childrenIDs != null) {
      for (int i = 0; i < childrenIDs.getLength(); i++) {
        int childUnid = Integer.parseInt(childrenIDs.item(i).getTextContent());
        Node childNode = nodeMap.get(childUnid);
        if (childNode != null) {
          node.children.add(childNode);
          childNode.parent = node;
        }
      }
    }
  }

  private static NodeList getChildrenIDs(int unid) {
    // Find the node element with this unid
    Element nodeElement = findNodeElementByUnid(unid);
    if (nodeElement != null) {
      NodeList childrenList = nodeElement.getElementsByTagName("CHILDREN");
      if (childrenList.getLength() > 0) {
        Element childrenElement = (Element)childrenList.item(0);
        return childrenElement.getElementsByTagName("ID");
      }
    }
    return null;
  }

  private static Element findNodeElementByUnid(int unid) {
    // Search in ROOT
    NodeList rootList = doc.getElementsByTagName("ROOT");
    if (rootList.getLength() == 1) {
      Element rootElement = (Element)rootList.item(0);
      int rootUnid = Integer.parseInt(
          rootElement.getElementsByTagName("UNID").item(0).getTextContent());
      if (rootUnid == unid) {
        return rootElement;
      }
    }

    // Search in INNERNODES
    NodeList inList = doc.getElementsByTagName("IN");
    for (int i = 0; i < inList.getLength(); i++) {
      Element inElement = (Element)inList.item(i);
      int nodeUnid = Integer.parseInt(
          inElement.getElementsByTagName("UNID").item(0).getTextContent());
      if (nodeUnid == unid) {
        return inElement;
      }
    }

    // Search in LEAFNODES
    NodeList leafList = doc.getElementsByTagName("LEAF");
    for (int i = 0; i < leafList.getLength(); i++) {
      Element leafElement = (Element)leafList.item(i);
      int nodeUnid = Integer.parseInt(
          leafElement.getElementsByTagName("UNID").item(0).getTextContent());
      if (nodeUnid == unid) {
        return leafElement;
      }
    }

    return null;
  }

  // New method to collect all function declarations before type checking
  private static void collectFunctionDeclarations()
      throws TypeCheckingException {
    collectFunctionDeclarations(root);
  }

  private static void collectFunctionDeclarations(Node node)
      throws TypeCheckingException {
    if (node instanceof InnerNode) {
      InnerNode inNode = (InnerNode)node;
      if (inNode.symbol.equals("DECL")) {
        // Process function declaration
        processFunctionDeclaration(inNode);
      } else {
        // Recursively traverse children
        for (Node child : inNode.children) {
          collectFunctionDeclarations(child);
        }
      }
    }
    // Leaf nodes do not contain function declarations
  }

  private static void processFunctionDeclaration(InnerNode declNode)
      throws TypeCheckingException {
    // DECL -> HEADER BODY
    InnerNode headerNode = null;
    for (Node child : declNode.children) {
      if (child instanceof InnerNode) {
        InnerNode childNode = (InnerNode)child;
        if (childNode.symbol.equals("HEADER")) {
          headerNode = childNode;
          break;
        }
      }
    }

    if (headerNode == null) {
      throw new TypeCheckingException("Function declaration missing HEADER.");
    }

    FunctionInfo functionInfo = parseFunctionHeader(headerNode);

    if (functions.containsKey(functionInfo.functionName)) {
      throw new TypeCheckingException("Function '" + functionInfo.functionName +
                                      "' already declared.");
    }

    functions.put(functionInfo.functionName, functionInfo);

    // No need to type check the body here; it will be checked during type
    // checking
  }

  private static FunctionInfo parseFunctionHeader(InnerNode headerNode)
      throws TypeCheckingException {
    // HEADER -> FTYP FNAME( VNAME , VNAME , VNAME )
    String returnType = null;
    String functionName = null;
    List<String> paramNames = new ArrayList<>();
    List<String> paramTypes = new ArrayList<>();

    for (int i = 0; i < headerNode.children.size(); i++) {
      Node child = headerNode.children.get(i);
      if (child instanceof InnerNode) {
        InnerNode childNode = (InnerNode)child;
        switch (childNode.symbol) {
        case "FTYP":
          returnType = getTypeFromFTYP(childNode);
          break;
        case "FNAME":
          functionName = getFunctionNameFromFNAME(childNode);
          break;
        case "VNAME":
          String paramName = getVarNameFromVNAME(childNode);
          paramNames.add(paramName);
          // Assume parameter types are specified elsewhere; for simplicity,
          // we'll assume 'num' You may need to adjust this based on your actual
          // grammar
          paramTypes.add("num");
          break;
        }
      }
    }

    if (returnType == null || functionName == null || paramNames.size() != 3) {
      throw new TypeCheckingException("Invalid function header.");
    }

    FunctionInfo functionInfo =
        new FunctionInfo(returnType, functionName, paramNames, paramTypes);
    // Add parameters to the function's local variables
    for (int i = 0; i < paramNames.size(); i++) {
      functionInfo.localVariables.put(paramNames.get(i), paramTypes.get(i));
    }
    return functionInfo;
  }

  private static void typeCheck() throws TypeCheckingException {
    // Start type checking from the root node
    typeCheckNode(root, null);
  }

  private static void typeCheckNode(Node node, FunctionInfo currentFunction)
      throws TypeCheckingException {
    if (node instanceof InnerNode) {
      InnerNode inNode = (InnerNode)node;
      switch (inNode.symbol) {
      case "PROG":
        // Type check global variables and functions
        for (Node child : inNode.children) {
          typeCheckNode(child, null);
        }
        break;
      case "GLOBVARS":
        // Type check global variable declarations
        typeCheckGlobalVars(inNode);
        break;
      case "FUNCTIONS":
        // Type check function bodies
        for (Node child : inNode.children) {
          if (child instanceof InnerNode &&
              ((InnerNode)child).symbol.equals("DECL")) {
            typeCheckFunctionBody((InnerNode)child, null);
          } else {
            typeCheckNode(child, null);
          }
        }
        break;
      case "DECL":
        // Type check function body
        typeCheckFunctionBody(inNode, null);
        break;
      case "ALGO":
        // Type check algorithm (instructions)
        for (Node child : inNode.children) {
          typeCheckNode(child, currentFunction);
        }
        break;
      case "INSTRUC":
        // Type check instructions
        for (Node child : inNode.children) {
          typeCheckNode(child, currentFunction);
        }
        break;
      case "COMMAND":
        // Type check commands
        typeCheckCommand(inNode, currentFunction);
        break;
      // Add cases for other non-terminals as needed
      default:
        for (Node child : inNode.children) {
          typeCheckNode(child, currentFunction);
        }
        break;
      }
    } else if (node instanceof LeafNode) {
      // Leaf nodes do not require type checking by themselves
    }
  }

  private static void typeCheckGlobalVars(InnerNode inNode)
      throws TypeCheckingException {
    // Expected structure: VTYP VNAME , GLOBVARS
    String type = null;
    String varName = null;

    for (Node child : inNode.children) {
      if (child instanceof InnerNode) {
        InnerNode childNode = (InnerNode)child;
        if (childNode.symbol.equals("VTYP")) {
          type = getTypeFromVTYP(childNode);
        } else if (childNode.symbol.equals("VNAME")) {
          varName = getVarNameFromVNAME(childNode);
        } else if (childNode.symbol.equals("GLOBVARS")) {
          // Recursive call for further global variables
          typeCheckGlobalVars(childNode);
        }
      }
    }

    if (type != null && varName != null) {
      if (globalVariables.containsKey(varName)) {
        throw new TypeCheckingException("Variable '" + varName +
                                        "' already declared.");
      }
      globalVariables.put(varName, type);
    }
  }

  private static String getTypeFromVTYP(InnerNode vtypNode)
      throws TypeCheckingException {
    // VTYP -> num | text
    for (Node child : vtypNode.children) {
      if (child instanceof LeafNode) {
        LeafNode leaf = (LeafNode)child;
        if (leaf.terminal.equals("num") || leaf.terminal.equals("text")) {
          return leaf.terminal;
        } else {
          throw new TypeCheckingException("Invalid type in VTYP: " +
                                          leaf.terminal);
        }
      }
    }
    throw new TypeCheckingException("VTYP does not contain a valid type.");
  }

  private static String getVarNameFromVNAME(InnerNode vnameNode)
      throws TypeCheckingException {
    // VNAME -> V
    for (Node child : vnameNode.children) {
      if (child instanceof LeafNode) {
        LeafNode leaf = (LeafNode)child;
        if (leaf.terminal.startsWith("V_")) {
          return leaf.terminal;
        } else {
          throw new TypeCheckingException("Invalid variable name: " +
                                          leaf.terminal);
        }
      }
    }
    throw new TypeCheckingException(
        "VNAME does not contain a valid variable name.");
  }

  private static void typeCheckFunctionBody(InnerNode declNode,
                                            FunctionInfo dummy)
      throws TypeCheckingException {
    // DECL -> HEADER BODY
    // We already processed HEADER during function collection
    // Now, we need to type check the BODY
    InnerNode bodyNode = null;
    FunctionInfo functionInfo = null;

    for (Node child : declNode.children) {
      if (child instanceof InnerNode) {
        InnerNode childNode = (InnerNode)child;
        if (childNode.symbol.equals("BODY")) {
          bodyNode = childNode;
        } else if (childNode.symbol.equals("HEADER")) {
          // Retrieve function info from functions map
          String functionName = getFunctionNameFromHeader(childNode);
          functionInfo = functions.get(functionName);
          if (functionInfo == null) {
            throw new TypeCheckingException("Function '" + functionName +
                                            "' not found in functions map.");
          }
        }
      }
    }

    if (bodyNode != null && functionInfo != null) {
      typeCheckFunctionBodyContent(bodyNode, functionInfo);
    } else {
      throw new TypeCheckingException(
          "Function declaration missing BODY or HEADER.");
    }
  }

  private static String getFunctionNameFromHeader(InnerNode headerNode)
      throws TypeCheckingException {
    // Extract function name from HEADER node
    for (Node child : headerNode.children) {
      if (child instanceof InnerNode) {
        InnerNode childNode = (InnerNode)child;
        if (childNode.symbol.equals("FNAME")) {
          return getFunctionNameFromFNAME(childNode);
        }
      }
    }
    throw new TypeCheckingException("Function name not found in HEADER.");
  }

  private static void typeCheckFunctionBodyContent(InnerNode bodyNode,
                                                   FunctionInfo functionInfo)
      throws TypeCheckingException {
    // BODY -> PROLOG LOCVARS ALGO EPILOG SUBFUNCS end
    for (Node child : bodyNode.children) {
      if (child instanceof InnerNode) {
        InnerNode childNode = (InnerNode)child;
        switch (childNode.symbol) {
        case "LOCVARS":
          typeCheckLocalVars(childNode, functionInfo);
          break;
        case "ALGO":
          typeCheckNode(childNode, functionInfo);
          break;
          // Handle other parts as needed
        }
      }
    }
  }

  private static void typeCheckLocalVars(InnerNode locvarsNode,
                                         FunctionInfo functionInfo)
      throws TypeCheckingException {
    // LOCVARS ::= VTYP VNAME , VTYP VNAME , VTYP VNAME ,
    List<Node> children = locvarsNode.children;
    for (int i = 0; i < children.size(); i++) {
      Node child = children.get(i);
      if (child instanceof InnerNode) {
        InnerNode childNode = (InnerNode)child;
        if (childNode.symbol.equals("VTYP")) {
          String type = getTypeFromVTYP(childNode);
          i++;
          if (i < children.size()) {
            Node vnameNode = children.get(i);
            if (vnameNode instanceof InnerNode &&
                ((InnerNode)vnameNode).symbol.equals("VNAME")) {
              String varName = getVarNameFromVNAME((InnerNode)vnameNode);
              if (functionInfo.localVariables.containsKey(varName)) {
                throw new TypeCheckingException(
                    "Variable '" + varName + "' already declared in function.");
              }
              functionInfo.localVariables.put(varName, type);
            }
          }
        }
      }
    }
  }

  private static void typeCheckCommand(InnerNode commandNode,
                                       FunctionInfo functionInfo)
      throws TypeCheckingException {
    // COMMAND ::= skip | halt | print ATOMIC | ASSIGN | CALL | BRANCH
    if (commandNode.children.isEmpty()) {
      return;
    }

    Node firstChild = commandNode.children.get(0);
    if (firstChild instanceof LeafNode) {
      LeafNode leaf = (LeafNode)firstChild;
      if (leaf.terminal.equals("skip") || leaf.terminal.equals("halt")) {
        // No type checking needed
        return;
      } else if (leaf.terminal.equals("print")) {
        // Type check print command
        typeCheckPrintCommand(commandNode, functionInfo);
      } else {
        // Handle other leaf commands if necessary
      }
    } else if (firstChild instanceof InnerNode) {
      InnerNode childNode = (InnerNode)firstChild;
      switch (childNode.symbol) {
      case "ASSIGN":
        // Type check assignment
        typeCheckAssignment(childNode, functionInfo);
        break;
      case "CALL":
        // Type check function call
        evaluateFunctionCall(childNode, functionInfo);
        break;
      case "BRANCH":
        // Type check branch (if-else)
        typeCheckBranch(childNode, functionInfo);
        break;
        // Handle other commands as needed
      }
    }
  }

  // Updated evaluateFunctionCall method
  private static String evaluateFunctionCall(InnerNode callNode,
                                             FunctionInfo currentFunction)
      throws TypeCheckingException {
    // CALL ::= FNAME( ATOMIC , ATOMIC , ATOMIC )
    String functionName = null;
    List<String> argTypes = new ArrayList<>();

    for (Node child : callNode.children) {
      if (child instanceof InnerNode) {
        InnerNode childNode = (InnerNode)child;
        if (childNode.symbol.equals("FNAME")) {
          functionName = getFunctionNameFromFNAME(childNode);
        } else if (childNode.symbol.equals("ATOMIC")) {
          String argType = evaluateAtomic(childNode, currentFunction);
          argTypes.add(argType);
        }
      }
    }

    if (functionName == null) {
      throw new TypeCheckingException("Function call missing function name.");
    }

    FunctionInfo calledFunction = functions.get(functionName);
    if (calledFunction == null) {
      // Function not declared; throw an error
      throw new TypeCheckingException("Undeclared function: " + functionName);
    }

    // Check argument types
    if (argTypes.size() != calledFunction.paramTypes.size()) {
      throw new TypeCheckingException(
          "Argument count mismatch in function call to '" + functionName + "'");
    }

    for (int i = 0; i < argTypes.size(); i++) {
      if (!argTypes.get(i).equals(calledFunction.paramTypes.get(i))) {
        throw new TypeCheckingException("Type mismatch in argument " + (i + 1) +
                                        " of function '" + functionName +
                                        "'");
      }
    }

    return calledFunction.returnType;
  }

  private static String getTypeFromFTYP(InnerNode ftypNode)
      throws TypeCheckingException {
    // FTYP -> num | void
    for (Node child : ftypNode.children) {
      if (child instanceof LeafNode) {
        LeafNode leaf = (LeafNode)child;
        if (leaf.terminal.equals("num") || leaf.terminal.equals("void")) {
          return leaf.terminal;
        } else {
          throw new TypeCheckingException("Invalid function return type: " +
                                          leaf.terminal);
        }
      }
    }
    throw new TypeCheckingException("FTYP does not contain a valid type.");
  }

  private static String getFunctionNameFromFNAME(InnerNode fnameNode)
      throws TypeCheckingException {
    // FNAME -> F
    for (Node child : fnameNode.children) {
      if (child instanceof LeafNode) {
        LeafNode leaf = (LeafNode)child;
        if (leaf.terminal.startsWith("F_")) {
          return leaf.terminal;
        } else {
          throw new TypeCheckingException("Invalid function name: " +
                                          leaf.terminal);
        }
      }
    }
    throw new TypeCheckingException(
        "FNAME does not contain a valid function name.");
  }

  // The rest of the methods remain mostly unchanged, but with careful checks
  // to ensure variable scoping and types are correctly handled

  private static void typeCheckPrintCommand(InnerNode printNode,
                                            FunctionInfo functionInfo)
      throws TypeCheckingException {
    // print ATOMIC
    Node atomicNode = printNode.children.get(1);
    String type = evaluateAtomic((InnerNode)atomicNode, functionInfo);
    // For print command, any type is acceptable
  }

  private static void typeCheckAssignment(InnerNode assignNode,
                                          FunctionInfo functionInfo)
      throws TypeCheckingException {
    // ASSIGN ::= VNAME < input | VNAME = TERM
    String varName = null;
    String varType = null;

    Node vnameNode = assignNode.children.get(0);
    varName = getVarNameFromVNAME((InnerNode)vnameNode);
    varType = getVariableType(varName, functionInfo);

    if (assignNode.children.size() == 2) {
      // VNAME < input
      // For simplicity, let's assume input is always of type num
      return;
    } else if (assignNode.children.size() == 3) {
      // VNAME = TERM
      Node termNode = assignNode.children.get(2);
      String termType = evaluateTerm((InnerNode)termNode, functionInfo);

      if (!varType.equals(termType)) {
        throw new TypeCheckingException("Type mismatch: cannot assign '" +
                                        termType + "' to variable '" + varName +
                                        "' of type '" + varType + "'");
      }
    }
  }

  private static String getVariableType(String varName,
                                        FunctionInfo functionInfo)
      throws TypeCheckingException {
    if (functionInfo != null) {
      // Check local variables and parameters
      if (functionInfo.localVariables.containsKey(varName)) {
        return functionInfo.localVariables.get(varName);
      } else if (functionInfo.paramNames.contains(varName)) {
        int index = functionInfo.paramNames.indexOf(varName);
        return functionInfo.paramTypes.get(index);
      }
    }
    if (globalVariables.containsKey(varName)) {
      return globalVariables.get(varName);
    } else {
      throw new TypeCheckingException("Undeclared variable: " + varName);
    }
  }

  private static String evaluateTerm(InnerNode termNode,
                                     FunctionInfo functionInfo)
      throws TypeCheckingException {
    // TERM ::= ATOMIC | CALL | OP
    Node child = termNode.children.get(0);
    if (child instanceof InnerNode) {
      InnerNode childNode = (InnerNode)child;
      switch (childNode.symbol) {
      case "ATOMIC":
        return evaluateAtomic(childNode, functionInfo);
      case "CALL":
        return evaluateFunctionCall(childNode, functionInfo);
      case "OP":
        return evaluateOperation(childNode, functionInfo);
      }
    }
    throw new TypeCheckingException("Invalid TERM node.");
  }

  private static String evaluateAtomic(InnerNode atomicNode,
                                       FunctionInfo functionInfo)
      throws TypeCheckingException {
    // ATOMIC ::= VNAME | CONST
    Node child = atomicNode.children.get(0);
    if (child instanceof InnerNode) {
      InnerNode childNode = (InnerNode)child;
      if (childNode.symbol.equals("VNAME")) {
        String varName = getVarNameFromVNAME(childNode);
        return getVariableType(varName, functionInfo);
      } else if (childNode.symbol.equals("CONST")) {
        return evaluateConst(childNode);
      }
    }
    throw new TypeCheckingException("Invalid ATOMIC node.");
  }

  private static String evaluateConst(InnerNode constNode)
      throws TypeCheckingException {
    // CONST ::= N | T
    Node child = constNode.children.get(0);
    if (child instanceof LeafNode) {
      LeafNode leaf = (LeafNode)child;
      if (leaf.terminal.matches("-?[0-9]+(\\.[0-9]+)?")) {
        return "num";
      } else if (leaf.terminal.matches("\"[A-Za-z]{1,8}\"")) {
        return "text";
      } else {
        throw new TypeCheckingException("Invalid CONST value: " +
                                        leaf.terminal);
      }
    }
    throw new TypeCheckingException("Invalid CONST node.");
  }

  // Other methods like evaluateOperation, typeCheckBranch, etc., remain
  // unchanged Ensure that in these methods, variable types are correctly
  // determined and operations are type-checked according to your language's
  // rules

  // For example, in evaluateOperation:
  private static String evaluateOperation(InnerNode opNode,
                                          FunctionInfo functionInfo)
      throws TypeCheckingException {
    // OP ::= UNOP( ARG ) | BINOP( ARG , ARG )
    Node operatorNode = opNode.children.get(0);

    String operator = null;

    if (operatorNode instanceof InnerNode) {
      InnerNode operatorInnerNode = (InnerNode)operatorNode;
      if (operatorInnerNode.symbol.equals("BINOP") ||
          operatorInnerNode.symbol.equals("UNOP")) {
        // Get the actual operator from the BINOP or UNOP node
        operator = getOperator(operatorInnerNode);
      } else {
        throw new TypeCheckingException("Unexpected operator node: " +
                                        operatorInnerNode.symbol);
      }
    } else if (operatorNode instanceof LeafNode) {
      operator = ((LeafNode)operatorNode).terminal;
    }

    if (operator == null) {
      throw new TypeCheckingException("Operator not found in OP node.");
    }

    if (operator.equals("not") || operator.equals("sqrt")) {
      // UNOP
      Node argNode = opNode.children.get(2); // UNOP ( ARG )
      String argType = evaluateArg((InnerNode)argNode, functionInfo);
      return typeCheckUnop(operator, argType);
    } else {
      // BINOP
      Node argNode1 = opNode.children.get(2); // BINOP ( ARG , ARG )
      Node argNode2 = opNode.children.get(4);

      String argType1 = evaluateArg((InnerNode)argNode1, functionInfo);
      String argType2 = evaluateArg((InnerNode)argNode2, functionInfo);
      return typeCheckBinop(operator, argType1, argType2);
    }
  }

  private static String getOperator(InnerNode opNode)
      throws TypeCheckingException {
    // opNode is either BINOP or UNOP
    // The actual operator is expected to be a child of this node
    for (Node child : opNode.children) {
      if (child instanceof LeafNode) {
        LeafNode leaf = (LeafNode)child;
        // The terminal should be the actual operator (e.g., "add", "sub")
        return leaf.terminal;
      } else if (child instanceof InnerNode) {
        InnerNode innerChild = (InnerNode)child;
        // Depending on your grammar, you might need to go deeper
        // For now, assume operator is directly under BINOP/UNOP
        return getOperator(innerChild);
      }
    }
    throw new TypeCheckingException("Operator not found in node.");
  }

  private static String evaluateArg(InnerNode argNode,
                                    FunctionInfo functionInfo)
      throws TypeCheckingException {
    // ARG ::= ATOMIC | OP | ARG | SIMPLE

    Node child = argNode.children.get(0);

    if (child instanceof InnerNode) {
      InnerNode childNode = (InnerNode)child;
      switch (childNode.symbol) {
      case "ATOMIC":
        return evaluateAtomic(childNode, functionInfo);
      case "OP":
        return evaluateOperation(childNode, functionInfo);
      case "ARG":
        // Recursively evaluate the nested ARG
        return evaluateArg(childNode, functionInfo);
      case "SIMPLE":
        // Evaluate the SIMPLE condition
        return evaluateSimpleCondition(childNode, functionInfo);
      case "COMPOSIT":
        // Evaluate the COMPOSIT condition
        return evaluateCompositeCondition(childNode, functionInfo);
      default:
        throw new TypeCheckingException("Invalid symbol in ARG node: " +
                                        childNode.symbol);
      }
    } else if (child instanceof LeafNode) {
      // Handle leaf nodes if necessary
      throw new TypeCheckingException("Unexpected leaf node in ARG.");
    }

    throw new TypeCheckingException("Invalid ARG node.");
  }

  private static String evaluateSimpleCondition(InnerNode simpleNode,
                                                FunctionInfo functionInfo)
      throws TypeCheckingException {
    // SIMPLE ::= BINOP( ATOMIC , ATOMIC ) | BINOP( SIMPLE , SIMPLE ) | UNOP(
    // SIMPLE )
    return evaluateOperation(simpleNode, functionInfo);
  }

  private static String evaluateCompositeCondition(InnerNode compositNode,
                                                   FunctionInfo functionInfo)
      throws TypeCheckingException {
    // COMPOSIT ::= BINOP( SIMPLE , SIMPLE ) | UNOP( SIMPLE )
    return evaluateOperation(compositNode, functionInfo);
  }

  private static String typeCheckUnop(String operator, String argType)
      throws TypeCheckingException {
    switch (operator) {
    case "not":
      if (argType.equals("num")) {
        return "num";
      } else {
        throw new TypeCheckingException("Operator 'not' requires 'num' type.");
      }
    case "sqrt":
      if (argType.equals("num")) {
        return "num";
      } else {
        throw new TypeCheckingException("Operator 'sqrt' requires 'num' type.");
      }
    default:
      throw new TypeCheckingException("Unknown unary operator: " + operator);
    }
  }

  private static String typeCheckBinop(String operator, String argType1,
                                       String argType2)
      throws TypeCheckingException {
    switch (operator) {
    case "or":
    case "and":
      if (argType1.equals("num") && argType2.equals("num")) {
        return "num";
      } else {
        throw new TypeCheckingException("Operator '" + operator +
                                        "' requires 'num' type arguments.");
      }
    case "eq":
    case "grt":
      if (argType1.equals(argType2)) {
        return "num"; // Assuming comparison returns num (e.g., boolean as num)
      } else {
        throw new TypeCheckingException(
            "Operator '" + operator + "' requires arguments of the same type.");
      }
    case "add":
    case "sub":
    case "mul":
    case "div":
      if (argType1.equals("num") && argType2.equals("num")) {
        return "num";
      } else {
        throw new TypeCheckingException("Operator '" + operator +
                                        "' requires 'num' type arguments.");
      }
    default:
      throw new TypeCheckingException("Unknown binary operator: " + operator);
    }
  }

  private static void typeCheckBranch(InnerNode branchNode,
                                      FunctionInfo functionInfo)
      throws TypeCheckingException {
    // BRANCH ::= if COND then ALGO else ALGO
    Node condNode = branchNode.children.get(1);
    String condType = evaluateCondition((InnerNode)condNode, functionInfo);
    if (!condType.equals("num")) {
      throw new TypeCheckingException(
          "Condition in if-statement must be of type 'num'.");
    }

    // Type check ALGO nodes (then and else blocks)
    Node thenAlgoNode = branchNode.children.get(3);
    Node elseAlgoNode = branchNode.children.get(5);
    typeCheckNode(thenAlgoNode, functionInfo);
    typeCheckNode(elseAlgoNode, functionInfo);
  }

  private static String evaluateCondition(InnerNode condNode,
                                          FunctionInfo functionInfo)
      throws TypeCheckingException {
    // COND ::= SIMPLE | COMPOSIT
    Node child = condNode.children.get(0);
    if (child instanceof InnerNode) {
      InnerNode childNode = (InnerNode)child;
      switch (childNode.symbol) {
      case "SIMPLE":
        return evaluateSimpleCondition(childNode, functionInfo);
      case "COMPOSIT":
        return evaluateCompositeCondition(childNode, functionInfo);
      }
    }
    throw new TypeCheckingException("Invalid COND node.");
  }
}