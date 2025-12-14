package service.verify;

import java.util.*;

/* Andrew :) */
public class RowVerifier extends Verifier {
    public RowVerifier(int[][] board) {
        super(board);
    }

    @Override
    public int[] verify(int index) {
        if (index < 1 || index > 9) {
            throw new IllegalArgumentException("Row index must be between 1 and 9.");
        }

        int row = index - 1;
        List<Integer> duplicates = new ArrayList<>();
        Map<Integer, Integer> seen = new HashMap<>();

        for (int i = 0; i < this.board[row].length; i++) {
            int value = this.board[row][i];
            int cellIndex = toCellIndex(index, i);

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
