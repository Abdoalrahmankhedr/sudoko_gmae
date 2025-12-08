package service.verify;

import java.util.HashSet;
import java.util.Set;

public class ColumnVerifier extends Verifier {
    public ColumnVerifier(int[][] board) {
        super(board);
    }

    @Override
    public boolean verify() {
        for (int i = 1; i < 10; i++) {
            if (!verify(i)) return false;
        }

        return true;
    }

    @Override
    public boolean verify(int index) {
        if (index < 1 || index > 9) {
            throw new IllegalArgumentException("Column index must be between 1 and 9.");
        }

        int col = index - 1;
        Set<Integer> seen = new HashSet<>();
        for (int i = 0; i < 9; i++) {
            int value = this.board[i][col];
            if (value != 0) {
                if (value < 1 || value > 9 || seen.contains(value)) return false;
                seen.add(value);
            }
        }

        return true;
    }
}
