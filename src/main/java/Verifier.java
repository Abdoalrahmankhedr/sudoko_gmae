import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Andrew :)

public class Verifier {
    public static String DELIMITER = ",";

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("No arguments provided, exiting..");
        } else if (args.length >= 2) {
            // Get arguments
            String filepath = args[0];
            String mode = args[1];

            // Get board
            int[][] board = readCsv(filepath);

            if (board != null) {
                // Call executor here:
                Executor.start(board, mode);
            }
        }
    }

    private static int[][] readCsv(String filepath) {
        if (!filepath.endsWith(".csv")) {
            // Make sure it is a csv file
            System.out.println("File provided is not csv, please give correct file path");
            return null;
        }

        File csv = new File(filepath);
        if (!csv.exists() || !csv.canRead()) {
            // Make sure it can be read
            System.out.println("File provided does not exist, or is being used by another program");
            return null;
        }

        List<List<Integer>> results = new ArrayList<>();
        try (Scanner reader = new Scanner(csv)) {
            while (reader.hasNextLine()) {
                try {
                    List<Integer> result = new ArrayList<>();
                    for (String n : reader.nextLine().split(DELIMITER)) {
                        int pn = Integer.parseInt(n);
                        if (pn >= 0 && pn <= 9) {
                            result.add(pn);
                        } else {
                            System.out.println("Number in board not in range 0 - 9");
                            return null;
                        }
                    }
                    results.add(result);
                } catch (NumberFormatException err) {
                    System.out.println("Invalid number in board");
                    return null;
                }
            }
        } catch (FileNotFoundException err) {
            System.out.println("File provided does not exist, or is being used by another program");
            return null;
        }

        return results.stream()
                .map(list -> list.stream()
                        .mapToInt(Integer::intValue)
                        .toArray())
                .toArray(int[][]::new);
    }
}
