import java.util.*;

public class ScopeEntry {
    private static List<Integer> scopeHistory = new ArrayList<Integer>();
    private SymbolTable currTable;
    private static Integer id = 0;

    public ScopeEntry(SymbolTable table) {
        this.currTable = table;
        id++;
    }

    public void addScope(Integer level) {
        //scopeLevel.add(level);
        //
    }


}
