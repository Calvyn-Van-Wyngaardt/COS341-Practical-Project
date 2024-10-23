public class AssignNode extends SyntaxTreeNode {
  private String varName;
  private SyntaxTreeNode expression;
  private boolean isInput;

  public AssignNode(String varName, SyntaxTreeNode expression,
                    boolean isInput) {
    this.varName = varName;
    this.expression = expression;
    this.isInput = isInput;
  }

  @Override
  public boolean typeCheck(NewSymbolTable symbolTable) {
    boolean result = true;

    if (!symbolTable.contains(varName)) {
      System.err.println("Error: Variable '" + varName + "' is not declared.");
      result = false;
    } else {
      char varType = symbolTable.getType(varName);

      if (isInput) {
        if (varType != 'n') {
          System.err.println("Error: Variable '" + varName +
                             "' must be of type 'num' for input.");
          result = false;
        }
      } else {
        if (!expression.typeCheck(symbolTable)) {
          result = false;
        } else {
          char exprType = expression.getType();
          if (varType != exprType) {
            System.err.println("Error: Type mismatch. Cannot assign '" +
                               exprType + "' to '" + varType + "'.");
            result = false;
          }
        }
      }
    }

    return result;
  }

  @Override
  public char getType() {
    return 'v';
  }
}
