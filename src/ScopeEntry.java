
public class ScopeEntry {
    private ScopeEntry prev;
    private ScopeEntry next;
    private SymbolTable currTable;
    private static Integer id = 0;

    public ScopeEntry(SymbolTable table) {
        this.currTable = table;
        prev = null;
        next = null;
        id++;
    }

    public void setNext(ScopeEntry next) {
        this.next = next;
    }

    public void setSymbolTable(SymbolTable table) {
        this.currTable = table;
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
