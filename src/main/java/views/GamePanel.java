package views;

import controller.SudokuController;
import exceptions.GameInvalidException;
import model.UserAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class GamePanel extends JPanel {
    private final GUI gui;
    private final JTextField[][] cells;
    private int[][] currentBoard;

    private int activeRow = -1;
    private int activeCol = -1;

    public GamePanel(GUI gui) {
        this.gui = gui;
        this.cells = new JTextField[9][9];

        setLayout(new BorderLayout());
        setBackground(new Color(250, 250, 250));

        // Top Panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.setBackground(new Color(250, 250, 250));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        JLabel title = new JLabel("Sudoku", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(50, 50, 50));
        topPanel.add(title);
        add(topPanel, BorderLayout.NORTH);

        // Center Panel: Sudoku Grid
        // Use a background panel to center the grid
        JPanel gridContainer = new JPanel(new GridBagLayout());
        gridContainer.setBackground(new Color(250, 250, 250));

        JPanel gridPanel = new JPanel(new GridLayout(9, 9));
        gridPanel.setPreferredSize(new Dimension(500, 500));
        gridPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        gridPanel.setBackground(Color.WHITE);

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                JTextField cell = new JTextField();
                cell.setHorizontalAlignment(JTextField.CENTER);
                cell.setFont(new Font("Segoe UI", Font.BOLD, 24));
                cell.setForeground(new Color(50, 50, 50));

                // Restrict input
                ((javax.swing.text.AbstractDocument) cell.getDocument()).setDocumentFilter(new LimitInputFilter());

                cell.setBorder(BorderFactory.createMatteBorder(
                        (i % 3 == 0 && i != 0) ? 2 : 1, // Top
                        (j % 3 == 0 && j != 0) ? 2 : 1, // Left
                        1, 1, // Bottom, Right
                        Color.BLACK));

                final int row = i;
                final int col = j;

                // DocumentListener to capture input immediately
                cell.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                    public void insertUpdate(javax.swing.event.DocumentEvent e) {
                        handleInput(row, col, cell.getText());
                    }

                    public void removeUpdate(javax.swing.event.DocumentEvent e) {
                        handleInput(row, col, cell.getText());
                    }

                    public void changedUpdate(javax.swing.event.DocumentEvent e) {
                        handleInput(row, col, cell.getText());
                    }
                });

                // FocusListener just for tracking active cell
                cell.addFocusListener(new java.awt.event.FocusAdapter() {
                    @Override
                    public void focusGained(java.awt.event.FocusEvent e) {
                        activeRow = row;
                        activeCol = col;
                    }
                });

                cells[i][j] = cell;
                gridPanel.add(cell);
            }
        }
        gridContainer.add(gridPanel);
        add(gridContainer, BorderLayout.CENTER);

        // Bottom Panel: Controls
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        bottomPanel.setBackground(new Color(250, 250, 250));

        JButton undoBtn = createStyledButton("Undo", new Color(65, 105, 225)); // Orange
        undoBtn.addActionListener(e -> {
            commitActiveCell();
            handleUndo();
        });
        undoBtn.setForeground(Color.white);

        JButton verifyBtn = createStyledButton("Verify", new Color(65, 105, 225)); // Green
        verifyBtn.addActionListener(e -> {
            commitActiveCell();
            handleVerify();
        });
        verifyBtn.setForeground(Color.white);

        JButton solveBtn = createStyledButton("Solve", new Color(65, 105, 225)); // Royal Blue
        solveBtn.addActionListener(e -> {
            commitActiveCell();
            handleSolve();
        });
        solveBtn.setForeground(Color.white);

        JButton backBtn = createStyledButton("Back", new Color(41, 25, 165));
        backBtn.setForeground(Color.white); // Keep consistent with StartPanel

        backBtn.addActionListener(e -> {
            if (gui.getViewable() instanceof SudokuController) {
                ((SudokuController) gui.getViewable()).clearLog();
            }
            gui.showPage("StartPanel");
        });

        bottomPanel.add(undoBtn);
        bottomPanel.add(verifyBtn);
        bottomPanel.add(solveBtn);
        bottomPanel.add(backBtn);
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("control Z"), "undoAction");

        this.getActionMap().put("undoAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                commitActiveCell();
                handleUndo();
            }
        });
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void commitActiveCell() {
        if (activeRow != -1 && activeCol != -1) {
            handleInput(activeRow, activeCol, cells[activeRow][activeCol].getText());
        }
    }

    private Color getCellColor(int r, int c, boolean isEditable) {
        // Checkerboard pattern applies to ALL cells now, matching the mockup
        int blockRow = r / 3;
        int blockCol = c / 3;
        boolean isDarkerBlock = (blockRow + blockCol) % 2 != 0;

        return isDarkerBlock ? new Color(235, 240, 255) : Color.WHITE;
    }

    public void updateBoard(int[][] board) {
        this.currentBoard = board;
        isUpdatingBoard = true;
        try {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    int val = board[i][j];
                    cells[i][j].setText(val == 0 ? "" : String.valueOf(val));
                    cells[i][j].setEditable(val == 0);

                    cells[i][j].setForeground(val == 0 ? new Color(0, 102, 204) : Color.BLACK);
                    cells[i][j].setBackground(getCellColor(i, j, val == 0));
                }
            }
        } finally {
            isUpdatingBoard = false;
        }
    }

    private boolean isUndoing = false;
    private boolean isUpdatingBoard = false;

    private void handleInput(int r, int c, String text) {
        if (isUndoing || isUpdatingBoard)
            return; // Prevent processing inputs during undo or board update

        try {
            int newVal = 0;
            if (!text.trim().isEmpty()) {
                newVal = Integer.parseInt(text.trim());
                if (newVal < 1 || newVal > 9)
                    throw new NumberFormatException();
            }

            int prev = currentBoard[r][c];
            if (prev != newVal) {
                currentBoard[r][c] = newVal;
                gui.logUserAction(new UserAction(r, c, prev, newVal));
                saveGame();
            }
        } catch (Exception ex) {
            // Revert if invalid
            if (isUndoing)
                return;

            int prev = currentBoard[r][c];
            Runnable doRevert = () -> cells[r][c].setText(prev == 0 ? "" : String.valueOf(prev));
            SwingUtilities.invokeLater(doRevert);
        }
    }

    private void saveGame() {
        if (gui.getViewable() instanceof SudokuController) {
            ((SudokuController) gui.getViewable()).saveCurrentGame(currentBoard);
        }
    }

    private void handleSolve() {
        try {
            int[][] current = getUserBoard();
            int[][] solved = gui.solveGame(current);
            updateBoard(solved); // Reflect solution
        } catch (GameInvalidException e) {
            JOptionPane.showMessageDialog(this, "Solver Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int[][] getUserBoard() {
        int[][] board = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                String text = cells[i][j].getText();
                board[i][j] = (text == null || text.trim().isEmpty()) ? 0 : Integer.parseInt(text.trim());
            }
        }
        return board;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(100, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg);
            }
        });

        return btn;
    }

    private void handleVerify() {
        int[][] current = getUserBoard();

        // 1. Check for incompleteness first
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (current[i][j] == 0) {
                    JOptionPane.showMessageDialog(this, "The board is not completed", "Verify",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
        }

        // 2. If complete, verify correctness
        boolean[][] result = gui.verifyGame(current);
        boolean valid = true;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!result[i][j]) {
                    cells[i][j].setBackground(new Color(255, 200, 200)); // Invalid
                    valid = false;
                } else {
                    cells[i][j].setBackground(getCellColor(i, j, cells[i][j].isEditable()));
                }
            }
        }

        if (valid) {
            JOptionPane.showMessageDialog(this, "Congratulations! The board is correct.", "Verify",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "The board is completed but some numbers are incorrect (highlighted).",
                    "Verify",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleUndo() {
        if (gui.getViewable() instanceof SudokuController) {
            UserAction lastAction = ((SudokuController) gui.getViewable()).undoLastAction();
            if (lastAction != null) {
                int r = lastAction.getX();
                int c = lastAction.getY();
                int prev = lastAction.getPrev();

                isUndoing = true; // Block input listener
                try {
                    currentBoard[r][c] = prev;
                    cells[r][c].setText(prev == 0 ? "" : String.valueOf(prev));
                    cells[r][c].setBackground(getCellColor(r, c, true)); // Undo cells are always editable
                    saveGame(); // Save the state AFTER undo so file persists the reversion
                } finally {
                    isUndoing = false;
                }
            } else {
                JOptionPane.showMessageDialog(this, "Nothing to undo.", "Undo", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Undo not supported by current controller.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private class LimitInputFilter extends javax.swing.text.DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String str, javax.swing.text.AttributeSet attr)
                throws javax.swing.text.BadLocationException {
            if (isValid(fb.getDocument().getLength(), str)) {
                super.insertString(fb, offset, str, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String str, javax.swing.text.AttributeSet attrs)
                throws javax.swing.text.BadLocationException {
            if (isValid(fb.getDocument().getLength() - length, str)) {
                super.replace(fb, offset, length, str, attrs);
            }
        }

        private boolean isValid(int currentLength, String str) {
            if (str == null)
                return true;
            if (currentLength + str.length() > 1)
                return false;
            return str.matches("[1-9]*");
        }
    }
}
