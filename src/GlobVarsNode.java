import java.util.List;

public class GlobVarsNode extends SyntaxTreeNode {
  private List<VariableDeclaration> declarations;

  public GlobVarsNode(List<VariableDeclaration> declarations) {
    this.declarations = declarations;
  }

  @Override
  public boolean typeCheck(NewSymbolTable symbolTable) {
    for (VariableDeclaration decl : declarations) {
      char type = decl.getType();
      String name = decl.getName();
      if (symbolTable.contains(name)) {
        System.err.println("Variable " + name + " already declared.");
        return false;
      }
      symbolTable.add(name, type);
    }
    return true;
  }

  @Override
  public char getType() {
    return 'v';
  }
}
