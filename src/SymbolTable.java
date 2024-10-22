import java.util.*;

public class SymbolTable {
    private Map<Integer, TableEntry> symbolTable;
    private static Integer scopeNumber = 0;
    private static Integer id = 0;

    public SymbolTable() {
        this.symbolTable = new HashMap<Integer, TableEntry>();
        scopeNumber++;
    }

    public TableEntry addSymbol(String name, String value, String type) {
        return symbolTable.put(id++, new TableEntry(name, value, type));
    }

    public String lookup(String name) {
        for (TableEntry entry : symbolTable.values()) {
            if (entry.getName().equals(name)) {
                return entry.getValue();
            }
        }
        return null;
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
    private final String name;
    private final String value;
    private final String type;

    public TableEntry(String name, String value, String type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("\t%s\t%s\t%s", name, type, value);
    }
}