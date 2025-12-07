package service.storage;

import exceptions.SolutionInvalidException;

import java.io.File;
import java.io.FileNotFoundException;

public class BoardManager {
    private final String delimiter;
    private final String extension;

    public BoardManager(String delimiter, String extension) {
        this.delimiter = delimiter;
        this.extension = extension;
    }

    /* Get a ready board from file */
    public int[][] getFromFile(String boardPath) throws FileNotFoundException {
        File file = new File(boardPath);

        /* Make sure file is correct format and exists */
        if (boardPath.endsWith(this.extension) && file.exists() && file.canRead()) {
            try {
                FileManager manager = new FileManager(boardPath);
                String content = manager.read();

                if (content == null || content.trim().isEmpty()) {
                    System.out.println("[GameManager] File is empty, nothing retrieved");
                    return null;
                }

                int[][] board = new int[9][9];
                String[] rows = content.split("\\n");
                if (rows.length != 9) {
                    throw new SolutionInvalidException(String.format("Game board only has %d rows", rows.length));
                }

                /* Parse through rows */
                for (int i = 0; i < 9; i++) {
                    String[] columns = rows[i].split(this.delimiter);
                    if (columns.length != 9) {
                        throw new SolutionInvalidException(String.format("Game board only has %d columns", columns.length));
                    }

                    /* Parse through columns */
                    for (int j = 0; j < 9; j++) {
                        int value = Integer.parseInt(columns[j]);

                        if (value < 0 || value > 9) {
                            throw new SolutionInvalidException(String.format(
                                    "Game board has %d on (%d, %d)", value, i + 1, j + 1)
                            );
                        }

                        board[i][j] = value;
                    }
                }
                return board;

            } catch (NumberFormatException err) {
                System.out.printf("[GameManager] Error occurred when parsing game file: %s\n", err.getMessage());
                return null;
            }

        } else {
            throw new FileNotFoundException("File either doesn't exist or isn't a csv file");
        }
    }


    /* Save board state to file */
    public boolean saveToFile(String boardPath, int[][] board) {
        if (board == null || board.length != 9 || board[0].length != 9) {
            System.err.println("[BoardManager] Invalid board dimensions. Expected 9x9.");
            return false;
        }

        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    sb.append(board[i][j]);
                    /* Add delimiter after every number except the last one in the row */
                    if (j < 8) {
                        sb.append(this.delimiter);
                    }
                }

                /* Add a newline character after every row except the last row */
                if (i < 8) {
                    sb.append(System.lineSeparator());
                }
            }

            FileManager manager = new FileManager(boardPath);
            manager.write(sb.toString());

            return true;

        } catch (Exception e) {
            System.out.printf("[BoardManager] Error occurred during file save: %s\n", e.getMessage());
        }

        return false;
    }

    /* Delete board file */
    public void deleteFile(String boardPath) {
        FileManager manager = new FileManager(boardPath);
        manager.delete();
    }
}
