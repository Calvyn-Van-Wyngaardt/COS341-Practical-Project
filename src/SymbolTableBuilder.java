import java.io.File;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;

// Class representing a node in the parse tree
class TreeNode {
    int unid;
    String symb;     // For nonterminals
    String terminal; // For terminals
    int parentUnid;
    List<Integer> childrenUnids = new ArrayList<>();

    // For easy reference to child nodes
    List<TreeNode> children = new ArrayList<>();

    @Override
    public String toString() {
        return "TreeNode{"
            + "unid=" + unid + ", symb='" + symb + '\'' + ", terminal='" +
            terminal + '\'' + ", parentUnid=" + parentUnid +
            ", childrenUnids=" + childrenUnids + '}';
    }
}

class SymbolTableEntry {
    String name;
    String type;
    int scopeLevel;
    int declaringNodeUnid;

    public SymbolTableEntry(String name, String type, int scopeLevel,
                            int declaringNodeUnid) {
        this.name = name;
        this.type = type;
        this.scopeLevel = scopeLevel;
        this.declaringNodeUnid = declaringNodeUnid;
    }

    @Override
    public String toString() {
        return "Name: " + name + ", Type: " + type +
            ", Scope Level: " + scopeLevel +
            ", Declared at UNID: " + declaringNodeUnid;
    }
}

public class SymbolTableBuilder {

    // Maps UNID to TreeNode
    Map<Integer, TreeNode> nodeMap = new HashMap<>();

    // Symbol table: List of symbol table entries for each scope level
    Map<Integer, List<SymbolTableEntry>> symbolTable = new HashMap<>();

    // Current scope level
    int currentScopeLevel = 0;

    // To track visited nodes and avoid infinite recursion
    Set<Integer> visitedNodes = new HashSet<>();

    public static void main(String[] args) {
        SymbolTableBuilder builder = new SymbolTableBuilder();
        // Use default "parseOutput.xml" or pass as argument
        String xmlFilePath = "parserOutput.xml";
        if (args.length > 0) {
            xmlFilePath = args[0];
        }
        builder.buildSymbolTable(xmlFilePath);
    }

