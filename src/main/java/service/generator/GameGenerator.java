package service.generator;

import exceptions.SolutionInvalidException;
import model.Difficulty;
import model.Game;
import service.verify.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates Sudoku games of different difficulty levels from a valid solution.
 * Uses Strategy Pattern for different difficulty generation strategies.
 */
public class GameGenerator {
    private final RandomPairs randomPairs;
    private final Map<Difficulty, Integer> difficultyCellsToRemove;
    private final String baseDir = "src/main/java/resources/";

    public GameGenerator() {
        this.randomPairs = new RandomPairs();
        this.difficultyCellsToRemove = new HashMap<>();
        this.difficultyCellsToRemove.put(Difficulty.EASY, 10);
        this.difficultyCellsToRemove.put(Difficulty.MEDIUM, 20);
        this.difficultyCellsToRemove.put(Difficulty.HARD, 25);
    }


    private boolean isValidSolution(int[][] board) {
        // Check for incomplete (contains zeros)
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] == 0) {
                    return false;
                }
            }
        }

        // Check for duplicates
        RowVerifier rowVerifier = new RowVerifier(board);
        ColumnVerifier colVerifier = new ColumnVerifier(board);
        BlockVerifier blockVerifier = new BlockVerifier(board);

        int[] rowDuplicates = rowVerifier.verify();
        int[] colDuplicates = colVerifier.verify();
        int[] blockDuplicates = blockVerifier.verify();

        return rowDuplicates.length == 0 &&
                colDuplicates.length == 0 &&
                blockDuplicates.length == 0;
    }


    public Map<Difficulty, Game> generate(Game sourceGame) throws SolutionInvalidException {
        int[][] sourceBoard = sourceGame.getBoard();

        // Verify source solution is valid
        if (!isValidSolution(sourceBoard)) {
            throw new SolutionInvalidException("Source solution is invalid or incomplete");
        }

        Map<Difficulty, Game> generatedGames = new HashMap<>();

        // Generate each difficulty level
        for (Difficulty difficulty : Difficulty.values()) {
            int cellsToRemove = difficultyCellsToRemove.get(difficulty);
            int[][] newBoard = generateBoard(sourceBoard, cellsToRemove);

            String fileName = "game_" + System.currentTimeMillis() + ".csv";
            String filePath = baseDir + difficulty.name().toLowerCase() + "/" + fileName;

            // Ensure directory exists
            File dir = new File(baseDir + difficulty.name().toLowerCase());
            if (!dir.exists()) {
                dir.mkdirs();
            }

            Game game = new Game(difficulty, filePath, newBoard);
            generatedGames.put(difficulty, game);
        }

        return generatedGames;
    }


    private int[][] generateBoard(int[][] sourceBoard, int cellsToRemove) {
        // Deep copy the board
        int[][] newBoard = new int[9][9];
        for (int i = 0; i < 9; i++) {
            System.arraycopy(sourceBoard[i], 0, newBoard[i], 0, 9);
        }

        // Remove random cells
        List<int[]> positions = randomPairs.generateDistinctPairs(cellsToRemove);
        for (int[] pos : positions) {
            newBoard[pos[0]][pos[1]] = 0;
        }

        return newBoard;
    }
}