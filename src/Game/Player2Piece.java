package Game;

public class Player2Piece extends Piece implements IsKing{
    int priorty;
    
    public  Player2Piece(int row,int col){
        super(row, col);
        priorty = 3;
    }
    

    @Override
    public void makeKing() {
        priorty = 4;
    }
    
    @Override
    int toInt() {
        return 3;
    }
}