    public void buildSymbolTable(String xmlFilePath) {
        try {
            // Parse the XML file and build the parse tree
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builderFactory = factory.newDocumentBuilder();
            Document doc = builderFactory.parse(new File(xmlFilePath));
            doc.getDocumentElement().normalize();

            // Build nodes from XML
            buildNodesFromXML(doc);

            // Link parent and children nodes
            linkNodes();

            // Debug: Print all parsed nodes
            System.out.println("\nParsed Nodes:");
            for (TreeNode node : nodeMap.values()) {
                System.out.println(node);
            }

            // Start traversal from the root node
            TreeNode root = findRootNode();

            if (root == null) {
                System.out.println("Error: Root node not found.");
                return;
            }

            // Traverse the tree and build the symbol table
            traverseTree(root);

            // Print the symbol table
            printSymbolTable();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Build nodes from the XML document
    private void buildNodesFromXML(Document doc) {
        // Handle ROOT node
        NodeList rootNodes = doc.getElementsByTagName("ROOT");
        for (int i = 0; i < rootNodes.getLength(); i++) {
            Element rootElement = (Element) rootNodes.item(i);
            TreeNode rootNode = new TreeNode();
            rootNode.unid = Integer.parseInt(
                rootElement.getElementsByTagName("UNID").item(0).getTextContent());
            rootNode.symb =
                rootElement.getElementsByTagName("SYMB").item(0).getTextContent();

            NodeList childrenList = rootElement.getElementsByTagName("CHILDREN");
            if (childrenList.getLength() > 0) {
                Element childrenElement = (Element) childrenList.item(0);
                NodeList idList = childrenElement.getElementsByTagName("ID");
                for (int j = 0; j < idList.getLength(); j++) {
                    int childUnid = Integer.parseInt(idList.item(j).getTextContent());
                    rootNode.childrenUnids.add(childUnid);
                }
            }

            nodeMap.put(rootNode.unid, rootNode);
        }

        // Handle INNERNODES
        NodeList innerNodes = doc.getElementsByTagName("IN");
        for (int i = 0; i < innerNodes.getLength(); i++) {
            Element inElement = (Element) innerNodes.item(i);
            TreeNode innerNode = new TreeNode();
            innerNode.unid = Integer.parseInt(
                inElement.getElementsByTagName("UNID").item(0).getTextContent());
            innerNode.parentUnid = Integer.parseInt(
                inElement.getElementsByTagName("PARENT").item(0).getTextContent());
            innerNode.symb =
                inElement.getElementsByTagName("SYMB").item(0).getTextContent();

            NodeList childrenList = inElement.getElementsByTagName("CHILDREN");
            if (childrenList.getLength() > 0) {
                Element childrenElement = (Element) childrenList.item(0);
                NodeList idList = childrenElement.getElementsByTagName("ID");
                for (int j = 0; j < idList.getLength(); j++) {
                    int childUnid = Integer.parseInt(idList.item(j).getTextContent());
                    innerNode.childrenUnids.add(childUnid);
                }
            }

            nodeMap.put(innerNode.unid, innerNode);
        }

        // Handle LEAFNODES
        NodeList leafNodes = doc.getElementsByTagName("LEAF");
        for (int i = 0; i < leafNodes.getLength(); i++) {
            Element leafElement = (Element) leafNodes.item(i);
            TreeNode leafNode = new TreeNode();
            leafNode.unid = Integer.parseInt(
                leafElement.getElementsByTagName("UNID").item(0).getTextContent());
            leafNode.parentUnid = Integer.parseInt(
                leafElement.getElementsByTagName("PARENT").item(0).getTextContent());
            leafNode.terminal = leafElement.getElementsByTagName("TERMINAL")
                                    .item(0)
                                    .getTextContent()
                                    .trim();

            nodeMap.put(leafNode.unid, leafNode);
        }
    }

    // Link nodes by setting parent and child relationships
    private void linkNodes() {
        for (TreeNode node : nodeMap.values()) {
            // Link to parent
            if (node.parentUnid != 0) {
                TreeNode parentNode = nodeMap.get(node.parentUnid);
                if (parentNode != null) {
                    parentNode.children.add(node);
                } else {
                    System.out.println("Warning: Parent node with UNID " +
                                        node.parentUnid + " not found for node " +
                                        node.unid);
                }
            }
            // Link to children
            for (Integer childUnid : node.childrenUnids) {
                TreeNode childNode = nodeMap.get(childUnid);
                if (childNode != null) {
                    node.children.add(childNode);
                    childNode.parentUnid = node.unid;
                } else {
                    System.out.println("Warning: Child node with UNID " + childUnid +
                                        " not found for node " + node.unid);
                }
            }
        }
    }

    // Find the root node of the parse tree
    private TreeNode findRootNode() {
        for (TreeNode node : nodeMap.values()) {
            if (node.parentUnid == 0) { // Root node has no parent
                return node;
            }
        }
        return null;
    }

    // Traverse the parse tree and build the symbol table
    private void traverseTree(TreeNode node) {
        if (node == null || visitedNodes.contains(node.unid)) {
            return;
        }

        // Mark node as visited
        visitedNodes.add(node.unid);

        // Debug: Print current node being traversed
        System.out.println("\nTraversing Node: " + node);

        // Check for entering a new scope
        if (isScopeCreatingNode(node)) {
            currentScopeLevel++;
            // Initialize symbol table for this scope level
            symbolTable.putIfAbsent(currentScopeLevel, new ArrayList<>());
            System.out.println("Entered new scope: Level " + currentScopeLevel);
        }

        // Process the node for variable or function declarations
        if (isVariableDeclarationNode(node)) {
            System.out.println("Found Variable Declaration: " + node);
            processVariableDeclaration(node);
        } else if (isFunctionDeclarationNode(node)) {
            System.out.println("Found Function Declaration: " + node);
            processFunctionDeclaration(node);
        }

        // Recursively traverse child nodes
        for (TreeNode child : node.children) {
            traverseTree(child);
        }

        // Check for exiting a scope
        if (isScopeCreatingNode(node)) {
            System.out.println("Exiting scope: Level " + currentScopeLevel);
            // After finishing this scope, decrease the scope level
            currentScopeLevel--;
        }
    }

    // Determine if a node creates a new scope (e.g., function bodies, blocks)
    private boolean isScopeCreatingNode(TreeNode node) {
        // Adjust based on actual grammar symbols
        return "BODY".equals(node.symb) || "ALGO".equals(node.symb) ||
               "PROG".equals(node.symb) || "FUNC_BODY".equals(node.symb);
    }

    // Determine if a node represents a variable declaration
    private boolean isVariableDeclarationNode(TreeNode node) {
        // Check if node.symb is "VNAME" and its parent symb is "VTYP" or "GLOBVARS"
        if ("VNAME".equals(node.symb)) {
            TreeNode parent = nodeMap.get(node.parentUnid);
            if (parent != null &&
                ("VTYP".equals(parent.symb) || "GLOBVARS".equals(parent.symb) || "PARAMS".equals(parent.symb))) {
                return true;
            }
        }
        return false;
    }

    // Process a variable declaration node
    private void processVariableDeclaration(TreeNode node) {
        // Retrieve the variable name from the terminal node
        String varName = getTerminalValue(node);
        System.out.println("Processing Variable: " + varName);

        // Retrieve the type from the parent node
        TreeNode parent = nodeMap.get(node.parentUnid);
        String varType = getTerminalValue(parent);
        System.out.println("Variable Type: " + varType);

        if (varName != null && varType != null) {
            // Check for duplicates
            boolean duplicate = false;
            List<SymbolTableEntry> currentScopeSymbols =
                symbolTable.get(currentScopeLevel);
            for (SymbolTableEntry entry : currentScopeSymbols) {
                if (entry.name.equals(varName)) {
                    System.out.println("Error: Duplicate variable declaration for " +
                                        varName + " in scope " + currentScopeLevel);
                    duplicate = true;
                    break;
                }
            }
            if (!duplicate) {
                // Create a symbol table entry
                SymbolTableEntry entry = new SymbolTableEntry(
                    varName, varType, currentScopeLevel, node.unid);
                symbolTable.get(currentScopeLevel).add(entry);
                System.out.println("Added Variable to Symbol Table: " + entry);
            }
        } else {
            System.out.println("Error: Variable name or type is null.");
        }
    }

    // Determine if a node represents a function declaration
    private boolean isFunctionDeclarationNode(TreeNode node) {
        // Check if node.symb is "FNAME" and its parent symb is "HEADER" or "DECL"
        if ("FNAME".equals(node.symb)) {
            TreeNode parent = nodeMap.get(node.parentUnid);
            if (parent != null &&
                ("HEADER".equals(parent.symb) || "DECL".equals(parent.symb))) {
                return true;
            }
        }
        return false;
    }

    // Process a function declaration node
    private void processFunctionDeclaration(TreeNode node) {
        // Retrieve the function name from the terminal node
        String funcName = getTerminalValue(node);
        System.out.println("Processing Function: " + funcName);

        // Retrieve the return type from the grandparent node
        TreeNode parent = nodeMap.get(node.parentUnid);
        TreeNode grandParent = nodeMap.get(parent.parentUnid);
        String returnType = getTerminalValue(grandParent);
        System.out.println("Function Return Type: " + returnType);

        if (funcName != null && returnType != null) {
            // Check for duplicates
            boolean duplicate = false;
            List<SymbolTableEntry> currentScopeSymbols =
                symbolTable.get(currentScopeLevel);
            for (SymbolTableEntry entry : currentScopeSymbols) {
                if (entry.name.equals(funcName)) {
                    System.out.println("Error: Duplicate function declaration for " +
                                        funcName + " in scope " + currentScopeLevel);
                    duplicate = true;
                    break;
                }
            }
            if (!duplicate) {
                // Create a symbol table entry
                SymbolTableEntry entry = new SymbolTableEntry(
                    funcName, "Function: " + returnType, currentScopeLevel, node.unid);
                symbolTable.get(currentScopeLevel).add(entry);
                System.out.println("Added Function to Symbol Table: " + entry);
            }

            // Traverse the function body and add parameters and internal variables
            processFunctionScope(node);
        } else {
            System.out.println("Error: Function name or return type is null.");
        }
    }

    // Process function parameters and internal variables
    private void processFunctionScope(TreeNode funcNode) {
        // Look for parameters (assuming "PARAMS" or similar nonterminal)
        for (TreeNode child : funcNode.children) {
            if ("PARAMS".equals(child.symb)) {
                // Traverse and add parameters
                traverseTree(child);
            }
        }

        // Look for function body and process internal variables (assuming "BODY" or "FUNC_BODY")
        for (TreeNode child : funcNode.children) {
            if ("BODY".equals(child.symb) || "FUNC_BODY".equals(child.symb)) {
                // Enter new scope for function body
                currentScopeLevel++;
                symbolTable.putIfAbsent(currentScopeLevel, new ArrayList<>());
                traverseTree(child);
                currentScopeLevel--;
            }
        }
    }

    // Helper method to get the terminal value of a node
    private String getTerminalValue(TreeNode node) {
        if (node.terminal != null && !node.terminal.isEmpty()) {
            return node.terminal;
        } else if (!node.children.isEmpty()) {
            for (TreeNode child : node.children) {
                String value = getTerminalValue(child);
                if (value != null) {
                    return value;
                }
            }
        }
        return null;
    }

    // Print the symbol table
    private void printSymbolTable() {
        System.out.println("\nSymbol Table:");
        for (Map.Entry<Integer, List<SymbolTableEntry>> entry :
             symbolTable.entrySet()) {
            int scopeLevel = entry.getKey();
            List<SymbolTableEntry> symbols = entry.getValue();
            System.out.println("Scope Level: " + scopeLevel);
            for (SymbolTableEntry symbol : symbols) {
                System.out.println("  " + symbol);
            }
        }
    }
}