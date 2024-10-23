import java.io.File;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

class Symbol {
  String name;
  String type;
  int id;
  int scopeLevel;
  int unid;

  public Symbol(String name, String type, int id, int scopeLevel, int unid) {
    this.name = name;
    this.type = type;
    this.id = id;
    this.scopeLevel = scopeLevel;
    this.unid = unid;
  }

  @Override
  public String toString() {
    return "Symbol{id=" + id + ", name='" + name + "', type='" + type +
        "', scopeLevel=" + scopeLevel + ", unid=" + unid + "}";
  }
}

class Scope {
  Map<String, Symbol> symbols = new HashMap<>();
  int level;
  Scope parentScope;

  public Scope(int level, Scope parentScope) {
    this.level = level;
    this.parentScope = parentScope;
  }
}

abstract class Node {
  int unid;
  int parentUnid;
}

class InnerNode extends Node {
  String symb;
  List<Integer> childrenIds;
  public InnerNode(int unid, String symb, int parentUnid) {
    this.unid = unid;
    this.symb = symb;
    this.parentUnid = parentUnid;
    this.childrenIds = new ArrayList<>();
  }
}

class LeafNode extends Node {
  String terminal;
  public LeafNode(int unid, String terminal, int parentUnid) {
    this.unid = unid;
    this.terminal = terminal;
    this.parentUnid = parentUnid;
  }
}

class Pair<K, V> {
  private K key;
  private V value;

  public Pair(K key, V value) {
    this.key = key;
    this.value = value;
  }

  public K getKey() { return key; }
  public V getValue() { return value; }
}

class VarDeclaration {
  String varType;
  String varName;
  int unid;

  public VarDeclaration(String varType, String varName, int unid) {
    this.varType = varType;
    this.varName = varName;
    this.unid = unid;
  }
}

class ParamDeclaration {
  String paramName;
  int unid;

  public ParamDeclaration(String paramName, int unid) {
    this.paramName = paramName;
    this.unid = unid;
  }
}

public class RecSPLSymbolTable {
  private Map<String, Integer> nameToIdMap = new HashMap<>();
  private Map<Integer, Node> nodes = new HashMap<>();
  private Deque<Scope> scopeStack = new ArrayDeque<>();
  private List<Scope> allScopes = new ArrayList<>();
  private int rootUnid;
  private int symbolIdCounter = 0;
  private boolean insideFunction = false;

