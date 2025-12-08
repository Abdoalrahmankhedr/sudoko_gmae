package service.storage;

import model.Difficulty;
import model.Game;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/* Andrew :) */
/*
    Handles all file operations relating to Game objects
*/
public class GameManager {
    private final BoardManager boardManager;
    private final Map<Difficulty, String> dirs = new HashMap<>();
    private final String incomplete = "src/main/java/resources/incomplete/game.csv";

    public GameManager() {
        this.boardManager = new BoardManager(",", ".csv");
        this.dirs.put(Difficulty.EASY, "src/main/java/resources/easy");
        this.dirs.put(Difficulty.MEDIUM, "src/main/java/resources/medium");
        this.dirs.put(Difficulty.HARD, "src/main/java/resources/hard");
    }

    /* Loads current incomplete game */
    public Game load() {
        try {
            int[][] board = this.boardManager.getFromFile(this.incomplete);
            // TODO: how to get difficulty from file
            return new Game(this.incomplete, board);
        } catch (FileNotFoundException err) {
            System.out.println("[GameManager] No incomplete game was found");
        }

        return null;
    }

    /* Loads game based on difficulty */
    public Game load(Difficulty difficulty) {
        String gamePath = this.dirs.get(difficulty);
        if (gamePath == null) {
            System.out.println("[GameManager] (ERROR) Difficulty given is not registered with a directory");
            return null;
        }

        File directory = new File(gamePath);

        if (!directory.isDirectory()) {
            throw new IllegalArgumentException(String.format(
                    "Directory path for difficulty %s is not a folder", difficulty.toString()
            ));
        }

        if (!directory.exists()) {
            System.out.printf("[GameManager] No games were saved in this difficulty (DirectoryCreated: %b)\n",
                    directory.mkdirs());
            return null;
        }

        File[] games = directory.listFiles(File::isFile);
        if (games == null || games.length == 0) {
            System.out.println("[GameManager] No games were saved in this difficulty");
            return null;
        }

        Random random = new Random();
        int randomIndex = random.nextInt(games.length);
        File selectedFile = games[randomIndex];

        try {
            int[][] board = this.boardManager.getFromFile(selectedFile.getPath());
            return new Game(difficulty, selectedFile.getPath(), board);
        } catch (FileNotFoundException err) {
            System.out.println("[GameManager] Error while parsing game file: " + err.getMessage());
        }

        return null;
    }

    /* Save game to file */
    public boolean save(Game game) {
        String gamePath = game.getFilePath();
        Difficulty gameDiff = game.getDifficulty();

        try {
            if (gamePath != null && gameDiff != null) {
                return this.boardManager.saveToFile(gamePath, game.getBoard());
            } else return false;
        } catch (Exception e) {
            System.out.printf("[GameManager] Failed to save game: %s\n", e.getMessage());
        }

        return false;
    }

    /* Save board to current game file */
    public boolean saveCurrent(int[][] board) {
        try {
            return this.boardManager.saveToFile(this.incomplete, board);
        } catch (Exception e) {
            System.out.printf("[GameManager] Failed to save game: %s\n", e.getMessage());
        }

        return false;
    }

    /* Delete game file */
    public boolean delete(Game game) {
        String deletePath = game.getFilePath();
        if (deletePath == null || deletePath.isEmpty()) {
            System.out.println("[GameManager] Cannot delete game: File path is not defined.");
            return false;
        }

        try {
            this.boardManager.deleteFile(deletePath);
            return true;
        } catch (Exception e) {
            System.out.printf("[GameManager] Error during file deletion: %s\n", e.getMessage());
        }

        return false;
    }

    /* Delete current game file */
    public boolean deleteCurrent() {
        try {
            this.boardManager.deleteFile(this.incomplete);
            return true;
        } catch (Exception e) {
            System.out.printf("[GameManager] Error during file deletion: %s\n", e.getMessage());
        }

        return false;
    }
}
