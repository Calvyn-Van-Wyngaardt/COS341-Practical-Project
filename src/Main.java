import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

@SuppressWarnings("all")
public class Main {
  public static void main(String[] args) {
    File inputDir = new File("input");
    File inputFiles[] = inputDir.listFiles();
    ArrayList<String[]> inputs = new ArrayList<>();

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

    System.out.println("========== Lexer ==========");
    try {
      RecSPLLexer lexer = new RecSPLLexer();
      lexer.main(inputs.get(fileIndex));
    } catch (Exception e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
      return;
    }

    System.out.println("========== Parser ==========");
    try {
      RecSPLParser parser = new RecSPLParser();
      parser.main(args);
    } catch (Exception e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
      return;
    }

    // System.out.println("========== Symbol Table ==========");
    // try {
    //   RecSPLSymbolTable symbolTable = new RecSPLSymbolTable();
    //   symbolTable.main(args);
    // } catch (Exception e) {
    //   System.err.println(e.getMessage());
    //   e.printStackTrace();
    //   return;
    // }
    System.out.println("========== Type Checker ==========");
    try {
      TypeChecker typeChecker = new TypeChecker();
      typeChecker.main(args);
    } catch (Exception e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
      return;
    }

    System.out.println("Program completed successfully");
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
