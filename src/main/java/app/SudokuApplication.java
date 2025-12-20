package app;

import adapter.ViewControllerAdapter;
import controller.SudokuController;
import controller.Viewable;
import model.Catalog;
import views.Controllable;

        public class SudokuApplication {
            public static void main(String[] args) {
                System.out.println("=== Sudoku Game Application ===\n");

                // Initialize Controller (Business Logic Layer)
                Viewable controller = new SudokuController();

                // Create Adapter to bridge View and Controller
                Controllable adapter = new ViewControllerAdapter(controller);

                // Check game catalogue
                System.out.println("Checking game catalogue...");
                Catalog catalog = adapter.getCatalog();

                System.out.println("Has incomplete game: " + catalog.isCurrent());
                System.out.println("Has all difficulty levels: " + catalog.isAllModesExist());
                System.out.println();

                // Example workflow based on catalogue status
                if (catalog.isCurrent()) {
                    System.out.println("Loading incomplete game...");
                    loadIncompleteGame(adapter);
                } else if (catalog.isAllModesExist()) {
                    System.out.println("Loading game by difficulty...");
                    loadGameByDifficulty(adapter, 'E'); // Load Easy game
                } else {
                    System.out.println("No games available. Please provide a source solution.");
                    System.out.println("Example: adapter.driveGames(\"path/to/solved_sudoku.csv\")");
                }

                System.out.println("\n=== Application Ready ===");
                System.out.println("Now you can integrate with GUI (Swing/JavaFX)");
            }

            private static void loadIncompleteGame(Controllable adapter) {
                try {
                    int[][] board = adapter.getGame('I'); // 'I' for incomplete
                    System.out.println("Incomplete game loaded successfully!");
                    printBoard(board);
                } catch (Exception e) {
                    System.err.println("Error loading incomplete game: " + e.getMessage());
                }
            }

            private static void loadGameByDifficulty(Controllable adapter, char difficulty) {
                try {
                    int[][] board = adapter.getGame(difficulty);
                    String diffName = getDifficultyName(difficulty);
                    System.out.println(diffName + " game loaded successfully!");
                    printBoard(board);

                    // Example: Verify the loaded game
                    boolean[][] validation = adapter.verifyGame(board);
                    System.out.println("\nGame verification completed.");

                } catch (Exception e) {
                    System.err.println("Error loading game: " + e.getMessage());
                }
            }

            private static void printBoard(int[][] board) {
                System.out.println("\nCurrent Board:");
                for (int i = 0; i < 9; i++) {
                    if (i % 3 == 0 && i != 0) {
                        System.out.println("------+-------+------");
                    }
                    for (int j = 0; j < 9; j++) {
                        if (j % 3 == 0 && j != 0) {
                            System.out.print("| ");
                        }
                        System.out.print(board[i][j] == 0 ? ". " : board[i][j] + " ");
                    }
                    System.out.println();
                }
            }

            /**
             * Helper to get difficulty name
             */
            private static String getDifficultyName(char difficulty) {
                switch (Character.toUpperCase(difficulty)) {
                    case 'E': return "Easy";
                    case 'M': return "Medium";
                    case 'H': return "Hard";
                    default: return "Unknown";
                }
            }
        }
    }
}