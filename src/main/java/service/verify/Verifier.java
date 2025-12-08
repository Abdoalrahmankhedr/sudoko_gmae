package service.verify;

public abstract class Verifier {
    protected final int[][] board;

    public Verifier(int[][] board) {
        this.board = board;
    }

    /* Verifies a specific check to the entire board */
    public abstract boolean verify();
    /* Verifies a specific check on an index e.g. row 1, column 4 */
    public abstract boolean verify(int index);
}
