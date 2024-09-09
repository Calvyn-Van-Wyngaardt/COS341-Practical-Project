import java.util.*;

public class Lexer {
    ArrayList<String> tokens;
    int tokenCount;

    public Lexer(String[] input) {
        System.out.println("Input:\n" + input);
        this.tokens = tokenizeInput(input);
        this.tokenCount = this.tokens.size();
    }

    private ArrayList<String> tokenizeInput(String[] input) {
        ArrayList<String> tokens = new ArrayList<>();

        // Reformat string so that all excessive whitespace & newlines are reduced to a single " "
        // Additional checks are added to cater for all platforms
        for (int i = 0; i < input.length; i++) {
            input[i] = input[i].replaceAll("[\\s]+", " ");
            input[i] = input[i].replaceAll("[\\r\\n]+", " ");
            input[i] = input[i].replaceAll(",", " , ");
            input[i] = input[i].replaceAll("[\\(]+", " ( ");
            input[i] = input[i].replaceAll("[\\)]+", " ) ");
            
            String tokenStrings[] = input[i].split(" ");
            
            for (String s : tokenStrings) {
                if (!s.matches("[\\s]*")) {
                    tokens.add(s);
                }
            }
        }

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