import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String fileToProcess = "3";
        File inputDir = new File("input");                  // Lexer setup to run with makefile, 
        File inputFiles[] = inputDir.listFiles();                    // otherwise change "input" to "../input"
        ArrayList<String[]> inputs = new ArrayList<>();              // when compiling manually.

        for (File inputFile : inputFiles) {
            inputs.add(readFile(inputFile));
        }
        try {
            Lexer lexer = new Lexer(inputs.get(Integer.parseInt(fileToProcess)));
            System.out.println(lexer);
            // Parser parser = nemaw Parser("docs/SLR_Table.csv");

            // Print results (for demonstration)
            // System.out.println("Action Map:");
            // Parser.printFormattedTable(0);
            // System.out.println("\nGoto Map:");
            // Parser.printFormattedTable(1);

            ScopeAnalyzer analyzer = new ScopeAnalyzer();
            analyzer.initTreeWithScoping();
            System.out.println(analyzer);

        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static String[] readFile(File inputFile) {
        try {
            ArrayList<String> lines = new ArrayList<>();
            String[] output;
            try (Scanner scan = new Scanner(inputFile)) {
                while (scan.hasNextLine()) {
                    lines.add(scan.nextLine());
                }   output = new String[lines.size()];
                for (int i = 0; i < lines.size(); i++) {
                    output[i] = lines.get(i);
                }
            }
            return output;

        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return null;
    }
}