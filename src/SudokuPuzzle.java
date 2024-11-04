// Save this as SudokuPuzzle.java
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.util.Random;

public class SudokuPuzzle extends JFrame {
    protected SudokuPanel sudokuPanel;
    protected JButton newButton;
    protected JButton resetButton;
    protected JButton showSolutionButton;
    protected JComboBox<String> levelSelector;
    protected JLabel timerLabel;
    protected JLabel statusLabel;
    protected Timer gameTimer;
    protected boolean gameActive = false;
    protected SudokuSolver solver;
    private int elapsedTime = 0;

    public SudokuPuzzle() {
        setTitle("Sudoku Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Main container
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel for level and timer
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // Level selector panel
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
        sudokuPanel = new SudokuPanel();
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        // Create and style buttons
        newButton = new JButton("New Game");
        resetButton = new JButton("Reset");
        showSolutionButton = new JButton("Show Solution");
        
        // Style buttons
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
        
        // Add buttons to panel
        buttonPanel.add(newButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(showSolutionButton);
        
        // Status label
        statusLabel = new JLabel("Select difficulty and press New Game to start", SwingConstants.CENTER);
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statusPanel.add(statusLabel);
        
        // Add all components to main panel
        contentPane.add(topPanel);
        contentPane.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPane.add(sudokuPanel);
        contentPane.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPane.add(buttonPanel);
        contentPane.add(Box.createRigidArea(new Dimension(0, 5)));
        contentPane.add(statusPanel);
        
        // Set content pane
        setContentPane(contentPane);
        
        // Initialize solver and timer
        solver = new SudokuSolver();
        gameTimer = new Timer(1000, e -> updateTimer());
        gameTimer.start();
        
        // Pack and center
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
        
        switch (level) {
            case "Easy":
                difficulty = 35 + random.nextInt(11);
                break;
            case "Medium":
                difficulty = 30 + random.nextInt(11);
                break;
            default: // Hard
                difficulty = 25 + random.nextInt(11);
                break;
        }
        
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
            } catch (Exception e) {
                e.printStackTrace();
            }
            SudokuPuzzle frame = new SudokuPuzzle();
            frame.pack();
            frame.setVisible(true);
        });
    }
}

class SudokuPanel extends JPanel {
    private SudokuCell[][] cells;
    private int[][] solution;
    private int[][] initial;
    private final Color INITIAL_CELL_COLOR = new Color(240, 240, 240);
    
    public SudokuPanel() {
        setLayout(new GridLayout(9, 9, 0, 0));
        cells = new SudokuCell[9][9];
        
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                cells[row][col] = new SudokuCell(row, col);
                add(cells[row][col]);
            }
        }
        
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        setPreferredSize(new Dimension(450, 450));
    }
    
    public void setInitialPuzzle(int[][] puzzle, int[][] solution) {
        this.solution = solution;
        this.initial = new int[9][9];
        
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                cells[row][col].setValue(puzzle[row][col]);
                cells[row][col].setEditable(puzzle[row][col] == 0);
                if (puzzle[row][col] != 0) {
                    cells[row][col].setBackground(INITIAL_CELL_COLOR);
                    cells[row][col].setForeground(Color.BLACK);
                }
                initial[row][col] = puzzle[row][col];
            }
        }
    }
    
    public void reset() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (initial[row][col] == 0) {
                    cells[row][col].setValue(0);
                    cells[row][col].setBackground(Color.WHITE);
                }
            }
        }
    }
    
    public boolean isComplete() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (cells[row][col].getText().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public void showSolution() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                cells[row][col].setValue(solution[row][col]);
                cells[row][col].setEditable(false);
            }
        }
    }
    
    public boolean checkCurrentProgress() {
        boolean isCorrect = true;
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                String value = cells[row][col].getText();
                if (!value.isEmpty()) {
                    int num = Integer.parseInt(value);
                    if (num != solution[row][col]) {
                        cells[row][col].setBackground(new Color(255, 200, 200));
                        isCorrect = false;
                    } else {
                        if (initial[row][col] == 0) {
                            cells[row][col].setBackground(Color.WHITE);
                        }
                    }
                }
            }
        }

        if (isCorrect && isComplete()) {
            if (SwingUtilities.getWindowAncestor(this) instanceof SudokuPuzzle) {
                SudokuPuzzle game = (SudokuPuzzle) SwingUtilities.getWindowAncestor(this);
                game.gameTimer.stop();
                game.statusLabel.setText("Congratulations! Puzzle completed!");
                game.gameActive = false;
                game.showSolutionButton.setEnabled(false);
                game.resetButton.setEnabled(false);
            }
        }

        return isCorrect;
    }
}

class SudokuCell extends JTextField {
    private final int row, col;
    
