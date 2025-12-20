package views;

import controller.Viewable;
import exceptions.GameInvalidException;
import exceptions.GameNotFoundException;
import exceptions.SolutionInvalidException;
import model.Catalog;
import model.Difficulty;
import model.Game;
import model.UserAction;
import service.storage.BoardManager;

import javax.swing.*;
import java.awt.*;

public class GUI implements Controllable {

    private final Viewable viewable;
    private final JFrame mainFrame;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final StartPanel startPanel;
    private final GamePanel gamePanel;
    private final BoardManager boardManager;

    public GUI(Viewable viewable) {
        this.viewable = viewable;
        this.boardManager = new BoardManager(",", ".csv");
        this.mainFrame = new JFrame("Sudoku Game");
        this.cardLayout = new CardLayout();
        this.mainPanel = new JPanel(cardLayout);

        this.startPanel = new StartPanel(this);
        this.gamePanel = new GamePanel(this);

        initialize();
    }

    public void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(900, 700);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(false);

        mainPanel.add(startPanel, "StartPanel");
        mainPanel.add(gamePanel, "GamePanel");

        mainFrame.add(mainPanel);
        mainFrame.setVisible(true);

        showPage("StartPanel");
    }

    public void showPage(String pageName) {
        cardLayout.show(mainPanel, pageName);
    }

    public void requestNewGame(Difficulty level) {
        try {
            if (viewable instanceof controller.SudokuController) {
                ((controller.SudokuController) viewable).clearLog();
            }

            int[][] board = getGame(level == null ? 'I' : level.getCode()); // 'I' for incomplete
            gamePanel.updateBoard(board);
            showPage("GamePanel");
        } catch (GameNotFoundException e) {
            String message = (level == null)
                    ? "No saved game found. Start a new game!"
                    : "No games available for this difficulty level.";
            JOptionPane.showMessageDialog(mainFrame, message, "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Accessor for game panel to use undo
    public Viewable getViewable() {
        return viewable;
    }

    // --- Controllable Interface Implementation ---

    @Override
    public Catalog getCatalog() {
        return viewable.getCatalog();
    }

    @Override
    public int[][] getGame(char level) throws GameNotFoundException {
        Difficulty difficulty = null;
        if (Character.toUpperCase(level) != 'I') {
            difficulty = Difficulty.EASY.fromChar(level);
        }

        Game game = viewable.getGame(difficulty);
        return game.getBoard();
    }

    @Override
    public void driveGames(String sourcePath) throws SolutionInvalidException {
        try {
            int[][] board = boardManager.getFromFile(sourcePath);
            if (board == null) {
                throw new SolutionInvalidException("Could not load board from file.");
            }
            Game sourceGame = new Game(sourcePath, board);
            viewable.driveGames(sourceGame);
            JOptionPane.showMessageDialog(mainFrame, "Board added successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            throw new SolutionInvalidException(e.getMessage());
        }
    }

    @Override
    public boolean[][] verifyGame(int[][] game) {
        Game tempGame = new Game(null, game);
        String result = viewable.verifyGame(tempGame);

        boolean[][] validation = new boolean[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                validation[i][j] = true;
            }
        }

        if (result.startsWith("invalid")) {
            String[] parts = result.split(" ");
            for (int i = 1; i < parts.length; i++) {
                String[] coords = parts[i].split(",");
                if (coords.length == 2) {
                    try {
                        int r = Integer.parseInt(coords[0].trim());
                        int c = Integer.parseInt(coords[1].trim());
                        if (r >= 0 && r < 9 && c >= 0 && c < 9) {
                            validation[r][c] = false;
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return validation;
    }

    @Override
    public int[][] solveGame(int[][] game) throws GameInvalidException {
        Game tempGame = new Game(null, game);
        int[] solution = viewable.solveGame(tempGame);

        int[][] solvedBoard = new int[9][9];
        // Copy original
        for (int i = 0; i < 9; i++)
            System.arraycopy(game[i], 0, solvedBoard[i], 0, 9);

        for (int i = 0; i < solution.length; i += 2) {
            int idx = solution[i];
            int val = solution[i + 1];
            solvedBoard[idx / 9][idx % 9] = val;
        }

        return solvedBoard;
    }

    @Override
    public void logUserAction(UserAction userAction) throws java.io.IOException {
        viewable.logUserAction(userAction.toString());
    }
}
