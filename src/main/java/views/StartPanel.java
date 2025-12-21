package views;

import model.Difficulty;
import service.log.UserActionLogger;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;

public class StartPanel extends JPanel {
    private final GUI gui;

    public StartPanel(GUI gui) {
        this.gui = gui;
        setLayout(new BorderLayout()); // Use BorderLayout for Header/Center separation
        setBackground(new Color(245, 247, 250)); // Very subtle blue-grey background

        // --- Header Panel ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 247, 250));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // Add Board (Left)
        JButton addBoardBtn = createStyledOutlineButton("+ Add Custom Board");
        addBoardBtn.addActionListener(e -> openFileBrowser());
        JPanel leftContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftContainer.setOpaque(false);
        leftContainer.add(addBoardBtn);
        headerPanel.add(leftContainer, BorderLayout.WEST);

        // Complete Last Game (Right)
        JButton resumeBtn = createStyledOutlineButton("Complete Last Game");
        resumeBtn.addActionListener(e -> gui.requestNewGame(null));
        JPanel rightContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightContainer.setOpaque(false);
        rightContainer.add(resumeBtn);
        headerPanel.add(rightContainer, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // --- Center Panel (Difficulty Buttons) ---
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 20, 15, 20); // Even spacing

        // Buttons
        Dimension btnSize = new Dimension(250, 60);

        JButton easyBtn = createStyledButton("Easy", new Color(41, 25, 165)); // User's Blue
        easyBtn.addActionListener(e -> gui.requestNewGame(Difficulty.EASY));
        addCenteredButton(centerPanel, easyBtn, btnSize, gbc);

        JButton mediumBtn = createStyledButton("Meduim", new Color(41, 25, 165));
        mediumBtn.addActionListener(e -> gui.requestNewGame(Difficulty.MEDIUM));
        addCenteredButton(centerPanel, mediumBtn, btnSize, gbc);

        JButton hardBtn = createStyledButton("Hard", new Color(41, 25, 165));
        hardBtn.addActionListener(e -> gui.requestNewGame(Difficulty.HARD));
        addCenteredButton(centerPanel, hardBtn, btnSize, gbc);

        add(centerPanel, BorderLayout.CENTER);

        // --- Bottom Panel (Exit Button) ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(new Color(245, 247, 250));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 20));

        JButton exitBtn = createStyledButton("Exit", new Color(220, 53, 69)); // Red background
        exitBtn.addActionListener(e -> {
            UserActionLogger.getInstance().delete();
            System.exit(0);
        });
        bottomPanel.add(exitBtn);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void addCenteredButton(JPanel panel, JButton btn, Dimension size, GridBagConstraints gbc) {
        btn.setPreferredSize(size);
        btn.setMinimumSize(size);
        btn.setMaximumSize(size);
        panel.add(btn, gbc);
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg.brighter());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg);
            }
        });
        return btn;
    }

    private JButton createStyledOutlineButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        Color mainColor = new Color(41, 25, 165);
        btn.setForeground(mainColor);
        btn.setBackground(new Color(245, 247, 250)); // Match panel bg
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Custom Border
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(mainColor, 2),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)));
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(230, 230, 240));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(245, 247, 250));
            }
        });

        return btn;
    }

    private void openFileBrowser() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
        fileChooser.setFileFilter(filter);

        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            try {
                gui.driveGames(path);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading board: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
