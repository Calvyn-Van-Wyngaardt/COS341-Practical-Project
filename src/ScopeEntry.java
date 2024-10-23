
public class ScopeEntry {
    private ScopeEntry prev;
    private ScopeEntry next;
    private SymbolTable currTable;
    private static Integer id = 0;
    private boolean existingStill;

    public ScopeEntry(SymbolTable table) {
        this.currTable = table;
        prev = null;
        next = null;
        id++;
        existingStill = true;
    }

    public void setNext(ScopeEntry next) {
        this.next = next;
    }

    public void setSymbolTable(SymbolTable table) {
        this.currTable = table;
    }

    public void setExisting(boolean existing) {
        existingStill = existing;
    }

    public SymbolTable getSymbolTable() {
        return currTable;
    }

    public void setPrev(ScopeEntry prev) {
        this.prev = prev;
    }

    public ScopeEntry getNext() {
        return next;
    }

    public boolean isExisting() {
        return existingStill;
    }

    public ScopeEntry getPrev() {
        return prev;
    }

    public void checkSymbolTable() {
        currTable.setSymbolTable(currTable.checkSymbolTable());
    }

    @Override
    public String toString() {
        return currTable.toString();
    }
}
