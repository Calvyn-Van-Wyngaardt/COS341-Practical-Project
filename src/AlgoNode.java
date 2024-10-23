public class AlgoNode extends SyntaxTreeNode {
  private SyntaxTreeNode instruc;

  public AlgoNode(SyntaxTreeNode instruc) { this.instruc = instruc; }

  @Override
  public boolean typeCheck(NewSymbolTable symbolTable) {
    return instruc != null ? instruc.typeCheck(symbolTable) : true;
  }

  @Override
  public char getType() {
    return 'v';
  }
}
