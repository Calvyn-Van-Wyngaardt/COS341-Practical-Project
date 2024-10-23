import java.util.*;

class ScopeChecker {
    private LinkedList<ScopeEntry> scope = new LinkedList<>();       //Final Data structure used for Type checking
    private Stack<SymbolTable> tempStack = new Stack<>();            //Temp Data structure used to iterate through program
    private static Integer stackNumber = 0;                         //Keep track of where I am in linked list
    private static final String TOKEN_V = "V_[a-z][a-z0-9]*$";
    private static final String TOKEN_F = "F_[a-z][a-z0-9]*$";
    private static final String TOKEN_T = "[A-Z][a-z]{0,7}";
    private static final String TOKEN_N = "-?[0-9]+(\\.[0-9]+)?";

    public void enterScope() {
        tempStack.push(new SymbolTable());
        ScopeEntry newEntry = new ScopeEntry(new SymbolTable());
        scope.add(newEntry);
        
        if (scope.size() > 1) {
            ScopeEntry prev = scope.get(stackNumber-1);
            
            //Set previous next
            prev.setNext(newEntry);
            //Set current prev
            newEntry.setPrev(prev);
            newEntry.setNext(null);
            
            scope.set(stackNumber-1, prev);
            scope.set(stackNumber, newEntry);
        }

        stackNumber++;
    }

    public String printFinalTables() {
        String out = "";
        int i = 0;
        for (ScopeEntry se : scope) {
            out += String.format("\n$$$$$$$ - Scope Entry (%s) %d - $$$$$$$\n\t\t[ID]\t[Representation]\t[Value]%10s\t[Type]\t\t[Internal Name]\n%s", se.isExisting(), i++, "", se.toString());
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
                scope.get(stackNumber).setExisting(false);
            }
            
            tempStack.pop();
            //Add statement for final DS
        }
    }


    //Do the V_abc input remapping...
    public void checkSymbolTables() {
        for (ScopeEntry s : scope) {
            s.checkSymbolTable();
        }
    }

    public boolean lookups() throws Error {
        LinkedList<ScopeEntry> tempScope = new LinkedList<ScopeEntry>(scope);
        for (int i = 0; i < tempScope.size(); i++) {
            ScopeEntry currScope = tempScope.get(i);
            Map<Integer, TableEntry> currTable = currScope.getSymbolTable().getSymbolTable();
            for (TableEntry t : currTable.values()) {
                String[] value = t.getRepresentation().split(" ");
                
                //Var decl
                if (value.length > 1) {
                    if (value[1].matches(TOKEN_V)) {
                        //Search from the current scope backwards to check for duplicate decl...

                        for (int k = i; k > 0; k--) {
                            ScopeEntry curr = scope.get(k);
                            if (!curr.isExisting() && !curr.getSymbolTable().getSymbolTable().isEmpty()) {
                                Collection<TableEntry> tableEntries = curr.getSymbolTable().getSymbolTable().values();
                                TableEntry entriesArray[] = new TableEntry[tableEntries.size()];
    
                                int z = 0;
                                for (TableEntry t1 : tableEntries) {
                                    entriesArray[z++] = t1;
                                }
                                
                                int varCount = 0;
                                for (TableEntry ea : entriesArray) {
                                    if (ea.getRepresentation().contains(value[0]) && ea.getRepresentation().contains(value[1])) {
                                        if (varCount > 1) {
                                            throw new Error(String.format("ERROR: Duplicate declaration found for %s",value[1]));
                                        } else {
                                            varCount++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                } 
                //Var init
                //TODO: Ensure you can't have 2 entries within same scope
                //TODO: Ensure that the var actually changes in the correct scope
                else if (value[0].matches(TOKEN_V)) {
                    boolean found = false;
                    boolean skip = false;
                    for (int k = i; k > 0; k--) {
                        skip = false;
                        ScopeEntry curr = scope.get(k);
                        if (!curr.isExisting() && !curr.getSymbolTable().getSymbolTable().isEmpty()) {
                            Collection<TableEntry> tableEntries = curr.getSymbolTable().getSymbolTable().values();
                            TableEntry entriesArray[] = new TableEntry[tableEntries.size()];
    
                            int z = 0;
                            for (TableEntry t1 : tableEntries) {
                                entriesArray[z++] = t1;
                            }
    
                            for (int l = 0; l < entriesArray.length; l++) {
                                String[] rep = entriesArray[l].getRepresentation().split(" ");
                                for (String s : rep) {
                                    System.out.println(s);
                                }

                                //Found the variable declaration, change the value...
                                if (rep.length > 1) {
                                    //Needs to be modified...
                                    if (rep[1].equals(value[0])) {
                                        SymbolTable st = scope.get(k).getSymbolTable();
                                        TableEntry newEntry = new TableEntry(entriesArray[l].getId(), entriesArray[l].getRepresentation(), value[0], "variable_declaration");
                                        st.setEntry(l, newEntry);
                                        
                                        ScopeEntry newScopeEntry = scope.get(k);
                                        newScopeEntry.setSymbolTable(st);
                                        
                                        scope.set(k, newScopeEntry);
                                        found = true;
                                        k = 0;
                                    }
                                }
                            }
                        } else {
                            skip = true;
                        }

                        scope.set(k, curr);
                    }

                    if (!found && !skip) {
                        throw new Error(String.format("ERROR: Could not find declaration for variable"));
                    } 
                }
            }
        }

        return false;
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

    // public String lookupSymbol(String name) {
    //     //Add statement for final DS ?

    //     for (int i = tempStack.size(); i > 0; i--) {
    //         SymbolTable curr = tempStack.get(i);
    //         String type = curr.lookup(name);
    //         if (type != null) {
    //             return type;
    //         }
    //     }
    //     return null;
    // }

    public void printCurrentScope() {
        if (!scope.isEmpty()) {
            System.out.println("Current Scope Symbol Table: " + scope.peek());
        }
    }
}