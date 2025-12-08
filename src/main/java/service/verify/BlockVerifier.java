package service.verify;

import java.util.HashSet;
import java.util.Set;

public class BlockVerifier extends Verifier {
    public BlockVerifier(int[][] board) {
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

        int block = index - 1;
        int startRow = (block / 3) * 3;
        int startCol = (block % 3) * 3;
        Set<Integer> seen = new HashSet<>();

        for (int i = startRow; i < startRow + 3; i++) {
            for (int j = startCol; j < startCol + 3; j++) {
                int value = board[i][j];

                if (value != 0) {
                    if (value < 1 || value > 9 || seen.contains(value)) return false;
                    seen.add(value);
                }
            }
        }

        return true;
    }
}
