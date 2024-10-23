import java.util.List;

public class InstrucNode extends SyntaxTreeNode {
  private List<SyntaxTreeNode> commands;

  public InstrucNode(List<SyntaxTreeNode> commands) {
    this.commands = commands;
  }

  @Override
  public boolean typeCheck(NewSymbolTable symbolTable) {
    for (SyntaxTreeNode command : commands) {
      if (!command.typeCheck(symbolTable)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public char getType() {
    return 'v';
  }
}
