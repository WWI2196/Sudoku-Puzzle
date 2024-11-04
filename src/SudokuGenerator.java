import java.util.Random;

class SudokuGenerator {
    private final Random random = new Random();
    private int[][] currentSolution;
    
    public int[][] generateSolution() {
        int[][] grid = new int[9][9];
        fillGrid(grid);
        currentSolution = grid;
        return grid;
    }
    
    public int[][] getSolution() {
        return currentSolution;
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