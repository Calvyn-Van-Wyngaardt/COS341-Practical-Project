import java.io.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Lexer {
    private final ArrayList<Token> tokens;
    private int tokenCount;
    private static final String TOKEN_V = "V_[a-z][a-z0-9]*$";
    private static final String TOKEN_F = "F_[a-z][a-z0-9]*$";
    private static final String TOKEN_T = "[A-Z][a-z]{0,7}";
    private static final String TOKEN_N = "-?[0-9]+(\\.[0-9]+)?";
    private static final Set<String> RESERVED_KEYWORDS = new HashSet<>(
      Arrays.asList("main", "begin", "end", "skip", "halt", "print", "if",
                    "then", "else", "not", "sqrt", "or", "and", "eq", "grt",
                    "add", "sub", "mul", "div", "void", "num", "text", "return"));
    private static final Set<String> OPERATORS = new HashSet<>(
      Arrays.asList("=", "+", "-", "*", "/", "(", ")", ",", ";", "{", "}"));
    
    // private static final String INPUT_FILE_PATH = "lex1_simple.txt";
    private static final String OUTPUT_FILE_PATH = "docs/lexedTokens.xml";

    private static Document xmlDocument;
    private static Element rootElement;

    public Lexer(String[] input) throws LexicalException {
        this.tokenCount = 0;
        this.tokens = tokenizeInput(input);
        createXMLfile();
    }

    private ArrayList<Token> tokenizeInput(String[] input) throws LexicalException {
        ArrayList<Token> inputTokens = new ArrayList<>();

        for (int i = 0; i < input.length; i++) {
            input[i] = input[i].replaceAll("[\\s]+", " ");
            input[i] = input[i].replaceAll("[\\r\\n]+", " ");
            input[i] = input[i].replaceAll(",", " , ");
            input[i] = input[i].replaceAll("[\\(]+", " ( ");
            input[i] = input[i].replaceAll("[\\)]+", " ) ");
            
            String tokenStrings[] = input[i].split(" ");
            
            try {
                for (int j = 0; j < tokenStrings.length; j++) {
                    if (!tokenStrings[j].matches("[\\s]*")) {
                        String tokenValue = tokenStrings[j];
                        String tokenClass;

                        if (tokenStrings[j].contains("<")) {
                            if (j+1 < tokenStrings.length) {
                                if (tokenStrings[j+1].contains("input")) {
                                    tokenValue = "< input";
                                    tokenClass = "user_input";
                                    j++;
                                } else {
                                    throw new LexicalException(String.format("ERROR: Character '<' should be followed user input for the variable name"));
                                }       
                            } else {
                                throw new LexicalException(String.format("ERROR: Character '<' should be followed user input for the variable name"));
                            }
                        } else if (tokenStrings[j].contains("\"")) {
                            int count = tokenStrings[j].length() - tokenStrings[j].replace("\"", "").length();
                            if (count != 2) {
                                throw new LexicalException(String.format("ERROR: %s not properly closed with appropriate \"\" symbols", tokenStrings[j]));
                            }
                            tokenClass = "string_token";
                        } else if (tokenStrings[j].matches(TOKEN_V)) {
                            tokenClass = "variable";
                        } else if (tokenStrings[j].matches(TOKEN_F)) {
                            tokenClass = "function";
                        } else if (tokenStrings[j].matches(TOKEN_T)) {
                            tokenClass = "string_token";
                        } else if (tokenStrings[j].matches(TOKEN_N)) {
                            tokenClass = "number_token";
                        } else if (RESERVED_KEYWORDS.contains(tokenStrings[j])) {
                            tokenClass = "reserved_keyword";
                        } else if (OPERATORS.contains(tokenStrings[j])) {
                            tokenClass = "operator";
                        }
                        else {
                            throw new LexicalException(String.format("ERROR: \"%s\" not a valid token for specified grammar", tokenStrings[j]));
                        }

                        inputTokens.add(new Token(tokenClass, tokenValue, ++this.tokenCount));
                    }
                }
            } catch (Error err) {
                System.out.println(err);
            }
        }

         if (!inputTokens.get(0).getValue().equals("main")) {
            throw new LexicalException("Input does not start with \"main\" as required by the grammar.");
        }

        return inputTokens;
    }

    private boolean createXMLfile() {
        try {
            initializeXML();
            for (Token t : tokens) {
                addTokenToXML(t);
            }
            writeXML(OUTPUT_FILE_PATH);
            
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return false;
        }
        
        return true;
    }

    private static void initializeXML() throws ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        xmlDocument = docBuilder.newDocument();
        rootElement = xmlDocument.createElement("TOKENSTREAM");
        xmlDocument.appendChild(rootElement);
    }

    private static void writeXML(String outputFilePath) {
        try {
          TransformerFactory transformerFactory = TransformerFactory.newInstance();
          Transformer transformer = transformerFactory.newTransformer();
          DOMSource source = new DOMSource(xmlDocument);
          StreamResult result = new StreamResult(new File(outputFilePath));
          transformer.transform(source, result);
          System.out.println("XML output written to " + outputFilePath);
        } catch (Exception e) {
          System.err.println("Error writing the XML file: " + e.getMessage());
        }
      }

    private static void addTokenToXML(Token t) {
        Element tokenElement = xmlDocument.createElement("TOK");
        Element idElement = xmlDocument.createElement("ID");
            idElement.appendChild(
                xmlDocument.createTextNode(String.valueOf(t.getId())));
        tokenElement.appendChild(idElement);
        Element classElement = xmlDocument.createElement("CLASS");
            classElement.appendChild(xmlDocument.createTextNode(t.getClassName()));
            tokenElement.appendChild(classElement);
        Element wordElement = xmlDocument.createElement("WORD");
            wordElement.appendChild(xmlDocument.createTextNode(t.getValue()));
            tokenElement.appendChild(wordElement);

        rootElement.appendChild(tokenElement);
    }

    @Override
    public String toString() {
        String tokenString = "";
        for (Token s : this.tokens) {
            tokenString += s.toString() + "\n";
        }

        return (String.format("Tokens: %d\n", this.tokenCount) + "\n=========\n" + tokenString);
    }
}