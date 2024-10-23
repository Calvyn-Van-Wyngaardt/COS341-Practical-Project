public abstract class SyntaxTreeNode {
  public abstract boolean typeCheck(NewSymbolTable symbolTable);
  public abstract char getType();
}
