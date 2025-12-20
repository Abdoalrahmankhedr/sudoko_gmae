package adapter;

import controller.Viewable;
import exceptions.GameInvalidException;
import exceptions.GameNotFoundException;
import exceptions.SolutionInvalidException;
import model.Catalog;
import model.Difficulty;
import model.Game;
import model.UserAction;
import service.storage.BoardManager;
import views.Controllable;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Adapter Pattern implementation that bridges the View and Controller layers.
 * Converts between the view's primitive type interface and controller's object-oriented interface.
 * This allows the View (GUI) and Controller (business logic) to remain decoupled.
 */
public class ViewControllerAdapter implements Controllable {
    private final Viewable controller;
    private final BoardManager boardManager;


    public ViewControllerAdapter(Viewable controller) {
        this.controller = controller;
        this.boardManager = new BoardManager(",", ".csv");
    }

    @Override
    public Catalog getCatalog() {
        return controller.getCatalog();
    }

    @Override
    public int[][] getGame(char level) throws GameNotFoundException {
        try {
            Game game;

            if (level == 'I' || level == 'i') {
                // Load incomplete game
                game = controller.getGame(null);
            } else {
                // Convert char to Difficulty enum
                Difficulty difficulty = charToDifficulty(level);
                game = controller.getGame(difficulty);
            }

            return game.getBoard();

        } catch (GameNotFoundException e) {
            throw e;
        }
    }

    @Override
    public void driveGames(String sourcePath) throws SolutionInvalidException {
        try {
            // Load board from file
            int[][] board = boardManager.getFromFile(sourcePath);

            if (board == null) {
                throw new SolutionInvalidException("Could not load board from file: " + sourcePath);
            }

            // Create Game object
            Game sourceGame = new Game(sourcePath, board);

            // Generate difficulty levels
            controller.driveGames(sourceGame);

        } catch (FileNotFoundException e) {
            throw new SolutionInvalidException("File not found: " + sourcePath);
        }
    }

    @Override
    public boolean[][] verifyGame(int[][] game) {
        // Create temporary Game object
        Game tempGame = new Game(null, game);

        // Get verification result
        String result = controller.verifyGame(tempGame);

        // Initialize result array (all true by default)
        boolean[][] validationResult = new boolean[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                validationResult[i][j] = true;
            }
        }

        // If invalid, mark duplicate cells as false
        if (result.startsWith("invalid")) {
            String[] parts = result.split(" ");

            // Skip first element which is "invalid"
            for (int i = 1; i < parts.length; i++) {
                String coord = parts[i];
                String[] xy = coord.split(",");

                if (xy.length == 2) {
                    try {
                        int x = Integer.parseInt(xy[0]);
                        int y = Integer.parseInt(xy[1]);
                        validationResult[x][y] = false;
                    } catch (NumberFormatException e) {
                        System.err.println("[Adapter] Error parsing coordinates: " + coord);
                    }
                }
            }
        }

        return validationResult;
    }

    @Override
    public int[][] solveGame(int[][] game) throws GameInvalidException {
        // Create temporary Game object
        Game tempGame = new Game(null, game);

        // Solve the game - returns [cellIndex, value, cellIndex, value, ...]
        int[] solution = controller.solveGame(tempGame);

        // Create result array: [x, y, solution] format
        int numEmptyCells = solution.length / 2;
        int[][] result = new int[numEmptyCells][3];

        for (int i = 0; i < numEmptyCells; i++) {
            int cellIndex = solution[i * 2];
            int value = solution[i * 2 + 1];

            // Convert cell index to x, y coordinates
            int row = cellIndex / 9;
            int col = cellIndex % 9;

            result[i][0] = row;
            result[i][1] = col;
            result[i][2] = value;
        }

        return result;
    }

    @Override
    public void logUserAction(UserAction userAction) throws IOException {
        // Convert UserAction to string format
        String actionString = userAction.toString();
        controller.logUserAction(actionString);
    }

    /**
     * Helper method to convert character to Difficulty enum.
     */
    private Difficulty charToDifficulty(char level) {
        char upperLevel = Character.toUpperCase(level);

        switch (upperLevel) {
            case 'E':
                return Difficulty.EASY;
            case 'M':
                return Difficulty.MEDIUM;
            case 'H':
                return Difficulty.HARD;
            default:
                throw new IllegalArgumentException("Invalid difficulty level: " + level);
        }
    }
}