import java.util.*;

public class SymbolTable {
    private Map<Integer, TableEntry> symbolTable;
    private static Integer scopeNumber = 0;
    private static Integer id = 0;
    private static int varCount = 0;
    private static int funCount = 0;
    private static final String TOKEN_V = "V_[a-z][a-z0-9]*$";
    private static final String TOKEN_F = "F_[a-z][a-z0-9]*$";
    private static final String TOKEN_T = "[A-Z][a-z]{0,7}";
    private static final String TOKEN_N = "-?[0-9]+(\\.[0-9]+)?";

    public SymbolTable() {
        this.symbolTable = new HashMap<Integer, TableEntry>();
        scopeNumber++;
    }

    public void setSymbolTable(Map<Integer, TableEntry> map) {
        symbolTable.clear();
        symbolTable.putAll(map);
    }

    public TableEntry addSymbol(String name, String value, String type) {
        TableEntry newEntry = new TableEntry(name, value, type);
        if (value.matches(TOKEN_V)) {
            newEntry.setInternalName(renameVar());
        } else if (value.matches(TOKEN_F)) {
            newEntry.setInternalName(renameFun());
        }

        return symbolTable.put(id++, newEntry);
    }

    //TODO: Lookup - See if var/function has been declared
    //TODO: Lookup - Variable reassignment
    //TODO: Ensure that the final DS (LinkedList) is correct:
        //TODO: Create a print function for the LinkedList

    public Map<Integer, TableEntry> checkSymbolTable() throws Error {
        int i = 0;
        Collection<TableEntry> entries = symbolTable.values();
        TableEntry entriesArray[] = new TableEntry[symbolTable.size()];
        Map<Integer, TableEntry> newTable = new HashMap<Integer, TableEntry>();

        for (TableEntry t : symbolTable.values()) {
            entriesArray[i++] = t;
        }

        for (i = 0; i < entriesArray.length; i++) {
            TableEntry newEntry = entriesArray[i];
            System.out.println("CURRENT ENTRY: " + entriesArray[i].getValue());
            if ((i+1) < entriesArray.length && entriesArray[i].getValue().equals("void")) {
                if (entriesArray[i+1].getValue().matches(TOKEN_F)) {
                    //Merge
                    newEntry = new TableEntry(entriesArray[i+1].getName(), String.format("%s %s", entriesArray[i].getValue(), entriesArray[i+1].getValue()) , "function declaration");
                    newEntry.setInternalName(entriesArray[i+1].getInternalName());
                    i++;
                } else {
                    throw new Error(String.format("ERROR: \"void\" type cannot be declared without function name"));
                }
            }
            else if ((i+1) < entriesArray.length && entriesArray[i].getValue().equals("num") || (i+1) < entriesArray.length && entriesArray[i].getValue().equals("text")) {
                if (i+1 < symbolTable.size()) {
                    if (entriesArray[i+1].getValue().matches(TOKEN_V)) {
                        //Merge
                        newEntry = new TableEntry(entriesArray[i+1].getName(), String.format("%s %s", entriesArray[i].getValue(), entriesArray[i+1].getValue()), "variable declaration");
                        newEntry.setInternalName(entriesArray[i+1].getInternalName());
                        i++;
                    } 
                } else {
                    throw new Error(String.format("ERROR: Symbol Table cannot have empty %s declaration...", entriesArray[i].getName()));
                }
            }
            // else {
            //     throw new Error(String.format("ERROR: Dangling %s without Function/variable name", entriesArray[i].getName()));
            // }

            newTable.put(i, newEntry);
        }

        return newTable;
    }

    public void modifyEntry(String oldName, String newValue, ScopeChecker scopeChecker) {
        Stack<SymbolTable> tempStack = scopeChecker.getTempStack();
        
        // Traverse the stack from the top (most recent scope) to the bottom
        for (int i = tempStack.size() - 1; i >= 0; i--) {
            SymbolTable currentScope = tempStack.get(i);
            for (TableEntry entry : currentScope.symbolTable.values()) {
                if (entry.getName().equals(oldName)) {
                    entry.setValue(newValue);  // Modify the existing value
                    return;  // Exit after modifying
                }
            }
        }
        System.out.println("No entry found for modification");
    }

    public Map<Integer, TableEntry> getSymbolTable() {
        return symbolTable;
    }

    public String lookup(String name) {
        //to be implemented
        return "Hi";
    }

    //Variables don't need to follow the grammer rules as this is internal renaming
    public String renameVar() {
        return String.format("V_%06d", varCount++);
    }

    //Same with functions
    public String renameFun() {
        return String.format("%06d", funCount++);
    }

    @Override
    public String toString() {
        String out = String.format("=====SYMBOL TABLE=====\n");
        
        for (TableEntry t : symbolTable.values()) {
            out += t.toString() + "\n";
        }
        out += "====== END ======\n";

        return out;
    }
}

class TableEntry {
    private final String oldName;
    private String value;
    private final String type;
    private String internalName;

    public TableEntry(String name, String value, String type) {
        this.oldName = name;
        this.value = value;
        this.type = type;
    }

    public void setInternalName(String name) {
        this.internalName = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return oldName;
    }

    public String getInternalName() {
        return internalName;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("\t%s\t%s\t%s\t%s", oldName, internalName, value, type);
    }
}


// TableEntry lastEntry = symbolTable.get(symbolTable.size());
//         if (lastEntry != null) {
//             String lastEntryName = lastEntry.getName();
//             String lastEntryValue = lastEntry.getValue();

//             //Merge entries into one
//             if (name.matches(TOKEN_V)) {
//                 //Declaration
//                 if (lastEntryName.equals("num") || lastEntryName.equals("text")) {
//                     symbolTable.remove(symbolTable.size() - 1);
//                     TableEntry mergedEntry = new TableEntry(name, lastEntryValue, lastEntryName);
//                     mergedEntry.setInternalName(renameVar());
//                     symbolTable.put(id, mergedEntry);
//                 }
//                 //Change the value...
//                 else {
                    
//                 }
//             } else if (name.matches(TOKEN_F)) {
//                 if (lastEntryName.equals("void") || lastEntryName.equals("text") || lastEntryName.equals("num")) {

//                 }           
//             }
    
//             for (TableEntry entry : symbolTable.values()) {
//                 if (entry.getName().equals(name)) {
//                     return entry.getValue();
//                 }
//             }
//         }
//         return null;