  public static void main(String[] args) {
    RecSPLSymbolTable symbolTable = new RecSPLSymbolTable();
    try {
      symbolTable.loadXML("output/parserOutput.xml");
      symbolTable.buildSymbolTable();
      symbolTable.printSymbolTable();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void loadXML(String filePath) throws Exception {
    File xmlFile = new File(filePath);
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document doc = dBuilder.parse(xmlFile);
    doc.getDocumentElement().normalize();

    NodeList rootList = doc.getElementsByTagName("ROOT");
    if (rootList.getLength() > 0) {
      Element rootElement = (Element)rootList.item(0);
      int unid = Integer.parseInt(getTextContent(rootElement, "UNID"));
      String symb = getTextContent(rootElement, "SYMB");
      InnerNode rootNode = new InnerNode(unid, symb, -1);
      rootUnid = unid;
      nodes.put(unid, rootNode);
      Element childrenElement =
          (Element)rootElement.getElementsByTagName("CHILDREN").item(0);
      NodeList idList = childrenElement.getElementsByTagName("ID");
      for (int i = 0; i < idList.getLength(); i++) {
        int childId = Integer.parseInt(idList.item(i).getTextContent());
        rootNode.childrenIds.add(childId);
      }
    }

    NodeList innerNodesList = doc.getElementsByTagName("IN");
    for (int i = 0; i < innerNodesList.getLength(); i++) {
      Element inElement = (Element)innerNodesList.item(i);
      int unid = Integer.parseInt(getTextContent(inElement, "UNID"));
      String symb = getTextContent(inElement, "SYMB");
      int parentUnid = Integer.parseInt(getTextContent(inElement, "PARENT"));
      InnerNode innerNode = new InnerNode(unid, symb, parentUnid);
      nodes.put(unid, innerNode);

      NodeList childrenList = inElement.getElementsByTagName("CHILDREN");
      if (childrenList.getLength() > 0) {
        Element childrenElement = (Element)childrenList.item(0);
        NodeList idList = childrenElement.getElementsByTagName("ID");
        for (int j = 0; j < idList.getLength(); j++) {
          int childId = Integer.parseInt(idList.item(j).getTextContent());
          innerNode.childrenIds.add(childId);
        }
      }
    }

    NodeList leafNodesList = doc.getElementsByTagName("LEAF");
    for (int i = 0; i < leafNodesList.getLength(); i++) {
      Element leafElement = (Element)leafNodesList.item(i);
      int unid = Integer.parseInt(getTextContent(leafElement, "UNID"));
      String terminal = getTextContent(leafElement, "TERMINAL");
      int parentUnid = Integer.parseInt(getTextContent(leafElement, "PARENT"));
      LeafNode leafNode = new LeafNode(unid, terminal, parentUnid);
      nodes.put(unid, leafNode);
    }
  }

  private String getTextContent(Element parent, String tagName) {
    NodeList nodeList = parent.getElementsByTagName(tagName);
    if (nodeList.getLength() > 0) {
      return nodeList.item(0).getTextContent().trim();
    }
    return "";
  }

  public void buildSymbolTable() {
    Node rootNode = nodes.get(rootUnid);
    Scope globalScope = new Scope(0, null);
    scopeStack.push(globalScope);
    allScopes.add(globalScope);
    traverseAndBuild(rootNode);
  }

  private void traverseAndBuild(Node node) {
    if (node instanceof InnerNode) {
      InnerNode innerNode = (InnerNode)node;

      if (innerNode.symb.equals("PROG") || innerNode.symb.equals("FUNCTIONS")) {
        if (!innerNode.symb.equals("PROG")) {
          int newScopeLevel = scopeStack.peek().level + 1;
          Scope parentScope = scopeStack.peek();
          Scope newScope = new Scope(newScopeLevel, parentScope);
          scopeStack.push(newScope);
          allScopes.add(newScope);
        }
      } else if (innerNode.symb.equals("BODY")) {
        if (!insideFunction) {
          int newScopeLevel = scopeStack.peek().level + 1;
          Scope parentScope = scopeStack.peek();
          Scope newScope = new Scope(newScopeLevel, parentScope);
          scopeStack.push(newScope);
          allScopes.add(newScope);
        }
      }

      if (innerNode.symb.equals("GLOBVARS") ||
          innerNode.symb.equals("LOCVARS")) {
        processVarDeclarations(innerNode);
      }

      if (innerNode.symb.equals("DECL")) {
        processFunctionDeclaration(innerNode);
        return;
      }

      for (int childId : innerNode.childrenIds) {
        Node childNode = nodes.get(childId);
        traverseAndBuild(childNode);
      }

      if (innerNode.symb.equals("BODY")) {
        if (!insideFunction) {
          Scope exitedScope = scopeStack.pop();
        }
      } else if (innerNode.symb.equals("PROG") ||
                 innerNode.symb.equals("FUNCTIONS")) {
        if (!innerNode.symb.equals("PROG")) {
          Scope exitedScope = scopeStack.pop();
        }
      }
    }
  }

  private void processVarDeclarations(InnerNode varNode) {
    List<VarDeclaration> declarations = new ArrayList<>();
    flattenVarDeclarations(varNode, declarations);

    for (VarDeclaration decl : declarations) {
      String varType = decl.varType;
      String varName = decl.varName;
      int unid = decl.unid;
      if (varName != null && varType != null) {
        Scope currentScope = scopeStack.peek();
        if (currentScope.symbols.containsKey(varName)) {
        } else {
          int symbolId;
          if (nameToIdMap.containsKey(varName)) {
            symbolId = nameToIdMap.get(varName);
          } else {
            symbolId = symbolIdCounter++;
            nameToIdMap.put(varName, symbolId);
          }
          Symbol symbol =
              new Symbol(varName, varType, symbolId, currentScope.level, unid);
          currentScope.symbols.put(varName, symbol);
        }
      }
    }
  }

  private void flattenVarDeclarations(InnerNode node,
                                      List<VarDeclaration> declarations) {
    if (node == null) {
      return;
    }

    String varType = null;

    for (int childId : node.childrenIds) {
      Node childNode = nodes.get(childId);
      if (childNode instanceof InnerNode) {
        InnerNode innerChild = (InnerNode)childNode;
        if (innerChild.symb.equals("VTYP")) {
          varType = extractType(innerChild);
        } else if (innerChild.symb.equals("VNAME")) {
          String varName = extractVarName(innerChild);
          int unid = innerChild.unid;
          declarations.add(new VarDeclaration(varType, varName, unid));
        } else if (innerChild.symb.equals("GLOBVARS") ||
                   innerChild.symb.equals("LOCVARS")) {
          flattenVarDeclarations(innerChild, declarations);
        }
      }
    }
  }

  private void processFunctionDeclaration(InnerNode declNode) {
    Scope parentScope = scopeStack.peek();
    int newScopeLevel = parentScope.level + 1;
    Scope functionScope = new Scope(newScopeLevel, parentScope);
    scopeStack.push(functionScope);
    allScopes.add(functionScope);

    boolean previousInsideFunction = insideFunction;
    insideFunction = true;

    InnerNode headerNode = null;
    InnerNode bodyNode = null;
    for (int childId : declNode.childrenIds) {
      Node childNode = nodes.get(childId);
      if (childNode instanceof InnerNode) {
        InnerNode innerChild = (InnerNode)childNode;
        if (innerChild.symb.equals("HEADER")) {
          headerNode = innerChild;
        } else if (innerChild.symb.equals("BODY")) {
          bodyNode = innerChild;
        }
      }
    }
    if (headerNode != null) {
      processFunctionHeader(headerNode);
    }
    if (bodyNode != null) {
      traverseAndBuild(bodyNode);
    }
    Scope exitedScope = scopeStack.pop();

    insideFunction = previousInsideFunction;
  }

  private void processFunctionHeader(InnerNode headerNode) {
    String returnType = null;
    String functionName = null;
    List<ParamDeclaration> parameterDeclarations = new ArrayList<>();
    for (int childId : headerNode.childrenIds) {
      Node childNode = nodes.get(childId);
      if (childNode instanceof InnerNode) {
        InnerNode innerChild = (InnerNode)childNode;
        if (innerChild.symb.equals("FTYP")) {
          returnType = extractType(innerChild);
        } else if (innerChild.symb.equals("FNAME")) {
          functionName = extractVarName(innerChild);
        } else if (innerChild.symb.equals("VNAME")) {
          String paramName = extractVarName(innerChild);
          int unid = innerChild.unid;
          parameterDeclarations.add(new ParamDeclaration(paramName, unid));
        }
      }
    }

    if (functionName != null && returnType != null) {
      Scope functionScope = scopeStack.peek();
      Scope parentScope = functionScope.parentScope;
      if (parentScope.symbols.containsKey(functionName)) {
      } else {
        int symbolId;
        if (nameToIdMap.containsKey(functionName)) {
          symbolId = nameToIdMap.get(functionName);
        } else {
          symbolId = symbolIdCounter++;
          nameToIdMap.put(functionName, symbolId);
        }
        int unid = headerNode.unid;
        Symbol functionSymbol = new Symbol(functionName, returnType, symbolId,
                                           parentScope.level, unid);
        parentScope.symbols.put(functionName, functionSymbol);
      }

      for (ParamDeclaration paramDecl : parameterDeclarations) {
        String paramName = paramDecl.paramName;
        int unid = paramDecl.unid;
        if (functionScope.symbols.containsKey(paramName)) {
        } else {
          int symbolId;
          if (nameToIdMap.containsKey(paramName)) {
            symbolId = nameToIdMap.get(paramName);
          } else {
            symbolId = symbolIdCounter++;
            nameToIdMap.put(paramName, symbolId);
          }
          Symbol paramSymbol =
              new Symbol(paramName, "n", symbolId, functionScope.level, unid);
          functionScope.symbols.put(paramName, paramSymbol);
        }
      }
    }
  }

  private String extractType(InnerNode typeNode) {
    for (int childId : typeNode.childrenIds) {
      Node childNode = nodes.get(childId);
      if (childNode instanceof LeafNode) {
        LeafNode leaf = (LeafNode)childNode;
        return leaf.terminal;
      }
    }
    return null;
  }

  private String extractVarName(InnerNode varNameNode) {
    for (int childId : varNameNode.childrenIds) {
      Node childNode = nodes.get(childId);
      if (childNode instanceof LeafNode) {
        LeafNode leaf = (LeafNode)childNode;
        return leaf.terminal;
      }
    }
    return null;
  }

  public void printSymbolTable() {
    System.out.println("\nSymbol Table:");
    allScopes.sort(Comparator.comparingInt(scope -> scope.level));
    for (Scope scope : allScopes) {
      if (!scope.symbols.isEmpty()) {
        System.out.println("Scope Level " + scope.level + ":");
        for (Symbol symbol : scope.symbols.values()) {
          System.out.println(symbol);
        }
      }
    }
  }

  public Deque<Scope> getScopes() { return scopeStack; }
}
