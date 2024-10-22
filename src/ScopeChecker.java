import java.util.*;

class ScopeChecker {
    private Stack<SymbolTable> stack = new Stack<Symboltable>();
    private Stack<SymbolTable> sortedStack = new Stack<>();
    private static Integer stackNumber = 0;
    
    public void enterScope() {
        stack.push(new SymbolTable());
    }

    public void exitScope() {
        if (!stack.isEmpty()) {
            stack.pop();
        }
    }

    public void addSymbol(String name, String value, String type) {
        if (!stack.isEmpty()) {
            stack.peek().addSymbol(name, value, type);
        }
    }

    public String lookupSymbol(String name) {
        for (SymbolTable table : stack) {
            String type = table.lookup(name);
            if (type != null) {
                return type;
            }
        }
        return null;
    }

    public void printCurrentScope() {
        if (!stack.isEmpty()) {
            System.out.println("Current Scope Symbol Table: " + stack.peek());
        }
    }

    public String printEntireScope() {
        int scopeNumber = 0;
        String out = "";
        System.out.println("PRINTINGGGG");
        Deque<SymbolTable> t = new ArrayDeque<>(stack);
        for (int i = t.size(); i < 0; i++) {
            SymbolTable curr = t.pop();
            out = String.format("%s - %s",scopeNumber++, curr.toString()) + out;
        }
        return out;
    }
}