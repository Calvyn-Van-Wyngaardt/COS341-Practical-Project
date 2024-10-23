import java.io.File;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class SymbolTableBuilder {
  private SymbolTable symbolTable;
  private Map<String, String> currentScope;

  public SymbolTableBuilder() {
    this.symbolTable = new SymbolTable();
    this.currentScope = new HashMap<>();
  }

  public void buildSymbolTable(String xmlFilePath) {
    try {
      File inputFile = new File(xmlFilePath);
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.parse(inputFile);
      doc.getDocumentElement().normalize();

      processTree(doc);

    } catch (Exception e) {
      System.err.println("Error building symbol table: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void processTree(Document doc) {
    symbolTable.enterScope();
    processGlobalDeclarations(doc);
    processFunctions(doc);
    processMainProgram(doc);
  }

  private void processGlobalDeclarations(Document doc) {
    NodeList globVarsNodes = doc.getElementsByTagName("IN");
    for (int i = 0; i < globVarsNodes.getLength(); i++) {
      Node node = globVarsNodes.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element element = (Element)node;
        if (element.getElementsByTagName("SYMB")
                .item(0)
                .getTextContent()
                .equals("GLOBVARS")) {
          processGlobalVarNode(element, doc);
        }
      }
    }
  }

  private void processGlobalVarNode(Element globalVarNode, Document doc) {
    NodeList children =
        globalVarNode.getElementsByTagName("CHILDREN").item(0).getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node child = children.item(i);
      if (child.getNodeType() == Node.ELEMENT_NODE &&
          child.getNodeName().equals("ID")) {
        String childId = child.getTextContent();

        String type = findNodeValueBySymb(doc, childId, "VTYP");
        String name = findNodeValueBySymb(doc, childId, "VNAME");

        if (type != null && name != null) {
          SymbolAttributes attrs =
              new SymbolAttributes(type, SymbolKind.VARIABLE, "global");
          if (!symbolTable.defineSymbol(name, attrs)) {
            System.err.println("Error: Symbol '" + name +
                               "' already defined in global scope");
          }
        }
      }
    }
  }

  private String findNodeValueBySymb(Document doc, String id, String symb) {
    NodeList nodes = doc.getElementsByTagName("IN");
    for (int i = 0; i < nodes.getLength(); i++) {
      Element element = (Element)nodes.item(i);
      if (element.getElementsByTagName("SYMB").item(0).getTextContent().equals(
              symb)) {
        NodeList children =
            element.getElementsByTagName("CHILDREN").item(0).getChildNodes();
        for (int j = 0; j < children.getLength(); j++) {
          Node child = children.item(j);
          if (child.getNodeType() == Node.ELEMENT_NODE) {
            return findLeafValue(doc, child.getTextContent());
          }
        }
      }
    }
    return null;
  }

  private String findLeafValue(Document doc, String id) {
    NodeList leaves = doc.getElementsByTagName("LEAF");
    for (int i = 0; i < leaves.getLength(); i++) {
      Element leaf = (Element)leaves.item(i);
      if (leaf.getElementsByTagName("UNID").item(0).getTextContent().equals(
              id)) {
        return leaf.getElementsByTagName("TERMINAL").item(0).getTextContent();
      }
    }
    return null;
  }

  private void processFunctions(Document doc) {
    NodeList functionNodes = doc.getElementsByTagName("FUNCTIONS");
  }

  private void processMainProgram(Document doc) {
    NodeList algoNodes = doc.getElementsByTagName("ALGO");
  }

  public SymbolTable getSymbolTable() { return symbolTable; }

  public void displaySymbolTable() {
    System.out.println("\n=== Symbol Table Contents ===");
    printTableHeader();

    List<SymbolTableEntry> allSymbols = new ArrayList<>();
    for (int scopeLevel = 0; scopeLevel < symbolTable.getScopes().size();
         scopeLevel++) {
      Map<String, SymbolAttributes> scope =
          symbolTable.getScopes().get(scopeLevel);
      for (Map.Entry<String, SymbolAttributes> entry : scope.entrySet()) {
        allSymbols.add(
            new SymbolTableEntry(entry.getKey(), entry.getValue(), scopeLevel));
      }
    }

    Collections.sort(allSymbols, (a, b) -> {
      if (a.scopeLevel != b.scopeLevel) {
        return a.scopeLevel - b.scopeLevel;
      }
      return a.name.compareTo(b.name);
    });

    String currentScope = "";
    for (SymbolTableEntry entry : allSymbols) {
      String scopeLabel = "Scope Level " + entry.scopeLevel +
                          (entry.scopeLevel == 0 ? " (Global)" : " (Local)");

      if (!currentScope.equals(scopeLabel)) {
        System.out.println("\n" + scopeLabel);
        printTableDivider();
        currentScope = scopeLabel;
      }

      printSymbolEntry(entry);
    }

    System.out.println("\nTotal Symbols: " + allSymbols.size());
  }

  private void printTableHeader() {
    System.out.println(String.format("%-20s %-15s %-10s %-20s %-20s", "Name",
                                     "Type", "Kind", "Scope",
                                     "Additional Info"));
    printTableDivider();
  }

  private void printTableDivider() { System.out.println("-".repeat(85)); }

  private void printSymbolEntry(SymbolTableEntry entry) {
    SymbolAttributes attrs = entry.attributes;
    String additionalInfo =
        formatAdditionalInfo(attrs.getAdditionalAttributes());

    System.out.println(String.format(
        "%-20s %-15s %-10s %-20s %-20s", entry.name, attrs.getType(),
        attrs.getKind(), attrs.getScope(), additionalInfo));
  }

  private String formatAdditionalInfo(Map<String, Object> attributes) {
    if (attributes.isEmpty()) {
      return "-";
    }
    return attributes.entrySet()
        .stream()
        .map(e -> e.getKey() + "=" + e.getValue())
        .reduce((a, b) -> a + ", " + b)
        .orElse("");
  }

  private static class SymbolTableEntry {
    String name;
    SymbolAttributes attributes;
    int scopeLevel;

    SymbolTableEntry(String name, SymbolAttributes attributes, int scopeLevel) {
      this.name = name;
      this.attributes = attributes;
      this.scopeLevel = scopeLevel;
    }
  }
}

