public class VariableDeclaration {
  private char type;
  private String name;

  public VariableDeclaration(char type, String name) {
    this.type = type;
    this.name = name;
  }

  public boolean typeCheck(NewSymbolTable symbolTable) {
    if (symbolTable.contains(name)) {
      System.err.println("Error: Variable '" + name + "' already declared.");
      return false;
    }
    symbolTable.add(name, type);
    return true;
  }

  public char getType() { return type; }

  public String getName() { return name; }
}
