public class ProgNode extends SyntaxTreeNode {
  private SyntaxTreeNode globVars;
  private SyntaxTreeNode algo;
  private SyntaxTreeNode functions;

  public ProgNode(SyntaxTreeNode globVars, SyntaxTreeNode algo,
                  SyntaxTreeNode functions) {
    this.globVars = globVars;
    this.algo = algo;
    this.functions = functions;
  }

  @Override
  public boolean typeCheck(NewSymbolTable symbolTable) {
    boolean result = true;

    if (globVars != null) {
      System.out.println("Type checking GlobVarsNode...");
      if (!globVars.typeCheck(symbolTable)) {
        result = false;
      }
    }

    if (algo != null) {
      System.out.println("Type checking AlgoNode...");
      if (!algo.typeCheck(symbolTable)) {
        result = false;
      }
    }

    if (functions != null) {
      System.out.println("Type checking FunctionsNode...");
      if (!functions.typeCheck(symbolTable)) {
        result = false;
      }
    }

    return result;
  }

  @Override
  public char getType() {
    return 'v';
  }
}
