package model;

/* Andrew :) */
public class Game {
    private final Difficulty difficulty;
    private final int[][] board;

    public Game(Difficulty difficulty, int[][] board) {
        this.difficulty = difficulty;
        this.board = board;
    }

    /* Update board */
    public void setCell(int x, int y, int value) {
        this.board[x][y] = value;
    }

    public void clearCell(int x, int y) {
        setCell(x, y, 0);
    }

    /* Getters */
    public Difficulty getDifficulty() {return difficulty;}
    public int[][] getBoard() {return board;}
}
