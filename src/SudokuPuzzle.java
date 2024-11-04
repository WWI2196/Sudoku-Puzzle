import javax.swing.*;
import java.awt.*;

import java.util.Random;

/*
    Main game class to controls the Sudoku game interface and logic. it handles the game window, menus, buttons and game flow.
 */

public class SudokuPuzzle extends JFrame {
    protected SudokuBoard sudokuPanel;
    protected JButton newButton;
    protected JButton resetButton;
    protected JButton showSolutionButton;
    protected JComboBox<String> levelSelector;
    protected JLabel timerLabel;
    protected JLabel statusLabel;
    protected Timer gameTimer;
    protected boolean gameActive = false;
    protected SudokuGenerator solver;
    private int elapsedTime = 0;


    public SudokuPuzzle() {
        setTitle("Sudoku Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Main container
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel for timer
        JPanel topPanel = new JPanel(new BorderLayout());

        JPanel levelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        levelSelector = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        JLabel levelLabel = new JLabel("Level: ");
        levelPanel.add(levelLabel);
        levelPanel.add(levelSelector);
        
        // Timer panel
        JPanel timerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        timerLabel = new JLabel("Time: 00:00");
        timerPanel.add(timerLabel);
        
        topPanel.add(levelPanel, BorderLayout.WEST);
        topPanel.add(timerPanel, BorderLayout.EAST);
        
        // Sudoku panel
        sudokuPanel = new SudokuBoard();
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        // Create buttons
        newButton = new JButton("New Game");
        resetButton = new JButton("Reset");
        showSolutionButton = new JButton("Show Solution");

        Dimension buttonSize = new Dimension(120, 30);
        newButton.setPreferredSize(buttonSize);
        resetButton.setPreferredSize(buttonSize);
        showSolutionButton.setPreferredSize(buttonSize);
        
        // Add action listeners
        newButton.addActionListener(e -> startNewGame());
        resetButton.addActionListener(e -> resetGame());
        showSolutionButton.addActionListener(e -> showSolution());
        
        // Set initial button states
        resetButton.setEnabled(false);
        showSolutionButton.setEnabled(false);

        buttonPanel.add(newButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(showSolutionButton);
        
        // Status label
        statusLabel = new JLabel("Select difficulty and press New Game to start", SwingConstants.CENTER);
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statusPanel.add(statusLabel);
        
        // Set main panel
        contentPane.add(topPanel);
        contentPane.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPane.add(sudokuPanel);
        contentPane.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPane.add(buttonPanel);
        contentPane.add(Box.createRigidArea(new Dimension(0, 5)));
        contentPane.add(statusPanel);

        setContentPane(contentPane);
        
        // Initialize the timer
        solver = new SudokuGenerator();
        gameTimer = new Timer(1000, e -> updateTimer());
        gameTimer.start();

        pack();
        setLocationRelativeTo(null);
        setResizable(false);

    }

    private void startNewGame() {
        resetButton.setEnabled(true);
        showSolutionButton.setEnabled(true);
        gameActive = true;
        
        String level = (String) levelSelector.getSelectedItem();
        int difficulty;
        Random random = new Random();
        
        difficulty = switch (level) {
            case "Easy" -> 40 + random.nextInt(11);
            case "Medium" -> 30 + random.nextInt(11);
            default -> 20 + random.nextInt(11);
        }; // Hard
        
        int[][] solution = solver.generateSolution();
        int[][] puzzle = solver.generatePuzzle(solution, difficulty);
        sudokuPanel.setInitialPuzzle(puzzle, solution);
        
        elapsedTime = 0;
        gameTimer.restart();
        statusLabel.setText("Game started - " + level + " level");
    }

    private void resetGame() {
        if (gameActive) {
            sudokuPanel.reset();
            showSolutionButton.setEnabled(true);
            statusLabel.setText("Game reset - all user entries cleared");
        }
    }

    private void showSolution() {
        if (gameActive) {
            sudokuPanel.showSolution();
            resetButton.setEnabled(false);
            showSolutionButton.setEnabled(false);
            gameTimer.stop();
            statusLabel.setText("Solution shown - Game Over");
            gameActive = false;
        }
    }

    private void updateTimer() {
        elapsedTime++;
        int minutes = elapsedTime / 60;
        int seconds = elapsedTime % 60;
        timerLabel.setText(String.format("Time: %02d:%02d", minutes, seconds));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            }
            SudokuPuzzle frame = new SudokuPuzzle();
            frame.pack();
            frame.setVisible(true);
        });
    }
}
