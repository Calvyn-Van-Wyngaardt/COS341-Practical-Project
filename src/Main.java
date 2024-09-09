import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        File inputDir = new File("../input");
        File inputFiles[] = inputDir.listFiles();
        ArrayList<String[]> inputs = new ArrayList<>();

        for (int i = 0; i < inputFiles.length; i++) {
            inputs.add(readFile(inputFiles[i]));
        }

        Lexer lexer = new Lexer(inputs.get(1));
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