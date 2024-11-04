
class GameMove {
    int row;
    int col;
    int oldValue;
    int newValue;
    
    public GameMove(int row, int col, int oldValue, int newValue) {
        this.row = row;
        this.col = col;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
}
