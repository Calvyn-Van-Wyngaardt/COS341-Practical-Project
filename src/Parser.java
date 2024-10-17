import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Parser {
    static String[][] data;
    static Map<Integer, Map<String, String>> actionMap = new HashMap<>();
    static Map<Integer, Map<String, String>> gotoMap = new HashMap<>();

    public Parser(String pathToCsv) {
        try {
            this.data = readCSV(pathToCsv);
            createMaps();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String[][] readCSV(String fileName) throws IOException {
        BufferedReader reader = Files.newBufferedReader(Paths.get(fileName));
        String[][] csvData = new String[128][]; // Assuming max 128 rows
        String line;
        int row = 0;
    
        while ((line = reader.readLine()) != null) {
            if (row != 128) {
                Scanner scanner = new Scanner(line);
                scanner.useDelimiter(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");  // Regex to handle commas inside quotes
    
                String[] rowData = new String[72]; // Assuming max 72 columns
                int col = 0;
                while (scanner.hasNext()) {
                    String value = scanner.next();
                    rowData[col++] = value == null || value.isEmpty() ? "" : value.replaceAll("^\"|\"$", "");  // Handle null and remove quotes
                }
    
                csvData[row++] = rowData;
                scanner.close();
            }
        }
    
        reader.close();
        return csvData;
    }
    
    public static void createMaps() {
        String[] headers = data[2]; // Row 1, the headers
        String[] terminalsAndNonTerminals = Arrays.copyOfRange(data[2], 0, 72); // Row 3, columns 2-71 (non-terminals and terminals)
        int gotoStartIndex = 38; // Index where GOTO columns start
    
        for (int i = 3; i < data.length && data[i] != null; i++) { // Start from row 4
            String[] row = data[i];
            if (row == null) continue;  // Skip null rows

            int state;
            try {
                state = Integer.parseInt(row[0]);
            } catch (NumberFormatException e) {
                continue; // Skip rows that don't start with a valid state number
            }
    
            Map<String, String> stateActions = new HashMap<>();
            Map<String, String> stateGotos = new HashMap<>();
    
            // Process ACTION columns
            for (int j = 1; j < gotoStartIndex && j < row.length; j++) {
                if (row[j] != null && !row[j].isEmpty()) {
                    stateActions.put(terminalsAndNonTerminals[j - 1], row[j]); // Map terminals/non-terminals to actions
                }
            }
    
            // Process GOTO columns
            for (int j = gotoStartIndex; j < row.length; j++) {
                if (row[j] != null && !row[j].isEmpty()) {
                    stateGotos.put(headers[j], row[j]);
                }
            }
    
            if (!stateActions.isEmpty()) {
                actionMap.put(state, stateActions);
            }
            if (!stateGotos.isEmpty()) {
                gotoMap.put(state, stateGotos);
            }
        }
    }
    

    public static void printFormattedTable(Integer num) {
        Map<Integer, Map<String, String>> map = new HashMap<>();
        switch (num) {
            case 0: map = actionMap; break;
            case 1: map = gotoMap; break;
            default: throw new Error("No table specified!");
        }
        // Collect all unique symbols
        Set<String> allSymbols = new TreeSet<>();
        for (Map<String, String> stateMap : map.values()) {
            if (stateMap != null) { // Check if stateMap is not null
                for (String key : stateMap.keySet()) {
                    if (key != null) { // Check if key is not null
                        allSymbols.add(key);
                    }
                }
            }
        }
        
        // Print header
        System.out.print("State    |");
        for (String symbol : allSymbols) {
            System.out.printf(" %-9s |", symbol);
        }
        System.out.println();
        
        // Print separator
        System.out.print("---------|");
        for (int i = 0; i < allSymbols.size(); i++) {
            System.out.print("-----------|");
        }
        System.out.println();
        
        // Print table content
        for (int state : new TreeSet<>(map.keySet())) {
            System.out.printf("%-8d |", state);
            Map<String, String> stateMap = map.get(state);
            for (String symbol : allSymbols) {
                String value = stateMap.getOrDefault(symbol, "");
                System.out.printf(" %-9s |", value);
            }
            System.out.println();
        }
    }

}
