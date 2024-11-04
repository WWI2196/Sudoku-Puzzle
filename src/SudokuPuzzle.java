// Save this as SudokuPuzzle.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import java.util.Random;

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

    private JButton undoButton;
    private JButton redoButton;
    private JButton hintButton;
    private JButton pauseButton;
    private JButton saveButton;
    private JButton loadButton;
    private int hintsRemaining = 3;
    final Color HINT_CELL_COLOR = new Color(200, 200, 255); // Light blue
    private boolean isPaused = false;
    private java.util.Stack<GameMove> undoStack = new java.util.Stack<>();
    private java.util.Stack<GameMove> redoStack = new java.util.Stack<>();
    private int mistakeCount = 0;

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
        sudokuPanel = new SudokuBoard();
        
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
        solver = new SudokuGenerator();
        gameTimer = new Timer(1000, e -> updateTimer());
        gameTimer.start();
        
        // Pack and center
        pack();
        setLocationRelativeTo(null);
        setResizable(false);

        initializeButtons();
        initializeUI();
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

    private void initializeButtons() {
        // Existing buttons setup...
        
        undoButton = new JButton("Undo");
        redoButton = new JButton("Redo");
        hintButton = new JButton("Hint (" + hintsRemaining + ")");
        pauseButton = new JButton("Pause");
        saveButton = new JButton("Save");
        loadButton = new JButton("Load");
        
        undoButton.addActionListener(e -> undo());
        redoButton.addActionListener(e -> redo());
        hintButton.addActionListener(e -> giveHint());
        pauseButton.addActionListener(e -> togglePause());
        saveButton.addActionListener(e -> saveGame());
        loadButton.addActionListener(e -> loadGame());
        
        // Add keyboard shortcuts
        KeyStroke undoKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK);
        KeyStroke redoKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK);
        
        undoButton.registerKeyboardAction(e -> undo(), undoKeyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        redoButton.registerKeyboardAction(e -> redo(), redoKeyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void undo() {
        if (!undoStack.isEmpty() && gameActive) {
            GameMove move = undoStack.pop();
            redoStack.push(move);
            sudokuPanel.setCellValue(move.row, move.col, move.oldValue);
            updateButtons();
        }
    }

    private void redo() {
        if (!redoStack.isEmpty() && gameActive) {
            GameMove move = redoStack.pop();
            undoStack.push(move);
            sudokuPanel.setCellValue(move.row, move.col, move.newValue);
            updateButtons();
        }
    }

    private void giveHint() {
        if (hintsRemaining > 0 && gameActive) {
            // Get the currently focused cell
            Component focusedComponent = getFocusOwner();
            if (focusedComponent instanceof SudokuTile) {
                SudokuTile cell = (SudokuTile) focusedComponent;
                // Only give hint if cell is empty and editable
                if (cell.getText().isEmpty() && cell.isEditable()) {
                    int row = cell.getRow();
                    int col = cell.getCol();
                    int correctValue = solver.getSolution()[row][col];
                    cell.setValue(correctValue);
                    cell.setBackground(HINT_CELL_COLOR);
                    cell.setEditable(false); // Prevent editing of hint cells
                    
                    hintsRemaining--;
                    hintButton.setText("Hint (" + hintsRemaining + ")");
                    if (hintsRemaining == 0) {
                        hintButton.setEnabled(false);
                    }
                }
            }
        }
    }

    private void togglePause() {
        isPaused = !isPaused;
        if (isPaused) {
            gameTimer.stop();
            sudokuPanel.setVisible(false);
            pauseButton.setText("Resume");
            statusLabel.setText("Game Paused");
        } else {
            gameTimer.start();
            sudokuPanel.setVisible(true);
            pauseButton.setText("Pause");
            statusLabel.setText("Game Resumed");
        }
    }

    private void saveGame() {
        // Implement game state saving to file
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream("sudoku_save.dat"))) {
            SudokuGameState state = new SudokuGameState(
                sudokuPanel.getCurrentValues(),
                solver.getSolution(),
                elapsedTime,
                hintsRemaining,
                mistakeCount,
                levelSelector.getSelectedItem().toString()
            );
            oos.writeObject(state);
            statusLabel.setText("Game saved successfully");
        } catch (IOException e) {
            statusLabel.setText("Error saving game");
        }
    }

    private void loadGame() {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream("sudoku_save.dat"))) {
            SudokuGameState state = (SudokuGameState) ois.readObject();
            // Restore game state
            sudokuPanel.setInitialPuzzle(state.getCurrentValues(), state.getSolution());
            elapsedTime = state.getElapsedTime();
            hintsRemaining = state.getHintsRemaining();
            mistakeCount = state.getMistakeCount();
            levelSelector.setSelectedItem(state.getLevel());
            gameActive = true;
            statusLabel.setText("Game loaded successfully");
        } catch (Exception e) {
            statusLabel.setText("Error loading game");
        }
    }

    private void updateButtons() {
        undoButton.setEnabled(!undoStack.isEmpty() && gameActive);
        redoButton.setEnabled(!redoStack.isEmpty() && gameActive);
    }

    private void initializeUI() {
        // Create control panel
        JPanel controlPanel = new JPanel(new GridLayout(2, 3, 5, 5));
        controlPanel.add(undoButton);
        controlPanel.add(redoButton);
        controlPanel.add(hintButton);
        controlPanel.add(pauseButton);
        controlPanel.add(saveButton);
        controlPanel.add(loadButton);
        
        // Add keyboard shortcuts
        addKeyBindings();
    }

    private void addKeyBindings() {
        JRootPane rootPane = getRootPane();
        InputMap im = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = rootPane.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), "newGame");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "saveGame");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK), "loadGame");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK), "pauseGame");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK), "hint");

        am.put("newGame", new AbstractAction() { 
            public void actionPerformed(ActionEvent e) { startNewGame(); }
        });
        // Add other actions similarly
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
