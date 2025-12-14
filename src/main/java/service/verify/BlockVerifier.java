package service.verify;

import java.util.*;

/* Andrew :) */
public class BlockVerifier extends Verifier {
    public BlockVerifier(int[][] board) {
        super(board);
    }

    @Override
    public int[] verify(int index) {
        if (index < 1 || index > 9) {
            throw new IllegalArgumentException("Column index must be between 1 and 9.");
        }

        int block = index - 1;
        int startRow = (block / 3) * 3;
        int startCol = (block % 3) * 3;
        Map<Integer, Integer> seen = new HashMap<>();
        List<Integer> duplicates = new ArrayList<>();

        for (int i = startRow; i < startRow + 3; i++) {
            for (int j = startCol; j < startCol + 3; j++) {
                int value = board[i][j];
                int cellIndex = toCellIndex(i, j);

                if (value != 0) {
                    if (seen.containsKey(value)) {
                        duplicates.add(cellIndex);
                        int firstIndex = seen.get(value);
                        if (!duplicates.contains(firstIndex)) duplicates.add(firstIndex);
                    } else {
                        seen.put(value, cellIndex);
                    }
                }
            }
        }

        return duplicates.stream().mapToInt(i -> i).toArray();
    }
}
