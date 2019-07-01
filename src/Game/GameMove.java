package Game;

class GameMove {

    int fromRow, fromCol; //row va col mabda
    int toRow, toCol; //row va col maghsad

    GameMove(int r1, int c1, int r2, int c2) {

        fromRow = r1;
        fromCol = c1;
        toRow = r2;
        toCol = c2;

    }

    boolean jumpCheck() { //check kardane inke aya jump hast harkat ya na
        return (fromRow - toRow == 2 || fromRow - toRow == -2);
    }

}