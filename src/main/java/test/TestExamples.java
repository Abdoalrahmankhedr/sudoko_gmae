package test;

import adapter.ViewControllerAdapter;
import controller.SudokuController;
import controller.Viewable;
import exceptions.GameInvalidException;
import model.Catalog;
import model.Game;
import model.UserAction;
import service.generator.RandomPairs;
import service.solver.GameSolver;
import service.solver.PermutationIterator;
import service.verify.GameVerifier;
import views.Controllable;

import java.util.List;

public class TestExamples {

    public static void main(String[] args) {
        System.out.println("=== Sudoku Backend Test Examples ===\n");

        // Run individual tests
        testRandomPairs();
        testPermutationIterator();
        testGameVerifier();
        testGameSolver();
        testFullWorkflow();

        System.out.println("\n=== All Tests Completed ===");
    }

    public static void testRandomPairs() {
        System.out.println("--- Test 1: RandomPairs ---");
        RandomPairs randomPairs = new RandomPairs();

        // Generate 5 random distinct pairs
        List<int[]> pairs = randomPairs.generateDistinctPairs(5);

        System.out.println("Generated 5 random coordinate pairs:");
        for (int[] pair : pairs) {
            System.out.printf("  (%d, %d)\n", pair[0], pair[1]);
        }
        System.out.println();
    }

    public static void testPermutationIterator() {
        System.out.println("--- Test 2: PermutationIterator ---");

        // Create iterator for 3 positions (9^3 = 729 permutations)
        PermutationIterator iterator = new PermutationIterator(3);

        System.out.println("Total permutations: " + iterator.getTotalPermutations());
        System.out.println("First 10 permutations:");

        int count = 0;
        while (iterator.hasNext() && count < 10) {
            int[] perm = iterator.next();
            System.out.printf("  %d: [%d, %d, %d]\n", count + 1, perm[0], perm[1], perm[2]);
            count++;
        }
        System.out.println("  ... (719 more)\n");
    }

    public static void testGameVerifier() {
        System.out.println("--- Test 3: GameVerifier ---");
        GameVerifier verifier = new GameVerifier();

        // Test Case 1: Valid complete board
        int[][] validBoard = createValidSudoku();
        Game validGame = new Game(null, validBoard);
        String result1 = verifier.verify(validGame);
        System.out.println("Valid board verification: " + result1);

        // Test Case 2: Incomplete board
        int[][] incompleteBoard = createValidSudoku();
        incompleteBoard[0][0] = 0; // Make it incomplete
        Game incompleteGame = new Game(null, incompleteBoard);
        String result2 = verifier.verify(incompleteGame);
        System.out.println("Incomplete board verification: " + result2);

        // Test Case 3: Invalid board with duplicates
        int[][] invalidBoard = createValidSudoku();
        invalidBoard[0][1] = invalidBoard[0][0]; // Create duplicate in row
        Game invalidGame = new Game(null, invalidBoard);
        String result3 = verifier.verify(invalidGame);
        System.out.println("Invalid board verification: " + result3);
        System.out.println();
    }

    public static void testGameSolver() {
        System.out.println("--- Test 4: GameSolver ---");
        GameSolver solver = new GameSolver();

        // Create a board with exactly 5 empty cells
        int[][] board = createValidSudoku();
        board[0][0] = 0;
        board[0][1] = 0;
        board[1][0] = 0;
        board[1][1] = 0;
        board[2][0] = 0;

        System.out.println("Board with 5 empty cells:");
        printBoard(board);

        Game game = new Game(null, board);

        try {
            System.out.println("\nSolving...");
            int[] solution = solver.solve(game);

            System.out.println("Solution found!");
            System.out.println("Format: [cellIndex, value, ...]");
            System.out.print("Solution: [");
            for (int i = 0; i < solution.length; i++) {
                System.out.print(solution[i]);
                if (i < solution.length - 1) System.out.print(", ");
            }
            System.out.println("]");

            // Apply solution
            for (int i = 0; i < solution.length; i += 2) {
                int cellIndex = solution[i];
                int value = solution[i + 1];
                int row = cellIndex / 9;
                int col = cellIndex % 9;
                board[row][col] = value;
            }

            System.out.println("\nSolved board:");
            printBoard(board);

        } catch (GameInvalidException e) {
            System.err.println("Error: " + e.getMessage());
        }
        System.out.println();
    }

    public static void testFullWorkflow() {
        System.out.println("--- Test 5: Full Workflow ---");

        // Initialize controller and adapter
        Viewable controller = new SudokuController();
        Controllable adapter = new ViewControllerAdapter(controller);

        // Step 1: Check catalogue
        System.out.println("Step 1: Check game catalogue");
        Catalog catalog = adapter.getCatalog();
        System.out.println("  Has incomplete game: " + catalog.isCurrent());
        System.out.println("  Has all difficulties: " + catalog.isAllModesExist());

        // Step 2: If no games, generate them
        if (!catalog.isAllModesExist()) {
            System.out.println("\nStep 2: Generate games from source");
            try {
                // Note: You need to provide a valid solved Sudoku CSV file
                // For this example, we'll show the error handling
                System.out.println("  Would call: adapter.driveGames(\"path/to/source.csv\")");
                System.out.println("  This generates Easy, Medium, and Hard games");
            } catch (Exception e) {
                System.out.println("  Note: Provide valid source file for actual generation");
            }
        }

        // Step 3: Simulate game play
        System.out.println("\nStep 3: Simulate gameplay");
        int[][] testBoard = createValidSudoku();
        testBoard[0][0] = 0; // One empty cell

        // User makes a move
        System.out.println("  User enters 5 at position (0,0)");
        testBoard[0][0] = 5;

        // Log the action
        UserAction action = new UserAction(0, 0, 0, 5);
        try {
            adapter.logUserAction(action);
            System.out.println("  Action logged: " + action);
        } catch (Exception e) {
            System.out.println("  Logging skipped for demo");
        }

// Step 4: Verify the board
        System.out.println("\nStep 4: Verify board");
        boolean[][] validation = adapter.verifyGame(testBoard);
        boolean allValid = true;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!validation[i][j]) {
                    allValid = false;
                    System.out.println("  Invalid cell at (" + i + "," + j + ")");
                }
            }
        }
        if (allValid) {
            System.out.println("  Board is VALID!");
        }

        System.out.println();
    }

    private static int[][] createValidSudoku() {
        return new int[][]{
                {5, 3, 4, 6, 7, 8, 9, 1, 2},
                {6, 7, 2, 1, 9, 5, 3, 4, 8},
                {1, 9, 8, 3, 4, 2, 5, 6, 7},
                {8, 5, 9, 7, 6, 1, 4, 2, 3},
                {4, 2, 6, 8, 5, 3, 7, 9, 1},
                {7, 1, 3, 9, 2, 4, 8, 5, 6},
                {9, 6, 1, 5, 3, 7, 2, 8, 4},
                {2, 8, 7, 4, 1, 9, 6, 3, 5},
                {3, 4, 5, 2, 8, 6, 1, 7, 9}
        };
    }

    private static void printBoard(int[][] board) {
        for (int i = 0; i < 9; i++) {
            if (i % 3 == 0 && i != 0) {
                System.out.println("  ------+-------+------");
            }
            System.out.print("  ");
            for (int j = 0; j < 9; j++) {
                if (j % 3 == 0 && j != 0) {
                    System.out.print("| ");
                }
                System.out.print(board[i][j] == 0 ? ". " : board[i][j] + " ");
            }
            System.out.println();
        }
    }
}
