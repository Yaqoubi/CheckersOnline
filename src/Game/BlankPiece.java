package Game;

public class BlankPiece extends Piece{
    public BlankPiece(int row,int col){
        super(row, col);
    }
    
       @Override
    int toInt() {
        return 0;
    }
}
