# Java Sudoku GUI Game

A implementation of the classic Sudoku puzzle game with a graphical user interface built in Java Swing. The game offers multiple difficulty levels, interactive features, and a sophisticated cell highlighting system for an engaging puzzle-solving experience.

## Features

### Core Game Features
- **Multiple Difficulty Levels**:
  - Easy Mode: 35-45 pre-filled numbers
  - Medium Mode: 30-40 pre-filled numbers
  - Hard Mode: 25-35 pre-filled numbers

### Game Controls
- **Basic Controls**:
  - New Game: Start a fresh game
  - Reset: Clear all user-entered values
  - Show Solution: Display the complete solution
  - Timer to track solving duration
 
### Interactive Grid Features
- **9x9 Sudoku Grid** with visually distinct 3x3 sub-regions
- **Smart Cell Interaction**:
  - Automatic highlighting of related cells (row, column, and 3x3 box)
  - Keyboard navigation between cells using arrow keys
  - Real-time error detection with visual feedback
  - Invalid entries highlighted in red
  - Visual distinction between initial and user-filled cells

## Technical Implementation

### Key Components
- Built using Java Swing for the GUI
- Implements Model-View-Controller pattern
- Custom cell highlighting system
- Input validation
- Automated puzzle generation and solution verification

### Classes Structure
- `SudokuPuzzle`: Main game window and control logic
- `SudokuPanel`: Grid panel implementation
- `SudokuCell`: Individual cell implementation with input handling
- `SudokuSolver`: Puzzle generation and solution verification
- `GameState`: Game state serialization

## Building and Running

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- Java Runtime Environment (JRE)

### Compilation
```bash
javac SudokuPuzzle.java
```

### Running the Game
```bash
java SudokuPuzzle
```

## Gameplay Instructions

1. Select difficulty level from the dropdown menu
2. Click "New Game" or press Ctrl+N to start
3. Fill cells using number keys 1-9
4. Use arrow keys to navigate between cells
5. Incorrect entries will be highlighted in red
6. Complete the puzzle by filling all cells correctly

## Contributing

Feel free to fork this repository and submit pull requests for any improvements. Please ensure your code follows the existing style and includes appropriate comments.

## License

This project is available under the MIT License. See the LICENSE file for more details.
