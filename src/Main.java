import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

@SuppressWarnings("all")
public class Main {
  public static void main(String[] args) {
    File inputDir = new File("input"); // Lexer setup to run with makefile,
    File inputFiles[] =
        inputDir.listFiles(); // otherwise change "input" to "../input"
    ArrayList<String[]> inputs = new ArrayList<>(); // when compiling manually.

    for (File inputFile : inputFiles) {
      inputs.add(readFile(inputFile));
    }

    Arrays.sort(inputFiles, (a, b) -> a.getName().compareTo(b.getName()));

    System.out.println("Available files:");
    for (int i = 0; i < inputFiles.length; i++) {
      System.out.println(i + ": " + inputFiles[i].getName());
    }

    Scanner scanner = new Scanner(System.in);
    System.out.print("Enter the number of the file to process: ");
    int fileIndex = scanner.nextInt();
    scanner.close();

    try {
      RecSPLLexer lexer = new RecSPLLexer();
      lexer.main(inputs.get(fileIndex));
    } catch (Exception e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
      return;
    }
    try {
      RecSPLParser parser = new RecSPLParser();
      parser.main(args);
    } catch (Exception e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
      return;
    }
    try{
      RecSPLSymbolTable symbolTable = new RecSPLSymbolTable();
      symbolTable.main(args);
    } catch (Exception e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
      return;
    }
    // try {
    //   RecSPLTypeChecker typeChecker = new RecSPLTypeChecker();
    //   typeChecker.main(args);
    // } catch (Exception e) {
    //   System.err.println(e.getMessage());
    //   e.printStackTrace();
    //   return;
    // }
  }

  private static String[] readFile(File inputFile) {
    try {
      ArrayList<String> lines = new ArrayList<>();
      String[] output;
      try (Scanner scan = new Scanner(inputFile)) {
        while (scan.hasNextLine()) {
          lines.add(scan.nextLine());
        }
        output = new String[lines.size()];
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
