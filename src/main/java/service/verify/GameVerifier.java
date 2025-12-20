package service.verify;

import model.Game;

import java.util.*;
import java.util.stream.Collectors;

public class GameVerifier {

    public String verify(Game game) {
        int[][] board = game.getBoard();

        // Check if incomplete (contains zeros)
        boolean hasEmptyCells = checkIncomplete(board);

        if (hasEmptyCells) {
            return "incomplete";
        }

        // Check for duplicates
        Set<Integer> allDuplicates = new HashSet<>();

        RowVerifier rowVerifier = new RowVerifier(board);
        ColumnVerifier colVerifier = new ColumnVerifier(board);
        BlockVerifier blockVerifier = new BlockVerifier(board);

        int[] rowDups = rowVerifier.verify();
        int[] colDups = colVerifier.verify();
        int[] blockDups = blockVerifier.verify();

        // Collect all duplicates
        for (int dup : rowDups) allDuplicates.add(dup);
        for (int dup : colDups) allDuplicates.add(dup);
        for (int dup : blockDups) allDuplicates.add(dup);

        if (allDuplicates.isEmpty()) {
            return "valid";
        }

        // Convert cell indices to x,y coordinates and format
        String duplicateCoords = allDuplicates.stream()
                .sorted()
                .map(this::cellIndexToCoordinates)
                .collect(Collectors.joining(" "));

        return "invalid " + duplicateCoords;
    }

    private boolean checkIncomplete(int[][] board) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private String cellIndexToCoordinates(int cellIndex) {
        // Cell index is 1-based from Verifier.toCellIndex()
        int adjustedIndex = cellIndex - 1;
        int row = adjustedIndex / 9;
        int col = adjustedIndex % 9;
        return row + "," + col;
    }

    public List<int[]> getDuplicateLocations(Game game) {
        int[][] board = game.getBoard();
        Set<Integer> allDuplicates = new HashSet<>();

        RowVerifier rowVerifier = new RowVerifier(board);
        ColumnVerifier colVerifier = new ColumnVerifier(board);
        BlockVerifier blockVerifier = new BlockVerifier(board);

        int[] rowDups = rowVerifier.verify();
        int[] colDups = colVerifier.verify();
        int[] blockDups = blockVerifier.verify();

        for (int dup : rowDups) allDuplicates.add(dup);
        for (int dup : colDups) allDuplicates.add(dup);
        for (int dup : blockDups) allDuplicates.add(dup);

        List<int[]> locations = new ArrayList<>();
        for (int cellIndex : allDuplicates) {
            int adjustedIndex = cellIndex - 1;
            int row = adjustedIndex / 9;
            int col = adjustedIndex % 9;
            locations.add(new int[]{row, col});
        }

        return locations;
    }

    public boolean isValid(int[][] board) {
        Game tempGame = new Game(null, board);
        String result = verify(tempGame);
        return "valid".equals(result);
    }
    /**
     * Checks if a board is incomplete.
     */
    public boolean isIncomplete(int[][] board) {
        return checkIncomplete(board);
    }
}