import java.util.*;

class ScopeChecker {
    private LinkedList<ScopeEntry> scope = new LinkedList<>();       //Final Data structure used for Type checking
    private Stack<SymbolTable> tempStack = new Stack<>();            //Temp Data structure used to iterate through program
    private static Integer stackNumber = 0;                         //Keep track of where I am in linked list
    
    public void enterScope() {
        tempStack.push(new SymbolTable());
        ScopeEntry newEntry = new ScopeEntry(new SymbolTable());
        scope.add(newEntry);
        
        if (scope.size() > 1) {
            // if (scope.peek().getSymbolTable().getSymbolTable().isEmpty()) {
            //     scope.pop();
            //     stackNumber -= 2;
            // } else {
                ScopeEntry prev = scope.get(stackNumber-1);
                
                //Set previous next
                prev.setNext(newEntry);
                //Set current prev
                newEntry.setPrev(prev);
                newEntry.setNext(null);
                
                scope.set(stackNumber-1, prev);
                scope.set(stackNumber, newEntry);
            // }

        }

        stackNumber++;
    }

    public String printFinalTables() {
        String out = "";
        for (ScopeEntry se : scope) {
            out += "\n$$$$$$$ - Scope Entry - $$$$$$$\n" + se.toString();
        }

        return out;
    }

    public void exitScope() {
        //Before exiting scope... Print that SymbolTable...
        SymbolTable currTable = tempStack.peek();
        System.out.println(currTable);

        if (!tempStack.isEmpty()) {
            if (!tempStack.peek().getSymbolTable().isEmpty()) {
                stackNumber--;
            }
            
            tempStack.pop();
            //Add statement for final DS
        }
    }

    public void checkSymbolTables() {
        for (ScopeEntry s : scope) {
            s.checkSymbolTable();
        }
    }


    public Stack<SymbolTable> getTempStack() {
        return tempStack;
    }

    public void addSymbol(String name, String value, String type) {
        if (!tempStack.isEmpty()) {
            SymbolTable newTable = tempStack.pop();
            ScopeEntry se = scope.get(stackNumber-1);

            newTable.addSymbol(name, value, type);
            se.setSymbolTable(newTable);
            
            if (scope.size() > 1) {
                scope.set(stackNumber-1, se);
                scope.get(stackNumber-2).setNext(scope.get(stackNumber-1));
                scope.get(stackNumber-1).setPrev(scope.get(stackNumber-2));
            }

            tempStack.push(newTable);
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