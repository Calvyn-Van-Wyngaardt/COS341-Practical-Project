import java.util.*;

public class Lexer {
    private final ArrayList<Token> tokens;
    private int tokenCount;
    private static final String TOKEN_V = "V_[a-z][a-z0-9]";
    private static final String TOKEN_F = "F_[a-z][a-z0-9]";
    private static final String TOKEN_T = "[A-Z][a-z]{0,7}";
    private static final String TOKEN_N = "-?[0-9]+(\\.[0-9]+)?";

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

                        // User input
                        if (tokenStrings[j].contains("<")) {
                            if (j+1 < tokenStrings.length) {
                                tokenValue = "< ";
                                
                                try {
                                    while (!tokenStrings[j].equals(";")) {
                                        tokenValue += tokenStrings[j] + " ";
                                        j++;
                                    }
                                    tokenStrings[j] = tokenStrings[j].strip();
                                } catch (Exception e) {
                                    System.out.println("ERROR: No \";\" found for user input");
                                }


                                tokenValue = String.format("< %s", tokenStrings[j+1]);
                                tokenClass = "user_input";
                            }
                            else {
                                throw new Error(String.format("ERROR: Character '<' should be followed user input for the variable name"));
                            }
                        } else if (tokenStrings[j].matches(TOKEN_V)) {
                            tokenValue = tokenStrings[j];
                            tokenClass = "Variable_Name";
                        } else if (tokenStrings[j].matches(TOKEN_F)) {
                            tokenValue = tokenStrings[j];
                            tokenClass = "Function_Name";
                        } else if (tokenStrings[j].matches(TOKEN_T)) {
                            tokenValue = tokenStrings[j];
                            tokenClass = "String_Token";
                        } else if (tokenStrings[j].matches(TOKEN_N)) {
                            tokenValue = tokenStrings[j];
                            tokenClass = "Number_Token";
                        }
                        else {
                            throw new Error(String.format("ERROR: \"%s\" not found in grammar", tokenStrings[j]));
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