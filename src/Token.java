public class Token {
    private final String className;
    private final String value;
    private final int id;

    public Token(String className, String value, int id) {
        this.className = className;
        this.value = value;
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("%d: %s - %s", id, value, className);
    }
}