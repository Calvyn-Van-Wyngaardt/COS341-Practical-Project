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
        TableEntry newEntry = new TableEntry(name, value, value, type);
        if (value.matches(TOKEN_V)) {
            newEntry.setInternalName(renameVar());
        } else if (value.matches(TOKEN_F)) {
            newEntry.setInternalName(renameFun());
        }

        return symbolTable.put(id++, newEntry);
    }

    public void setEntry(Integer id, TableEntry value) {
        symbolTable.replace(id, value);
    }

    //TODO: Lookup - See if function has been declared (check duplicates)
    //TODO: Lookup - Variable reassignment
    //TODO: Ensure that the final DS (LinkedList) is correct:

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

            //Declarations...
            if ((i+1) < entriesArray.length && entriesArray[i].getValue().equals("void")) {
                if (entriesArray[i+1].getValue().matches(TOKEN_F)) {
                    //Merge
                    newEntry = new TableEntry(entriesArray[i+1].getId(), String.format("%s %s", entriesArray[i].getRepresentation(), entriesArray[i+1].getRepresentation()), entriesArray[i+1].getValue(), "function_declaration");
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
                        newEntry = new TableEntry(entriesArray[i+1].getId(), String.format("%s %s", entriesArray[i].getRepresentation(), entriesArray[i+1].getRepresentation()), entriesArray[i+1].getValue(),"variable_declaration");
                        newEntry.setInternalName(entriesArray[i+1].getInternalName());
                        i++;
                    } 
                } else {
                    throw new Error(String.format("ERROR: Symbol Table cannot have empty %s declaration...", entriesArray[i].getRepresentation()));
                }
            } else if ((i+1) < entriesArray.length && entriesArray[i].getValue().matches(TOKEN_V) && entriesArray[i+1].getValue().equals("input")) {
                newEntry = new TableEntry(entriesArray[i+1].getId(), String.format("%s %s", entriesArray[i].getRepresentation(), entriesArray[i].getRepresentation()), entriesArray[i+1].getValue(), "user_input");
                newEntry.setInternalName(entriesArray[i].getInternalName());
                i++;
            } else if ((i+2) < symbolTable.size() && entriesArray[i].getValue().matches(TOKEN_V) && entriesArray[i+1].getValue().equals("=")) {
                //Modify value of variable...
                String type = "";
                if (entriesArray[i+2].getValue().matches(TOKEN_T)) {
                    type = "text";
                } else if (entriesArray[i+2].getValue().matches(TOKEN_N)) {
                    type = "num";
                }

                newEntry = new TableEntry(entriesArray[i].getId(), entriesArray[i].getValue(), entriesArray[i+2].getValue(), type);
                newEntry.setInternalName(entriesArray[i].getInternalName());
                i += 2;    
            }
            // else {
            //     throw new Error(String.format("ERROR: Dangling %s without Function/variable name", entriesArray[i].getRepresentation()));
            // }
            


            newTable.put(i, newEntry);
        }

        return newTable;
    }

    public Map<Integer, TableEntry> getSymbolTable() {
        return symbolTable;
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
    private final String id;
    private String value;
    private String representation;
    private final String type;
    private String internalName;

    public TableEntry(String id, String representation, String value, String type) {
        this.id = id;
        this.value = value;
        this.representation = representation;
        this.type = type;
    }

    public void setInternalName(String name) {
        this.internalName = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setRepresentation(String rep) {
        this.representation = rep;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getRepresentation() {
        return representation;
    }

    public String getInternalName() {
        return internalName;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("\t%14s %14s %18s %25s %14s", id, representation, value, type, internalName);
    }
}