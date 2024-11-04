import java.io.*;


class SudokuGameState implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int[][] currentValues;
    private final int[][] solution;
    private final int elapsedTime;
    private final int hintsRemaining;
    private final int mistakeCount;
    private final String level;
    
    public SudokuGameState(int[][] currentValues, int[][] solution, int elapsedTime,
                    int hintsRemaining, int mistakeCount, String level) {
        this.currentValues = currentValues;
        this.solution = solution;
        this.elapsedTime = elapsedTime;
        this.hintsRemaining = hintsRemaining;
        this.mistakeCount = mistakeCount;
        this.level = level;
    }
    
    // Add getters
    public int[][] getCurrentValues() {
        return currentValues;
    }
    
    public int[][] getSolution() {
        return solution;
    }
    
    public int getElapsedTime() {
        return elapsedTime;
    }
    
    public int getHintsRemaining() {
        return hintsRemaining;
    }
    
    public int getMistakeCount() {
        return mistakeCount;
    }
    
    public String getLevel() {
        return level;
    }
}
