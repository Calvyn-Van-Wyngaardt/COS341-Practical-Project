public class Main {
    public static void main(String[] args) {
        String input = "main\n\t\tbegin\t skip\t\nend\t\n";
        Lexer lexer = new Lexer(input);
        System.out.println(lexer);

    }
}