public class PrintNode extends SyntaxTreeNode {
  private SyntaxTreeNode atomic;

  public PrintNode(SyntaxTreeNode atomic) { this.atomic = atomic; }

  @Override
  public boolean typeCheck(NewSymbolTable symbolTable) {
    if (!atomic.typeCheck(symbolTable)) {
      return false;
    }
    char atomicType = atomic.getType();
    if (atomicType != 'n' && atomicType != 't') {
      System.err.println(
          "Error: Print statement expects 'num' or 'text' type.");
      return false;
    }
    return true;
  }

  @Override
  public char getType() {
    return 'v';
  }
}
