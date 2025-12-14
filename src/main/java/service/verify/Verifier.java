package service.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Verifier {
    protected final int[][] board;

    public Verifier(int[][] board) {
        this.board = board;
    }

    /* Verifies a specific check to the entire board */
    public int[] verify() {
        List<Integer> allDuplicates = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            allDuplicates.addAll(Arrays.stream(verify(i)).boxed().toList());
        }

        return allDuplicates.stream().mapToInt(i->i).toArray();
    };
    /* Verifies a specific check on an index e.g. row 1, column 4 */
    public abstract int[] verify(int index);
    /* Converts x, y to a number out of 81 (assuming 9x9 board) */
    protected static int toCellIndex(int r, int c) {return (r * 9) + c + 1;}
}
