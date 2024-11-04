import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.awt.Point;

class SudokuBoard extends JPanel {
    private final SudokuTile[][] cells;
    private int[][] solution;
    private int[][] initial;
    private final Color INITIAL_CELL_COLOR = new Color(240, 240, 240);
    
    public SudokuBoard() {
        setLayout(new GridLayout(9, 9, 0, 0));
        cells = new SudokuTile[9][9];
        
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                cells[row][col] = new SudokuTile(row, col);
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

    public Point getRandomEmptyCell() {
        List<Point> emptyCells = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (cells[i][j].getText().isEmpty()) {
                    emptyCells.add(new Point(i, j));
                }
            }
        }
        if (emptyCells.isEmpty()) return null;
        return emptyCells.get(new Random().nextInt(emptyCells.size()));
    }

    public void setCellValue(int row, int col, int value) {
        cells[row][col].setValue(value);
    }

    public int[][] getCurrentValues() {
        int[][] values = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                String text = cells[i][j].getText();
                values[i][j] = text.isEmpty() ? 0 : Integer.parseInt(text);
            }
        }
        return values;
    }
}
