package service.solver;

import exceptions.GameInvalidException;
import model.Game;

import java.util.ArrayList;
import java.util.List;

public class GameSolver {

    public int[] solve(Game game) throws GameInvalidException {
        int[][] board = game.getBoard();

        // Find empty cells
        List<Integer> emptyCells = findEmptyCells(board);

        if (emptyCells.size() > 5) {
            throw new GameInvalidException(
                    String.format("Solver requires 5 or less empty cells, found %d", emptyCells.size()));
        }

        // Convert to array for performance
        int[] emptyPositions = emptyCells.stream().mapToInt(i -> i).toArray();

        // Use Iterator Pattern to generate permutations on-the-fly
        PermutationIterator iterator = new PermutationIterator(emptyCells.size());

        System.out.println("[GameSolver] Starting to check up to " +
                iterator.getTotalPermutations() + " permutations...");

        long checked = 0;

        // Try each permutation using Flyweight Pattern
        while (iterator.hasNext()) {
            int[] permutation = iterator.next();
            checked++;

            // Create lightweight BoardState (Flyweight Pattern)
            BoardState state = new BoardState(board, emptyPositions, permutation);

            if (state.isValid()) {
                System.out.println("[GameSolver] Solution found after checking " +
                        checked + " permutations");
                return formatSolution(emptyPositions, permutation);
            }

            // Progress logging every 10,000 checks
            if (checked % 10000 == 0) {
                System.out.println("[GameSolver] Checked " + checked + " permutations...");
            }
        }

        throw new GameInvalidException("No solution found after checking all permutations");
    }

    private List<Integer> findEmptyCells(int[][] board) {
        List<Integer> emptyCells = new ArrayList<>();

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] == 0) {
                    emptyCells.add(row * 9 + col);
                }
            }
        }

        return emptyCells;
    }

    private int[] formatSolution(int[] emptyPositions, int[] values) {
        int[] result = new int[emptyPositions.length * 2];

        for (int i = 0; i < emptyPositions.length; i++) {
            result[i * 2] = emptyPositions[i];
            result[i * 2 + 1] = values[i];
        }

        return result;
    }

    /**
     * Returns the number of empty cells in a board.
     */
    public int countEmptyCells(int[][] board) {
        int count = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] == 0) {
                    count++;
                }
            }
        }
        return count;
    }
}
