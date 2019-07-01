package Game;

public abstract class Piece {
    int row,col;
    
    public Piece(int row, int col){
        this.row = row;
        this.col = col;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }
    
    abstract int toInt();
}
