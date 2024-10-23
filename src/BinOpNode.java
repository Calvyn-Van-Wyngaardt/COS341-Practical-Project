public class BinOpNode extends SyntaxTreeNode {
  private String operator;
  private SyntaxTreeNode left;
  private SyntaxTreeNode right;
  private char type;

  public BinOpNode(String operator, SyntaxTreeNode left, SyntaxTreeNode right) {
    this.operator = operator;
    this.left = left;
    this.right = right;
  }

  @Override
  public boolean typeCheck(NewSymbolTable symbolTable) {
    boolean result = true;

    if (!left.typeCheck(symbolTable)) {
      result = false;
    }
    if (!right.typeCheck(symbolTable)) {
      result = false;
    }

    char leftType = left.getType();
    char rightType = right.getType();

    switch (operator) {
    case "add":
    case "sub":
    case "mul":
    case "div":
      if (leftType == 'n' && rightType == 'n') {
        type = 'n';
      } else {
        System.err.println("Error: Operator '" + operator +
                           "' requires operands of type 'num'.");
        result = false;
      }
      break;
    case "or":
    case "and":
      if (leftType == 'b' && rightType == 'b') {
        type = 'b';
      } else {
        System.err.println("Error: Operator '" + operator +
                           "' requires operands of type 'bool'.");
        result = false;
      }
      break;
    case "eq":
    case "grt":
      if (leftType == 'n' && rightType == 'n') {
        type = 'b';
      } else {
        System.err.println("Error: Operator '" + operator +
                           "' requires operands of type 'num'.");
        result = false;
      }
      break;
    default:
      System.err.println("Error: Unknown operator '" + operator + "'.");
      result = false;
      break;
    }

    return result;
  }

  @Override
  public char getType() {
    return type;
  }
}