class SymbolTable {
  private Stack<Map<String, SymbolAttributes>> scopes;
  private int currentScopeLevel;

  public SymbolTable() {
    this.scopes = new Stack<>();
    this.currentScopeLevel = -1;
  }

  public void enterScope() {
    scopes.push(new HashMap<>());
    currentScopeLevel++;
  }

  public void exitScope() {
    if (!scopes.isEmpty()) {
      scopes.pop();
      currentScopeLevel--;
    }
  }

  public boolean defineSymbol(String name, SymbolAttributes attrs) {
    if (scopes.isEmpty())
      return false;

    if (scopes.peek().containsKey(name)) {
      return false;
    }

    scopes.peek().put(name, attrs);
    return true;
  }

  public SymbolAttributes lookupSymbol(String name) {
    for (int i = scopes.size() - 1; i >= 0; i--) {
      SymbolAttributes attrs = scopes.get(i).get(name);
      if (attrs != null) {
        return attrs;
      }
    }
    return null;
  }

  public Stack<Map<String, SymbolAttributes>> getScopes() { return scopes; }

  public int getCurrentScopeLevel() { return currentScopeLevel; }
}

class SymbolAttributes {
  private String type;
  private SymbolKind kind;
  private String scope;
  private Map<String, Object> additionalAttributes;

  public SymbolAttributes(String type, SymbolKind kind, String scope) {
    this.type = type;
    this.kind = kind;
    this.scope = scope;
    this.additionalAttributes = new HashMap<>();
  }

  public String getType() { return type; }
  public SymbolKind getKind() { return kind; }
  public String getScope() { return scope; }

  public void setAttribute(String key, Object value) {
    additionalAttributes.put(key, value);
  }

  public Object getAttribute(String key) {
    return additionalAttributes.get(key);
  }

  public Map<String, Object> getAdditionalAttributes() {
    return additionalAttributes;
  }
}

enum SymbolKind { VARIABLE, FUNCTION, PARAMETER, CONSTANT }