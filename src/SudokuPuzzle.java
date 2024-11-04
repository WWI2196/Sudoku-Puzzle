import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class SudokuPuzzle extends JFrame {
    private SudokuPanel sudokuPanel;
    private JButton newButton, resetButton, showSolutionButton;
    private JComboBox<String> levelSelector;
    private SudokuSolver solver;
    private int[][] solution;
    private int difficulty;

    public SudokuPuzzle() {
        setTitle("Sudoku Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initializeComponents();
        pack();
        setLocationRelativeTo(null);
    }

    private void initializeComponents() {
        // Initialize solver
        solver = new SudokuSolver();
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create control panel
        JPanel controlPanel = new JPanel(new FlowLayout());
        
        // Level selector
        String[] levels = {"Easy", "Medium", "Hard"};
        levelSelector = new JComboBox<>(levels);
        
        // Buttons
        newButton = new JButton("New");
        resetButton = new JButton("Reset");
        showSolutionButton = new JButton("Show Solution");
        
        // Add action listeners
        newButton.addActionListener(e -> startNewGame());
        resetButton.addActionListener(e -> resetGame());
        showSolutionButton.addActionListener(e -> showSolution());
        
        // Add components to control panel
        controlPanel.add(new JLabel("Level:"));
        controlPanel.add(levelSelector);
        controlPanel.add(newButton);
        controlPanel.add(resetButton);
        controlPanel.add(showSolutionButton);
        
        // Create Sudoku panel
        sudokuPanel = new SudokuPanel();
        
        // Add panels to main panel
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(sudokuPanel, BorderLayout.CENTER);
        
        // Add main panel to frame
        add(mainPanel);
        
        // Start new game
        startNewGame();
    }

    private void startNewGame() {
        resetButton.setEnabled(true);
        String level = (String) levelSelector.getSelectedItem();
        switch (level) {
            case "Easy":
                difficulty = 40; // Will show 40 numbers
                break;
            case "Medium":
                difficulty = 35; // Will show 35 numbers
                break;
            case "Hard":
                difficulty = 30; // Will show 30 numbers
                break;
        }
        
        solution = solver.generateSolution();
        int[][] puzzle = solver.generatePuzzle(solution, difficulty);
        sudokuPanel.setInitialPuzzle(puzzle, solution);
    }

    private void resetGame() {
        sudokuPanel.reset();
    }

    private void showSolution() {
        sudokuPanel.showSolution();
        resetButton.setEnabled(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SudokuPuzzle().setVisible(true);
        });
    }
}

class SudokuPanel extends JPanel {
    private SudokuCell[][] cells;
    private int[][] solution;
    private int[][] initial;
    
    public SudokuPanel() {
        setLayout(new GridLayout(9, 9));
        cells = new SudokuCell[9][9];
        
        // Create cells
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                cells[row][col] = new SudokuCell(row, col);
                add(cells[row][col]);
            }
        }
        
        // Add borders for 3x3 sub-grids
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
    }
    
    public void setInitialPuzzle(int[][] puzzle, int[][] solution) {
        this.solution = solution;
        this.initial = new int[9][9];
        
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                cells[row][col].setValue(puzzle[row][col]);
                cells[row][col].setEditable(puzzle[row][col] == 0);
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
    
    public void showSolution() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                cells[row][col].setValue(solution[row][col]);
                cells[row][col].setBackground(Color.WHITE);
            }
        }
    }
}

class SudokuCell extends JTextField {
    private int row, col;
    
    public SudokuCell(int row, int col) {
        this.row = row;
        this.col = col;
        
        setHorizontalAlignment(JTextField.CENTER);
        setFont(new Font("Arial", Font.BOLD, 20));
        
        // Add document listener for input validation
        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { validateInput(); }
            @Override
            public void removeUpdate(DocumentEvent e) { validateInput(); }
            @Override
            public void changedUpdate(DocumentEvent e) { validateInput(); }
        });
        
        // Add borders based on position
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
                setBackground(Color.RED);
                return;
            }
            setBackground(Color.WHITE);
        } catch (NumberFormatException e) {
            setText("");
            setBackground(Color.WHITE);
        }
    }
}

class SudokuSolver {
    private Random random = new Random();
    
    public int[][] generateSolution() {
        int[][] grid = new int[9][9];
        fillGrid(grid);
        return grid;
    }
    
    private boolean fillGrid(int[][] grid) {
        int[] unassigned = findUnassignedLocation(grid);
        if (unassigned == null) return true;
        
        int row = unassigned[0];
        int col = unassigned[1];
        
        // Try digits 1-9 in random order
        Integer[] nums = {1,2,3,4,5,6,7,8,9};
        shuffleArray(nums);
        
        for (int num : nums) {
            if (isSafe(grid, row, col, num)) {
                grid[row][col] = num;
                if (fillGrid(grid)) return true;
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
        
        // Create list of all positions
        java.util.List<Integer> positions = new java.util.ArrayList<>();
        for (int i = 0; i < 81; i++) positions.add(i);
        
        // Shuffle positions
        java.util.Collections.shuffle(positions);
        
        // Remove numbers to create puzzle
        int numbersToRemove = 81 - numFilled;
        for (int i = 0; i < numbersToRemove; i++) {
            int pos = positions.get(i);
            puzzle[pos/9][pos%9] = 0;
        }
        
        return puzzle;
    }
    
    private int[] findUnassignedLocation(int[][] grid) {
        for (int row = 0; row < 9; row++)
            for (int col = 0; col < 9; col++)
                if (grid[row][col] == 0)
                    return new int[]{row, col};
        return null;
    }
    
    private boolean isSafe(int[][] grid, int row, int col, int num) {
        // Check row
        for (int x = 0; x < 9; x++)
            if (grid[row][x] == num) return false;
        
        // Check column
        for (int x = 0; x < 9; x++)
            if (grid[x][col] == num) return false;
        
        // Check 3x3 box
        int startRow = row - row % 3, startCol = col - col % 3;
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (grid[i + startRow][j + startCol] == num) return false;
        
        return true;
    }
    
    private void shuffleArray(Integer[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }
}