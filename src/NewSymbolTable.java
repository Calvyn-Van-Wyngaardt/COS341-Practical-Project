import java.util.HashMap;
import java.util.Map;

public class NewSymbolTable {
  private Map<String, Character> table = new HashMap<>();

  public void add(String identifier, char type) { table.put(identifier, type); }

  public Character getType(String identifier) { return table.get(identifier); }

  public boolean contains(String identifier) {
    return table.containsKey(identifier);
  }

  public void print() {
    System.out.println("Symbol Table Contents:");
    for (Map.Entry<String, Character> entry : table.entrySet()) {
      String typeStr = typeCharToString(entry.getValue());
      System.out.println("Variable Name: " + entry.getKey() +
                         ", Type: " + typeStr);
    }
  }

  private String typeCharToString(char type) {
    switch (type) {
    case 'n':
      return "num";
    case 't':
      return "text";
    case 'b':
      return "bool";
    case 'v':
      return "void";
    default:
      return "unknown";
    }
  }
}
