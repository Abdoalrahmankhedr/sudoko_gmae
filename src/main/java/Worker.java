import java.util.*;
import java.util.concurrent.Callable;

// Worker class that verifies Sudoku sections (rows, columns, or boxes)
public class Worker implements Callable<List<String>> {
    private final int[][] board;
    private final WorkerType type;
    private final int index; // Which row/col/box to check (-1 means check all)
    
    public enum WorkerType {
        ROW, COLUMN, BOX
    }
    
    // Constructor for checking a specific section
    public Worker(int[][] board, int index, char c) {
        this.board = board;
        this.type = c=='R'? WorkerType.ROW:c=='C'? WorkerType.COLUMN : WorkerType.BOX;
        this.index = index;
    }
    
    @Override
    public List<String> call() {
        List<String> violations = new ArrayList<>();
        
        if (index == -1) {
            // Check all sections of this type (for mode 3)
            for (int i = 0; i < 9; i++) {
                List<String> sectionViolations = checkSection(i);
                violations.addAll(sectionViolations);
            }
        } else {
            // Check specific section (for mode 27)
            List<String> sectionViolations = checkSection(index);
            violations.addAll(sectionViolations);
        }
        
        return violations;
    }
    
    private List<String> checkSection(int idx) {
        List<String> violations = new ArrayList<>();
        int[] values = extractValues(idx);
        Map<Integer, List<Integer>> duplicates = findDuplicates(values);
        
        // For each duplicate value found, create a separate violation line
        for (Map.Entry<Integer, List<Integer>> entry : duplicates.entrySet()) {
            int duplicateValue = entry.getKey();
            List<Integer> positions = entry.getValue();
            violations.add(formatViolation(idx, duplicateValue, positions));
        }
        
        return violations;
    }
    
    private int[] extractValues(int idx) {
        int[] values = new int[9];
        
        switch (type) {
            case ROW:
                values = board[idx].clone();
                break;
                
            case COLUMN:
                for (int i = 0; i < 9; i++) {
                    values[i] = board[i][idx];
                }
                break;
                
            case BOX:
                // Box numbering: 0-8 (left to right, top to bottom)
                int startRow = (idx / 3) * 3;
                int startCol = (idx % 3) * 3;
                int count = 0;
                for (int i = startRow; i < startRow + 3; i++) {
                    for (int j = startCol; j < startCol + 3; j++) {
                        values[count++] = board[i][j];
                    }
                }
                break;
        }
        
        return values;
    }
    
    private Map<Integer, List<Integer>> findDuplicates(int[] values) {
        Map<Integer, List<Integer>> positionMap = new HashMap<>();
        
        // Track all positions for each value (1-indexed for output)
        for (int i = 0; i < values.length; i++) {
            int value = values[i];
            positionMap.putIfAbsent(value, new ArrayList<>());
            positionMap.get(value).add(i + 1); // 1-indexed positions
        }
        
        // Keep only duplicates (values that appear more than once)
        Map<Integer, List<Integer>> duplicates = new HashMap<>();
        for (Map.Entry<Integer, List<Integer>> entry : positionMap.entrySet()) {
            if (entry.getValue().size() > 1) {
                duplicates.put(entry.getKey(), entry.getValue());
            }
        }
        
        return duplicates;
    }
    
    private String formatViolation(int idx, int duplicateValue, List<Integer> positions) {
        String sectionType;
        switch (type) {
            case ROW:
                sectionType = "ROW";
                break;
            case COLUMN:
                sectionType = "COL";
                break;
            case BOX:
                sectionType = "BOX";
                break;
            default:
                sectionType = "UNKNOWN";
        }

        // Format: "ROW 1, #1, [1, 2, 3, 4, 5, 6, 7, 8, 9]"
        return String.format("%s %d, #%d, %s", 
                            sectionType, 
                            idx + 1,  // 1-indexed
                            duplicateValue, 
                            positions.toString());
    }
}