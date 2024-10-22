import java.util.*;

class ScopeChecker {
    private Stack<ScopeEntry> scope = new Stack<>();                //Final Data structure used for Type checking
    private Stack<SymbolTable> tempStack = new Stack<>();           //Temp Data structure used to iterate through program
    private static Integer stackNumber = 0;
    
    public void enterScope() {
        tempStack.push(new SymbolTable());
        //Add statement for final DS
    }

    public void exitScope() {
        //Before exiting scope... Print that SymbolTable...
        SymbolTable currTable = tempStack.peek();
        System.out.println(currTable);

        if (!tempStack.isEmpty()) {
            tempStack.pop();
            //Add statement for final DS
        }
    }

    public void addSymbol(String name, String value, String type) {
        if (!tempStack.isEmpty()) {
            SymbolTable newTable = tempStack.pop();
            newTable.addSymbol(name, value, type);
            tempStack.push(newTable);
            //Add statement for final DS
        }
    }

    public String lookupSymbol(String name) {
        //Add statement for final DS ?

        for (int i = tempStack.size(); i > 0; i--) {
            SymbolTable curr = tempStack.get(i);
            String type = curr.lookup(name);
            if (type != null) {
                return type;
            }
        }
        return null;
    }

    public void printCurrentScope() {
        if (!scope.isEmpty()) {
            System.out.println("Current Scope Symbol Table: " + scope.peek());
        }
    }
}