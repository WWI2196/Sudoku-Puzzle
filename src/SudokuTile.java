import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;

class SudokuTile extends JTextField {
    private final int row, col;
    
    public SudokuTile(int row, int col) {
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

        addHighlightListener();
    }
    
    public void setValue(int value) {
        setText(value == 0 ? "" : String.valueOf(value));
    }
    
    private void validateInput() {
        String text = getText();
        
        try {
            int value = Integer.parseInt(text);
            if (value < 1 || value > 9) {
                setBackground(new Color(255, 200, 200));
                setText("");
            } else {
                if (SwingUtilities.getWindowAncestor(this) != null) {
                    ((SudokuBoard) getParent()).checkCurrentProgress();
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
            if (parent instanceof SudokuBoard) {
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

    private void addHighlightListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEditable()) {
                    highlightRelatedCells(true);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                highlightRelatedCells(false);
            }
        });
    }

    private void highlightRelatedCells(boolean highlight) {
        Color highlightColor = new Color(240, 240, 255);
        
        // Iterate through related cells
        for (int i = 0; i < 9; i++) {
            // Row cells
            SudokuTile rowCell = (SudokuTile)getParent().getComponent(row * 9 + i);
            updateCellHighlight(rowCell, highlight, highlightColor);
            
            // Column cells
            SudokuTile colCell = (SudokuTile)getParent().getComponent(i * 9 + col);
            updateCellHighlight(colCell, highlight, highlightColor);
        }
        
        // 3x3 box cells
        int boxRow = row - row % 3;
        int boxCol = col - col % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                SudokuTile boxCell = (SudokuTile)getParent().getComponent((boxRow + i) * 9 + boxCol + j);
                updateCellHighlight(boxCell, highlight, highlightColor);
            }
        }
    }

    private void updateCellHighlight(SudokuTile cell, boolean highlight, Color highlightColor) {
        Color currentBg = cell.getBackground();
        
        // Don't change color if cell shows an error (red background)
        if (currentBg.getRed() == 255 && currentBg.getGreen() == 200 && currentBg.getBlue() == 200) {
            return;
        }
        
        // Don't change color if cell is an initial cell (light gray)
        if (currentBg.getRed() == 240 && currentBg.getGreen() == 240 && currentBg.getBlue() == 240) {
            return;
        }
        
        cell.setBackground(highlight ? highlightColor : Color.WHITE);
    }

    // Add getters for row and col
    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
