public class BranchNode extends SyntaxTreeNode {
  private SyntaxTreeNode condition;
  private SyntaxTreeNode thenAlgo;
  private SyntaxTreeNode elseAlgo;

  public BranchNode(SyntaxTreeNode condition, SyntaxTreeNode thenAlgo,
                    SyntaxTreeNode elseAlgo) {
    this.condition = condition;
    this.thenAlgo = thenAlgo;
    this.elseAlgo = elseAlgo;
  }

  @Override
  public boolean typeCheck(NewSymbolTable symbolTable) {
    boolean result = true;

    if (!condition.typeCheck(symbolTable)) {
      result = false;
    }
    if (condition.getType() != 'b') {
      System.err.println("Error: Condition does not evaluate to 'bool'.");
      result = false;
    }

    if (thenAlgo != null && !thenAlgo.typeCheck(symbolTable)) {
      result = false;
    }
    if (elseAlgo != null && !elseAlgo.typeCheck(symbolTable)) {
      result = false;
    }

    return result;
  }

  @Override
  public char getType() {
    return 'v';
  }
}
