package service.verify;

import java.util.*;

/* Andrew :) */
public class ColumnVerifier extends Verifier {
    public ColumnVerifier(int[][] board) {
        super(board);
    }

    @Override
    public int[] verify(int index) {
        if (index < 1 || index > 9) {
            throw new IllegalArgumentException("Column index must be between 1 and 9.");
        }

        int col = index - 1;
        Map<Integer, Integer> seen = new HashMap<>();
        List<Integer> duplicates = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            int value = this.board[i][col];
            int cellIndex = toCellIndex(i, index);

            if (value != 0) {
                if (seen.containsKey(value)) {
                    duplicates.add(cellIndex);
                    int firstIndex = seen.get(value);
                    if (!duplicates.contains(firstIndex)) {
                        duplicates.add(firstIndex);
                    }
                } else {
                    seen.put(value, cellIndex);
                }
            }
        }

        return duplicates.stream().mapToInt(i -> i).toArray();
    }
}
