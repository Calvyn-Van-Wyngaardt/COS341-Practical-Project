import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String fileToProcess = args[0];
        File inputDir = new File("../input");
        File inputFiles[] = inputDir.listFiles();
        ArrayList<String[]> inputs = new ArrayList<>();

        for (File inputFile : inputFiles) {
            inputs.add(readFile(inputFile));
        }

        Lexer lexer = new Lexer(inputs.get(Integer.parseInt(fileToProcess)));
        System.out.println(lexer);
    }

    private static String[] readFile(File inputFile) {
        try {
            ArrayList<String> lines = new ArrayList<>();
            Scanner scan = new Scanner(inputFile);
            while (scan.hasNextLine()) {
                lines.add(scan.nextLine());
            }        

            String output[] = new String[lines.size()];
            for (int i = 0; i < lines.size(); i++) {
                output[i] = lines.get(i);
            }

            scan.close();
            return output;

        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return null;
    }
}