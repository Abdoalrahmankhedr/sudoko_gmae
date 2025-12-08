package service.verify;

import java.util.HashSet;
import java.util.Set;

public class RowVerifier extends Verifier {
    public RowVerifier(int[][] board) {
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
            throw new IllegalArgumentException("Row index must be between 1 and 9.");
        }

        int row = index - 1;
        Set<Integer> seen = new HashSet<>();
        for (int value : this.board[row]) {
            if (value != 0) {
                if (value < 1 || value > 9 || seen.contains(value)) return false;
                seen.add(value);
            }
        }

        return true;
    }
}
