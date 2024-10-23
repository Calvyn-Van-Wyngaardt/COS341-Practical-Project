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
@SuppressWarnings("all")
public class RecSPLLexer {
  private static final String TOKEN_V = "V_[a-z][a-z0-9]*";
  private static final String TOKEN_F = "F_[a-z][a-z0-9]*";
  private static final String TOKEN_T = "\"[A-Z][a-z]{0,7}\"";
  private static final String TOKEN_N = "-?[0-9]+(\\.[0-9]+)?";
  private static final Set<String> RESERVED_KEYWORDS =
      new HashSet<>(Arrays.asList("main", "begin", "end", "skip", "halt",
                                  "print", "if", "then", "else", "not", "sqrt",
                                  "or", "and", "eq", "grt", "add", "sub", "mul",
                                  "div", "void", "num", "text", "return", "$"));
  private static final Set<String> OPERATORS = new HashSet<>(
      Arrays.asList("=", "+", "-", "*", "/", "(", ")", ",", ";", "{", "}"));

  private static String INPUT_FILE_PATH;
  private static final String OUTPUT_FILE_PATH = "./output/lexerOutput.xml";

  private static int tokenID = 1;
  private static Document xmlDocument;
  private static Element rootElement;

  public static void main(String[] fileContent)
      throws ParserConfigurationException, LexicalException {
    try {
      initializeXML();

      StringBuilder inputBuilder = new StringBuilder();
      for (String line : fileContent) {
        inputBuilder.append(line).append(" ");
      }

      String input = inputBuilder.toString().trim();
      if (!input.startsWith("main")) {
        throw new LexicalException(
            "Input does not start with 'main' as required by the grammar.");
      }

      tokenize(input);

      writeXML(OUTPUT_FILE_PATH);
    } catch (ParserConfigurationException e) {
      throw new ParserConfigurationException("Error initializing XML parser: " +
                                             e.getMessage());
    } catch (LexicalException e) {
      throw new LexicalException("Lexical error: " + e.getMessage());
    }
  }

  private static void initializeXML() throws ParserConfigurationException {
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    xmlDocument = docBuilder.newDocument();
    rootElement = xmlDocument.createElement("TOKENSTREAM");
    xmlDocument.appendChild(rootElement);
  }

  private static void tokenize(String line) throws LexicalException {
    List<String> tokens = new ArrayList<>();
    StringBuilder currentToken = new StringBuilder();
    boolean inQuotes = false;

    for (int i = 0; i < line.length(); i++) {
      char c = line.charAt(i);

      if (c == '"') {
        inQuotes = !inQuotes;
        currentToken.append(c);
      } else if (inQuotes) {
        currentToken.append(c);
      } else if (c == '<' && i + 6 < line.length() &&
                 line.substring(i, i + 7).equals("< input")) {
        if (currentToken.length() > 0) {
          tokens.add(currentToken.toString());
          currentToken = new StringBuilder();
        }
        tokens.add("< input");
        i += 6;
      } else if (Character.isWhitespace(c) || "()<>;,".indexOf(c) != -1) {
        if (currentToken.length() > 0) {
          tokens.add(currentToken.toString());
          currentToken = new StringBuilder();
        }
        if (!Character.isWhitespace(c)) {
          tokens.add(String.valueOf(c));
        }
      } else {
        currentToken.append(c);
      }
    }

    if (currentToken.length() > 0) {
      tokens.add(currentToken.toString());
    }

    for (String token : tokens) {
      if (token.equals("< input")) {
        addTokenToXML("reserved_keyword", token);
      } else if (RESERVED_KEYWORDS.contains(token)) {
        addTokenToXML("reserved_keyword", token);
      } else if (token.matches(TOKEN_V)) {
        addTokenToXML("V", token);
      } else if (token.matches(TOKEN_F)) {
        addTokenToXML("F", token);
      } else if (token.matches(TOKEN_T)) {
        addTokenToXML("T", token);
      } else if (token.matches(TOKEN_N)) {
        addTokenToXML("N", token);
      } else if (OPERATORS.contains(token)) {
        addTokenToXML("operator", token);
      } else {
        throw new LexicalException("Unrecognized token: " + token);
      }
    }
    addTokenToXML("reserved_keyword", "$");
  }

  private static void addTokenToXML(String tokenClass, String word) {
    Element tokenElement = xmlDocument.createElement("TOK");

    Element idElement = xmlDocument.createElement("ID");
    idElement.appendChild(
        xmlDocument.createTextNode(String.valueOf(tokenID++)));
    tokenElement.appendChild(idElement);

    Element classElement = xmlDocument.createElement("CLASS");
    classElement.appendChild(xmlDocument.createTextNode(tokenClass));
    tokenElement.appendChild(classElement);

    Element wordElement = xmlDocument.createElement("WORD");
    wordElement.appendChild(xmlDocument.createTextNode(word));
    tokenElement.appendChild(wordElement);

    rootElement.appendChild(tokenElement);
  }

  private static void writeXML(String outputFilePath) {
    try {
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource source = new DOMSource(xmlDocument);
      StreamResult result = new StreamResult(new File(outputFilePath));
      transformer.transform(source, result);
      System.out.println("Lexing completed successfully");
      System.out.println("XML output written to " + outputFilePath);
    } catch (Exception e) {
      System.err.println("Error writing the XML file: " + e.getMessage());
    }
  }
}

class LexicalException extends Exception {
  public LexicalException(String message) { super(message); }
}
