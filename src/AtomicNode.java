public class AtomicNode extends SyntaxTreeNode {
  private String value;
  private char type;

  public AtomicNode(String value, char type) {
    this.value = value;
    this.type = type;
  }

  @Override
  public boolean typeCheck(NewSymbolTable symbolTable) {
    boolean result = true;

    if (type == 'v') {
      if (!symbolTable.contains(value)) {
        System.err.println("Error: Variable '" + value + "' is not declared.");
        result = false;
      } else {
        this.type = symbolTable.getType(value);
      }
    }

    return result;
  }

  @Override
  public char getType() {
    return type;
  }
}
