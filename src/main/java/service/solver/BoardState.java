package service.solver;

import java.util.Arrays;

/**
 * Flyweight Pattern implementation for board verification.
 * Instead of creating copies of the board for each permutation,
 * we use this lightweight object to represent a board state temporarily.
 * This drastically reduces memory usage during solving.
 */
public class BoardState {
    private final int[][] originalBoard;
    private final int[] emptyPositions;  // Flattened indices of empty cells
    private final int[] values;          // Current values to test


    public BoardState(int[][] originalBoard, int[] emptyPositions, int[] values) {
        this.originalBoard = originalBoard;
        this.emptyPositions = emptyPositions;
        this.values = values;
    }

    /**
     * Gets the value at a specific cell without modifying the original board.
     * Uses Flyweight pattern - reads from either original board or temporary values.
     */
    public int getCell(int row, int col) {
        int flatIndex = row * 9 + col;

        // Check if this cell is in our empty positions
        for (int i = 0; i < emptyPositions.length; i++) {
            if (emptyPositions[i] == flatIndex) {
                return values[i];
            }
        }

        // Otherwise return from original board
        return originalBoard[row][col];
    }

    /**
     * Verifies if the current state is valid without creating a full board copy.
     * This is the key to the Flyweight pattern - we verify without allocating memory.
     */
    public boolean isValid() {
        // Check rows
        for (int row = 0; row < 9; row++) {
            if (!isRowValid(row)) return false;
        }

        // Check columns
        for (int col = 0; col < 9; col++) {
            if (!isColumnValid(col)) return false;
        }

        // Check 3x3 blocks
        for (int block = 0; block < 9; block++) {
            if (!isBlockValid(block)) return false;
        }

        return true;
    }

    private boolean isRowValid(int row) {
        boolean[] seen = new boolean[10]; // indices 1-9 used
        for (int col = 0; col < 9; col++) {
            int value = getCell(row, col);
            if (value == 0) return false; // Should be complete
            if (seen[value]) return false; // Duplicate
            seen[value] = true;
        }
        return true;
    }

    private boolean isColumnValid(int col) {
        boolean[] seen = new boolean[10];
        for (int row = 0; row < 9; row++) {
            int value = getCell(row, col);
            if (value == 0) return false;
            if (seen[value]) return false;
            seen[value] = true;
        }
        return true;
    }

    private boolean isBlockValid(int block) {
        int startRow = (block / 3) * 3;
        int startCol = (block % 3) * 3;
        boolean[] seen = new boolean[10];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int value = getCell(startRow + i, startCol + j);
                if (value == 0) return false;
                if (seen[value]) return false;
                seen[value] = true;
            }
        }
        return true;
    }

    /**
     * Creates an actual board representation (used only when solution is found).
     */
    public int[][] toBoard() {
        int[][] result = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                result[i][j] = getCell(i, j);
            }
        }
        return result;
    }

    public int[] getValues() {
        return values.clone();
    }
}