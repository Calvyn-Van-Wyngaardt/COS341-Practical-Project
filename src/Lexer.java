import java.util.*;

public class Lexer {
    private final ArrayList<Token> tokens;
    private int tokenCount;

    public Lexer(String[] input) {
        this.tokenCount = 0;
        this.tokens = tokenizeInput(input);
    }

    private ArrayList<Token> tokenizeInput(String[] input) {
        ArrayList<Token> inputTokens = new ArrayList<>();

        // Reformat string so that all excessive whitespace & newlines are reduced to a single " "
        // Additional checks are added to cater for all platforms
        for (int i = 0; i < input.length; i++) {
            input[i] = input[i].replaceAll("[\\s]+", " ");
            input[i] = input[i].replaceAll("[\\r\\n]+", " ");
            input[i] = input[i].replaceAll(",", " , ");
            input[i] = input[i].replaceAll("[\\(]+", " ( ");
            input[i] = input[i].replaceAll("[\\)]+", " ) ");
            
            String tokenStrings[] = input[i].split(" ");
            
            try {
                for (int j = 0; j < tokenStrings.length; j++) {
                    if (!tokenStrings[j].matches("[\\s]*")) {
                        String tokenValue = tokenStrings[j];
                        String tokenClass = "";

                        //////////////////////////////
                        //  Checking for terminals  //
                        //////////////////////////////

                        if (j < tokenStrings.length) {
                            if (tokenStrings[j].contains("<")) {

                                //Does not contain space (E.G "V_abc <helo")
                                if (tokenStrings[j].length() < 2) {
                                    tokenValue = String.format("< %s", tokenStrings[j+1]);
                                    j++;                            //Skip redundant input
                                }

                                tokenClass = "user_input";
                            }
                        } 
                        // Edge case: V_abc <
                        else if (j < tokenStrings.length && tokenValue.contains("<")) {
                            throw new Error(String.format("ERROR: Character '<' should be followed by a value for the variable name"));
                        }
                        
                        inputTokens.add(new Token(tokenClass, tokenValue, this.tokenCount++));
                    }
                }
            } catch (Error err) {
                System.out.println(err);
            }
        }

        return inputTokens;
    }

    @Override
    public String toString() {
        String tokenString = "";
        for (Token s : this.tokens) {
            tokenString += s.toString() + "\n";
        }

        return (String.format("Tokens: %d\n", this.tokenCount) + "\n=========\n" + tokenString);
    }
}