    public SudokuCell(int row, int col) {
        this.row = row;
        this.col = col;
        
        setHorizontalAlignment(JTextField.CENTER);
        setFont(new Font("Arial", Font.BOLD, 20));
        
        // Document listener for input validation
        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { validateInput(); }
            @Override
            public void removeUpdate(DocumentEvent e) { validateInput(); }
            @Override
            public void changedUpdate(DocumentEvent e) { validateInput(); }
        });

        // Key listener for input and navigation
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (getText().length() >= 1 && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                    return;
                }
                if (!Character.isDigit(c) || c == '0') {
                    e.consume();
                }
            }
            
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP -> moveFocus(row - 1, col);
                    case KeyEvent.VK_DOWN -> moveFocus(row + 1, col);
                    case KeyEvent.VK_LEFT -> moveFocus(row, col - 1);
                    case KeyEvent.VK_RIGHT -> moveFocus(row, col + 1);
                }
            }
        });
        
        // Style borders for 3x3 box separation
        int top = row % 3 == 0 ? 2 : 1;
        int left = col % 3 == 0 ? 2 : 1;
        int bottom = row == 8 ? 2 : 1;
        int right = col == 8 ? 2 : 1;
        
        setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK));
    }
    
    public void setValue(int value) {
        setText(value == 0 ? "" : String.valueOf(value));
    }
    
    private void validateInput() {
        String text = getText();
        if (text.isEmpty()) {
            setBackground(Color.WHITE);
            return;
        }

        try {
            int value = Integer.parseInt(text);
            if (value < 1 || value > 9) {
                setBackground(new Color(255, 200, 200));
                setText("");
            } else {
                if (SwingUtilities.getWindowAncestor(this) != null) {
                    ((SudokuPanel) getParent()).checkCurrentProgress();
                }
            }
        } catch (NumberFormatException e) {
            setText("");
            setBackground(Color.WHITE);
        }
    }
    
    private void moveFocus(int newRow, int newCol) {
        if (newRow >= 0 && newRow < 9 && newCol >= 0 && newCol < 9) {
            Container parent = getParent();
            if (parent instanceof SudokuPanel) {
                Component[] components = parent.getComponents();
                components[newRow * 9 + newCol].requestFocus();
            }
        }
    }
    
    @Override
    public void setEditable(boolean editable) {
        super.setEditable(editable);
        setFocusable(editable);
        if (!editable) {
            setBackground(new Color(240, 240, 240));
        }
    }
}

class SudokuSolver {
    private final Random random = new Random();
    
    public int[][] generateSolution() {
        int[][] grid = new int[9][9];
        fillGrid(grid);
        return grid;
    }
    
    private boolean fillGrid(int[][] grid) {
        int[] unassigned = findUnassignedLocation(grid);
        if (unassigned == null) {
            return true;
        }
        
        int row = unassigned[0];
        int col = unassigned[1];
        
        Integer[] nums = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        shuffleArray(nums);
        
        for (int num : nums) {
            if (isSafe(grid, row, col, num)) {
                grid[row][col] = num;
                
                if (fillGrid(grid)) {
                    return true;
                }
                
                grid[row][col] = 0;
            }
        }
        return false;
    }
    
    public int[][] generatePuzzle(int[][] solution, int numFilled) {
        int[][] puzzle = new int[9][9];
        for (int i = 0; i < 9; i++) {
            System.arraycopy(solution[i], 0, puzzle[i], 0, 9);
        }
        
        java.util.List<Integer> positions = new java.util.ArrayList<>();
        for (int i = 0; i < 81; i++) {
            positions.add(i);
        }
        
        java.util.Collections.shuffle(positions, random);
        
        int numbersToRemove = 81 - numFilled;
        for (int i = 0; i < numbersToRemove; i++) {
            int pos = positions.get(i);
            int row = pos / 9;
            int col = pos % 9;
            puzzle[row][col] = 0;
        }
        
        return puzzle;
    }
    
    // Finds an empty cell in the grid
    private int[] findUnassignedLocation(int[][] grid) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (grid[row][col] == 0) {
                    return new int[]{row, col};
                }
            }
        }
        return null;
    }
    
    // Checks if it's safe to place a number in a cell
    private boolean isSafe(int[][] grid, int row, int col, int num) {
        // Check row
        for (int x = 0; x < 9; x++) {
            if (grid[row][x] == num) {
                return false;
            }
        }
        
        // Check column
        for (int x = 0; x < 9; x++) {
            if (grid[x][col] == num) {
                return false;
            }
        }
        
        // Check 3x3 box
        int startRow = row - row % 3;
        int startCol = col - col % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (grid[i + startRow][j + startCol] == num) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    // Utility method to shuffle array elements
    private void shuffleArray(Integer[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            Integer temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }
}