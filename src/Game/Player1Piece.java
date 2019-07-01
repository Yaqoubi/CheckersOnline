package Game;

public class Player1Piece extends Piece implements IsKing{
    int priorty;
    
    public  Player1Piece(int row,int col){
        super(row, col);
        priorty = 1;
    }
    
    @Override
    public void makeKing() {
        priorty = 2;
    }

    @Override
    int toInt() {
        return priorty;
    }
}
