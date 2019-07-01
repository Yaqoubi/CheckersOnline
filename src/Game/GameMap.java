package Game;

import Game.GameMove;
import java.util.ArrayList;

//Zakhire Sazi Etelate Bazi Dar Sheya haye in class
class GameMap {
    
    //tayin final hayi baraye 5 noa khoneye bazi
    public static final int 
    blank = 0,
    player1 = 1,
    king1 = 2,
    player2 = 3,
    king2 = 4;

    //array map bazi
    private Piece[][] board;

    public GameMap() {
        board = new Piece[8][8];
        initBoard();
    }

    //init avaliue map bazi
    //chindan mohreha
    public void initBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if ( row % 2 == col % 2 ) { 
                    //gharar dadne mohre ha dar 3 radif bala va payin
                    if (row < 3)
                        board[row][col] = new Player2Piece(row, col);
                    else if (row > 4)
                        board[row][col] = new Player1Piece(row, col); 
                    else 
                        board[row][col] = new BlankPiece(row, col);
                    //khonehaye khaliye bazi
                } else 
                    board[row][col] = new BlankPiece(row, col);
            }
        }
    }
    
    //Gerftan mohreye ye khonye khas
    public Piece getPieceAt(int row, int col) { 
        return board[row][col];
    }

    //anjam harkat
    public void makeMove(GameMove move) { 
        makeMove(move.fromRow, move.fromCol, move.toRow, move.toCol);
    }

    
    //emal taghir be arraye bazi
    public void makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        //jabejayi mohre be khoneye jadid
        board[toRow][toCol] = board[fromRow][fromCol]; 
        board[fromRow][fromCol] = new BlankPiece(fromRow, fromCol);
        if (fromRow - toRow == 2 || fromRow - toRow == -2){ //check kardane inke az noa jump hast ya na
            int jumpRow = (fromRow + toRow) / 2;
            int jumpCol = (fromCol + toCol) / 2;
            board[jumpRow][jumpCol] = new BlankPiece(jumpRow, jumpCol);
        }
        if (toRow == 0 && board[toRow][toCol].toInt() == player1){ //check kardane mohre player aval baraye king shodan
            Player1Piece piece = (Player1Piece)board[toRow][toCol];
            piece.makeKing();
        }
        if (toRow == 7 && board[toRow][toCol].toInt() == player2){ //check kardane mohre player dovom baraye king shodan
            Player2Piece piece = (Player2Piece)board[toRow][toCol];
            piece.makeKing();
        }
    }
    
    
    //gerftan hameye harkat haye mojaz baraye yek pkayer
    public GameMove[] getLegalMoves(int player) {
        //null check
        if (player != player1 && player != player2)
            return null; 
        //check baraye king bodan chon king be 4 jahat mitone harkat kone
        int playerKing;
        if (player == player1){
            playerKing = king1;
        } else {
            playerKing = king2;
        }
        ArrayList moves = new ArrayList(); //ijad arrayi jadid baraye zakhire saziye harkat mojaz
        //baresiye tamame khone haye bazi
        for (int row = 0; row < 8; row++){ 
            for (int col = 0; col < 8; col++){
                //agar moraba male player dade shode hast
                if (board[row][col].toInt() == player || board[row][col].toInt() == playerKing){
                    //baresiye hame halat haye jump
                    if (canJump(player, row, col, row+1, col+1, row+2, col+2))
                        moves.add(new GameMove(row, col, row+2, col+2));
                    if (canJump(player, row, col, row-1, col+1, row-2, col+2))
                        moves.add(new GameMove(row, col, row-2, col+2));
                    if (canJump(player, row, col, row+1, col-1, row+2, col-2))
                        moves.add(new GameMove(row, col, row+2, col-2));
                    if (canJump(player, row, col, row-1, col-1, row-2, col-2))
                        moves.add(new GameMove(row, col, row-2, col-2));
                }
            }
        }
        if (moves.size() == 0){ //agar jumpi nemitonest bezane
            for (int row = 0; row < 8; row++){ //baresiye dobare hameye khone ha bare move mamoli
                for (int col = 0; col < 8; col++){

                    if (board[row][col].toInt() == player || board[row][col].toInt() == playerKing){ 
                        if (canMove(player,row,col,row+1,col+1))
                            moves.add(new GameMove(row,col,row+1,col+1));
                        if (canMove(player,row,col,row-1,col+1))
                            moves.add(new GameMove(row,col,row-1,col+1));
                        if (canMove(player,row,col,row+1,col-1))
                            moves.add(new GameMove(row,col,row+1,col-1));
                        if (canMove(player,row,col,row-1,col-1))
                            moves.add(new GameMove(row,col,row-1,col-1));
                    }
                }
            }
        }
        if (moves.size() == 0){ //agar hich harkat mamoli ham nemitonest bere
            return null; //blcok shode player
        }else { //dargheyre in sorat hame harkati ke peyda kardim ro bar migardonim to ye arraye
            //tabdil list be array
            GameMove[] moveArray = new GameMove[moves.size()];
            for (int i = 0; i < moves.size(); i++){
                moveArray[i] = (GameMove)moves.get(i);
            }
            return moveArray;
        }

    }

    //Baresiye fqt jump ha manande tabeye getLegalMoves
    public GameMove[] getLegalJumpsFrom(int player, int row, int col){
        if (player != player1 && player != player2) 
            return null; 
        int playerKing;

        if (player == player1){
            playerKing = king1;
        }else {
            playerKing = king2;
        }
        ArrayList moves = new ArrayList();
        if (board[row][col].toInt() == player || board[row][col].toInt() == playerKing){
            //if there is a possible jump, add it to list
            if (canJump(player, row, col, row+1, col+1, row+2, col+2))
                moves.add(new GameMove(row, col, row+2, col+2));
            if (canJump(player, row, col, row-1, col+1, row-2, col+2))
                moves.add(new GameMove(row, col, row-2, col+2));
            if (canJump(player, row, col, row+1, col-1, row+2, col-2))
                moves.add(new GameMove(row, col, row+2, col-2));
            if (canJump(player, row, col, row-1, col-1, row-2, col-2))
                moves.add(new GameMove(row, col, row-2, col-2));

        }
        if (moves.size() == 0){
            return null;
        }else { 
            GameMove[] moveArray = new GameMove[moves.size()];
            for (int i = 0; i < moves.size(); i++){
                moveArray[i] = (GameMove)moves.get(i);
            }
            return moveArray;
        }
    }
    
    //check kardan inke jump emakn pazir hast ya na
    private boolean canJump(int player, int r1, int c1, int r2, int c2, int r3, int c3){
        //check baraye inke kharej az board nabashe
        if (r3 < 0 || r3 >= 8 || c3 < 0 || c3 >= 8)
            return false; 
        //agar khoneye maghsad por bashe nemishe jump kard
        if (board[r3][c3].toInt() != blank) 
            return false; 
        //check kardan jump baraye har player jodagane chon dar do jahate motefavetan
        if (player == player1) { 
            if (board[r1][c1].toInt() == player1 && r3 > r1)
                return false; 
            if (board[r2][c2].toInt() != player2 && board[r2][c2].toInt() != king2)
                return false;
            return true; 
        }else { 
            if (board[r1][c1].toInt() == player2 && r3 < r1)
                return false; 
            if (board[r2][c2].toInt() != player1 && board[r2][c2].toInt() != king1) 
                return false; 
            return true;
        }

    }

    //baresiye inke player mitavand mohre ash ro jabeja konad ya na manande canJump
    private boolean canMove(int player, int r1, int c1, int r2, int c2){
        if (r2 < 0 || r2 >= 8 || c2 < 0 || c2 >= 8)
            return false; 
        if (board[r2][c2].toInt() != blank)
            return false;
        if (player == player1) {
            if (board[r1][c1].toInt() == player1 && r2 > r1)
                return false;
            return true; 
        }else { 
            if (board[r1][c1].toInt() == player2 && r2 < r1)
                return false;
            return true; 
        }
    }

}