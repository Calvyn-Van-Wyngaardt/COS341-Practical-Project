import java.util.*;

public class Lexer {
    ArrayList<String> tokens;
    int tokenCount;

    public Lexer(String input) {
        System.out.println("Input:\n" + input);
        this.tokens = tokenizeInput(input);
        this.tokenCount = this.tokens.size();
    }

    private ArrayList<String> tokenizeInput(String input) {
        ArrayList<String> tokens = new ArrayList<>();

        //Reformat string so that all excessive whitespace & newlines are reduced to a single " "
        input = input.replaceAll("\\s+", " ");
        input = input.replaceAll("\\n+", " ");
        String tokenStrings[] = input.split(" ");
        tokens.addAll(Arrays.asList(tokenStrings));

        return tokens;
    }

    public String toString() {
        String tokenSpace = "";
        String tokenString = "";
        int tokenNumber = 1;
        for (String s : this.tokens) {
            tokenString += tokenNumber++ + ": " + s + "\n";
            tokenSpace += s + " ";
        }

        return (String.format("Tokens: %d\n",this.tokenCount) + tokenSpace + "\n=========\n" + tokenString);
    }
}