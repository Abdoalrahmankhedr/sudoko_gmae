package controller;

import exceptions.GameInvalidException;
import exceptions.GameNotFoundException;
import exceptions.SolutionInvalidException;
import model.Catalog;
import model.Difficulty;
import model.Game;
import service.catalogue.GameCatalogue;
import service.generator.GameGenerator;
import service.log.UserActionLogger;
import service.solver.GameSolver;
import service.storage.GameManager;
import service.verify.GameVerifier;

import java.io.IOException;
import java.util.Map;

/**
 * Main controller implementing the Viewable interface.
 * Implements Facade Pattern - provides simplified interface to complex subsystems.
 * This is the main entry point for all business logic operations.
 */
public class SudokuController implements Viewable {
    // Subsystems
    private final service.GameCatalogue catalogue;
    private final GameManager gameManager;
    private final GameGenerator gameGenerator;
    private final GameVerifier gameVerifier;
    private final GameSolver gameSolver;
    private final UserActionLogger actionLogger;

    /**
     * Constructor initializes all subsystems.
     * Uses Singleton pattern for catalogue and logger.
     */
    public SudokuController() {
        this.catalogue = GameCatalogue.getInstance();
        this.gameManager = new GameManager();
        this.gameGenerator = new GameGenerator();
        this.gameVerifier = new GameVerifier();
        this.gameSolver = new GameSolver();
        this.actionLogger = UserActionLogger.getInstance();
    }

    @Override
    public Catalog getCatalog() {
        return catalogue.getCatalog();
    }

    @Override
    public Game getGame(Difficulty level) throws GameNotFoundException {
        Game game = null;

        if (level == null) {
            // Load incomplete game
            game = gameManager.load();
        } else {
            // Load game of specific difficulty
            game = gameManager.load(level);
        }

        if (game == null) {
            throw new GameNotFoundException(
                    "No game found for difficulty: " + (level == null ? "INCOMPLETE" : level.name())
            );
        }

        return game;
    }

    @Override
    public void driveGames(Game source) throws SolutionInvalidException {
        try {
            // Generate three difficulty levels
            Map<Difficulty, Game> generatedGames = gameGenerator.generate(source);

            // Save all generated games
            for (Map.Entry<Difficulty, Game> entry : generatedGames.entrySet()) {
                Game game = entry.getValue();
                boolean saved = gameManager.save(game);

                if (!saved) {
                    System.err.println("[SudokuController] Failed to save " +
                            entry.getKey() + " difficulty game");
                }
            }

            System.out.println("[SudokuController] Successfully generated and saved all difficulty levels");

        } catch (SolutionInvalidException e) {
            System.err.println("[SudokuController] Source solution is invalid: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public String verifyGame(Game game) {
        return gameVerifier.verify(game);
    }

    @Override
    public int[] solveGame(Game game) throws GameInvalidException {
        // Check if game has exactly 5 empty cells
        int emptyCells = gameSolver.countEmptyCells(game.getBoard());

        if (emptyCells != 5) {
            throw new GameInvalidException(
                    "Cannot solve: Game must have exactly 5 empty cells, found " + emptyCells
            );
        }

        return gameSolver.solve(game);
    }

    @Override
    public void logUserAction(String userAction) throws IOException {
        try {
            model.UserAction action = new model.UserAction(userAction);
            actionLogger.record(action);
        } catch (IllegalArgumentException e) {
            throw new IOException("Invalid user action format: " + e.getMessage());
        }
    }

    /**
     * Additional helper method for saving current game state.
     */
    public boolean saveCurrentGame(int[][] board) {
        return gameManager.saveCurrent(board);
    }

    /**
     * Deletes a completed game from storage.
     */
    public boolean deleteCompletedGame(Game game) {
        boolean deleted = gameManager.delete(game);

        // Also delete from current if it was the active game
        gameManager.deleteCurrent();

        // Delete log file
        actionLogger.delete();

        return deleted;
    }


    public model.UserAction undoLastAction() {
        return actionLogger.removeLast();
    }
}