package app;

import controller.SudokuController;
import controller.Viewable;
import javax.swing.SwingUtilities;
import views.GUI;

public class SudokuApplication {
    public static void main(String[] args) {
        System.out.println("=== Sudoku Game Application ===\n");

        // Initialize Controller (Business Logic Layer)
        Viewable controller = new SudokuController();

        // Launch GUI
        SwingUtilities.invokeLater(() -> {
            new GUI(controller);
        });
    }
}
