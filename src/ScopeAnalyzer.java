import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class ScopeAnalyzer {
    private static Map<String, TreeNode> nodeMap = new HashMap<String, TreeNode>();
    private static Set<String> processedNodes = new HashSet<>();
    private static String currentScopeParent = null;
    Set<String> recursionStack = new HashSet<>();

    private static final String TOKEN_V = "V_[a-z][a-z0-9]*$";
    private static final String TOKEN_F = "F_[a-z][a-z0-9]*$";
    private static final String TOKEN_T = "[A-Z][a-z]{0,7}";
    private static final String TOKEN_N = "-?[0-9]+(\\.[0-9]+)?";
    private static final Set<String> RESERVED_KEYWORDS = new HashSet<>(
      Arrays.asList("main", "begin", "end", "skip", "halt", "print", "if",
                    "then", "else", "not", "sqrt", "or", "and", "eq", "grt",
                    "add", "sub", "mul", "div", "void", "num", "text", "return", "skip"));
    private static final Set<String> OPERATORS = new HashSet<>(
      Arrays.asList("=", "+", "-", "*", "/", "(", ")", ",", ";", "{", "}"));
    
    public ScopeAnalyzer() {

    }

    public void initTreeWithScoping() {
        try {
            File inputFile = new File("input/syntax_tree.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
    
            // Initialize Scope Manager - this will take care of scope stuff
            ScopeChecker sc = new ScopeChecker();
    
            // Process ROOT
            Element rootElement = (Element) doc.getElementsByTagName("ROOT").item(0);
            System.out.println("Processing ROOT");
            processNodeWithScoping(rootElement, sc);
    
            // Process INNERNODES
            NodeList innerNodes = doc.getElementsByTagName("IN");
            System.out.println("Processing INNERNODES");
            for (int i = 0; i < innerNodes.getLength(); i++) {
                processNodeWithScoping((Element) innerNodes.item(i), sc);
            }
    
            // Process LEAFNODES
            NodeList leafNodes = doc.getElementsByTagName("LEAF");
            System.out.println("Processing LEAFNODES");
            for (int i = 0; i < leafNodes.getLength(); i++) {
                processLeafNodeWithScoping((Element) leafNodes.item(i), sc);
            }

            //Print global SymbolTable
            sc.exitScope();

            //Cleanup tables
            sc.checkSymbolTables();

            //Do lookups for var/function init/declaration
            System.out.println(sc.lookups());

            //Print tables
            System.out.println(sc.printFinalTables());

            // Connect children 
            for (TreeNode node : nodeMap.values()) {
                connectChildren(node, doc);
            }
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void processNodeWithScoping(Element element, ScopeChecker sc) {
        String unid = element.getElementsByTagName("UNID").item(0).getTextContent();
        String symbol = element.getElementsByTagName("SYMB").item(0).getTextContent();
        
        //Not dealing with ROOT
        if (!unid.equals("1")) {
            String parent = element.getElementsByTagName("PARENT").item(0).getTextContent();  // Get parent node ID
    
            // Scope transition handling
            if (currentScopeParent == null || !currentScopeParent.equals(parent)) {
                if (currentScopeParent != null) {
                    sc.exitScope();  // Exit previous scope
                    System.out.println("Exited scope of parent " + currentScopeParent);
                }
                sc.enterScope();  // Enter new scope
                currentScopeParent = parent;  // Update the current parent
                System.out.println("Entered new scope of parent " + currentScopeParent);
            }
        }


        System.out.println(String.format("PROCESSING NODE %s - %s", unid, symbol));

        // Store the node in the nodeMap but don't add anything to the scope
        TreeNode node = new TreeNode(unid, symbol);
        nodeMap.put(unid, node);
    }

    private static void processLeafNodeWithScoping(Element element, ScopeChecker sc) {
        String unid = element.getElementsByTagName("UNID").item(0).getTextContent();
        String value = element.getElementsByTagName("TERMINAL").item(0).getTextContent();
        String termType = getTerminalType(value);

        // System.out.println(String.format("PROCESSING LEAF %s - %s", unid, value));
    
        String symbolName = "UNID_" + unid;  // Assuming the terminal can be uniquely identified by UNID
        

        //Check symbols first with lookup
        //  If not found
            //  If declaration
                //  Add declaration
            //  Else If not declaration
                //  Throw error     -- No declaration found for var/function
        //  If found
            //  If declaration
                //  Throw error     -- Cannot have duplicate declarations
            //  Else If not declaration
                //  Change symbol in table

                
        if (value.equals("begin") || value.equals("{")) {
            sc.enterScope();
        } 
        else if (value.equals("end") || value.equals("}")) {
            sc.exitScope();
        }
        else {
            sc.addSymbol(symbolName, value, termType);
        }
        

        // Store node as a terminal
        TreeNode node = new TreeNode(unid, value);
        nodeMap.put(unid, node);
    }

    private static String getTerminalType(String terminal) {
        String type = "";

        if (terminal.matches(TOKEN_V)) {
            type = "variable_name";
        } else if (terminal.equals("void")) {
            type = "void_function";
        } else if (terminal.matches(TOKEN_F)) {
            type = "function_name";
        } else if (terminal.matches(TOKEN_T) || terminal.equals("text")) {
            type = "text";
        } else if (terminal.matches(TOKEN_N) || terminal.equals("num")) {
            type = "number";
        } else if (RESERVED_KEYWORDS.contains(terminal)) {
            type = "reserved_keyword";
        } else if (OPERATORS.contains(terminal)) {
            type = "operator";
        }

        return type;
    }
    
    private void connectChildren(TreeNode node, Document doc) {
        if (processedNodes.contains(node.getUId())) {
            return;  // Node has already been processed globally
        }
        
        if (recursionStack.contains(node.getUId())) {
            System.out.println("Detected circular dependency on node " + node.getUId() + ". Stopping recursion.");
            return;  // Circular dependency detected in this recursion path
        }

        recursionStack.add(node.getUId());  // Mark node as part of the current recursion stack

        Element element = findElementByUNID(doc, node.getUId());
        if (element != null) {
            NodeList children = element.getElementsByTagName("ID");
            for (int i = 0; i < children.getLength(); i++) {
                String childId = children.item(i).getTextContent();
                TreeNode childNode = nodeMap.get(childId);
                if (childNode != null && !node.getChildren().contains(childNode)) {
                    node.addChild(childNode);
                    connectChildren(childNode, doc);  // Recursively connect children
                }
            }
        }

        recursionStack.remove(node.getUId());  // Remove node from the recursion stack after processing
        processedNodes.add(node.getUId());  // Mark node as fully processed globally
    }

    private static Element findElementByUNID(Document doc, String unid) {
        NodeList elements = doc.getElementsByTagName("*");
        for (int i = 0; i < elements.getLength(); i++) {
            Element element = (Element) elements.item(i);
            NodeList unidNodes = element.getElementsByTagName("UNID");
            if (unidNodes.getLength() > 0 && unidNodes.item(0).getTextContent().equals(unid)) {
                return element;
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        String output = "";
        TreeMap<String, TreeNode> sortedNodeMap = new TreeMap<>(nodeMap);
        
        for (String key : sortedNodeMap.keySet()) {
            if (sortedNodeMap.get(key) != null) {
                output += sortedNodeMap.get(key).toString();
            } else {
                System.out.println("NODE " + key + " IS NULL!!!");
            }
        }

        return output;
    }
}