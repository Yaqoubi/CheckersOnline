package Game;

import Game.GameMap;
import Game.GameMove;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.PrintWriter;

//ijad class board va extend on az jpanel
public class GameBoard extends JPanel implements ActionListener, MouseListener { 

    GameMap boardData; //ijad obj jadidi baraye zakhire sazi dade haye bazi
    boolean gameInProgress; //booleani baraye inke bebinim bazi darhale ejraye ya na
    int turn; //contorl nobat
    int selectedRow, selectedCol; //zakhire khoneye entekhab shode
    GameMove[] legalMoves; //arrayiy baraye zakhire harkat haye mojaz
    String Player1; //esm avalin player
    String Player2; //esm dovomin player
    int whichSide;//moshkhas kardane inke frame baz shode aval bazi mikone ya dovom
    PrintWriter output;
    JFrame gameFrame;
    String player1Msg;//handle kardan log bazi
    String player2Msg;//handle kardan log bazi
    
    public GameBoard(String p1Name,String p2Name,int whichSide,PrintWriter output,JFrame gameFrame) {
        this.whichSide = whichSide;
        this.output = output;
        this.gameFrame = gameFrame;
        addMouseListener(this); //ezafe kardan moouse listner baraye handle kardan click ha
        
        //init safheye bazi va ijad game jadid
        boardData = new GameMap();
        Player1 = p1Name;
        Player2 = p2Name;
        if(whichSide == 1){
            player1Msg = p1Name;
            player2Msg = p2Name;
        }else{
            player1Msg = p2Name;
            player2Msg = p1Name;
        }
        NewGame();
    }

    //handle inke user disconnect ro zad
    public void actionPerformed(ActionEvent evt) { 
       
    }
    
    //ijad ye bazi jadid
    void NewGame() {
        boardData.initBoard(); 
        turn = GameMap.player1; //tayin nibat
        legalMoves = boardData.getLegalMoves(GameMap.player1); //gerftan hameye harkat haye mojaz
        selectedRow = -1; //-1 be in maniye ke hichi select nashode hanoz
        gameInProgress = true;
        repaint(); //repaint

    }

    //handle clic kbaroye disconnect btn
    void disconnect() {
        sendDisconnectCommand();
    }

    //handle game over va namayesh dialoge baroye thread
    void gameOver(String str) { 
        gameInProgress = false;
        Thread thread = new Thread(new ThreadHandling(str));
        thread.run();
        
    }
    //inner class hamin mozoye bala
    class ThreadHandling implements Runnable {
        String msg;
        
        public ThreadHandling(String msg){
            this.msg = msg;
        }
        public void run(){
            Object stringArray[] = { "OK!"};
                    int result = JOptionPane.showOptionDialog(gameFrame, msg, "Win - Lose",
                            JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, stringArray,
                            stringArray[0]);
                    if(0 == result){
                        sendDisconnectCommand();
                    }else{
                        sendDisconnectCommand();
                    }
        } 
    }
    
    //handle inke karbar ro yek moraba click kone
    public void mousePressed(MouseEvent evt) { 
        gameFrame.repaint();
        if (!gameInProgress){
        }else { 
            //peyda kardan khoneye morde nazar
            int col = (evt.getX() - 2) / 50; 
            int row = (evt.getY() - 2) / 50; 
            if (col >= 0 && col < 8 && row >= 0 && row < 8) 
                ClickedSquare(row,col);
        }
    }
    
    //tahlil halat haye mojaz
    void ClickedSquare(int row, int col) {
        if( (whichSide == 1 && turn == GameMap.player1) || (whichSide == 2 && turn == GameMap.player2) ){
            for (int i = 0; i < legalMoves.length; i++){ //baresiye tamami halat ha
                if (legalMoves[i].fromRow == row && legalMoves[i].fromCol == col) { //check kardan inke mohre block nabahe
                    selectedRow = row; 
                    selectedCol = col;
                    //namayesh jahayi ke mitone bere va mohrehayi ke mitone tekon bede
                    repaint();
                    return;
            }
        }
            if (selectedRow < 0) {//agar hichi entekhab nakarde bashe
                return;
            }

            for (int i = 0; i < legalMoves.length; i++){ //baresi tamami halat haye mojaz
                if (legalMoves[i].fromRow == selectedRow && legalMoves[i].fromCol == selectedCol //agar ye mohreyi entekhab karde ke mishe tekon dad
                    && legalMoves[i].toRow == row && legalMoves[i].toCol == col) { 
                    MakeMove(legalMoves[i]); //tekon dadane mohre
                    return;
                }
            }
        }else{
            //agar nobate harife va bayad wait kard baraye harkat harif
            //message.setText("Waiting For Oppenent Move!");  
        }
    }
    
    //ersal peygham disconnect be server hamanande private chat
    void sendDisconnectCommand(){
        String commandToServer = "||"+Player1+"//"+Player2+" "+"Close";
        output.println(commandToServer);
        gameFrame.dispose();
    }

    //tabeyi baraye harkat mohre
    void MakeMove(GameMove move) {
        //ersal move be server ke server be traf moghabel harkat anjam shode ro enteghal bede
        String commandToServer = "||"+Player1+"//"+Player2+" "+move.fromRow + move.fromCol+" "+move.toRow + move.toCol;
        output.println(commandToServer);
        boardData.makeMove(move);//update data bazi ba move jadid
        //check kardan baraye jump haye pey dar pey
        if (move.jumpCheck()) {
            legalMoves = boardData.getLegalJumpsFrom(turn, move.toRow, move.toCol);
            if (legalMoves != null) {
                selectedRow = move.toRow; 
                selectedCol = move.toCol; 
                repaint(); 
                return;
            }
        }
        
        //agar nobate player aval bode
        if (turn == GameMap.player1) { 
            turn = GameMap.player2; //hala nobate dovomiye
            legalMoves = boardData.getLegalMoves(turn); //gerftan harkat mojaz baraye player dge
            if (legalMoves == null) //agar hich harkati nadashte bashe pas bakhte va player aval barande shode
                gameOver(player1Msg + " wins!");
        } else { 
            turn = GameMap.player1;
            legalMoves = boardData.getLegalMoves(turn);
            if (legalMoves == null)
                gameOver(player2Msg + " wins!");
        }

        selectedRow = -1; 

        //handle kardan inke age ye mohre fqt monde ya ye mohre fqt mitone jabeja beshe va baghiye blockan ono automatic select kone baraye karbar
        if (legalMoves != null) { 
            boolean sameFromSquare = true; 
            for (int i = 1; i < legalMoves.length; i++) 
                if (legalMoves[i].fromRow != legalMoves[0].fromRow 
                        || legalMoves[i].fromCol != legalMoves[0].fromCol) { 
                    sameFromSquare = false; 
                    break;
                }
            if (sameFromSquare) { 
                selectedRow = legalMoves[0].fromRow;
                selectedCol = legalMoves[0].fromCol;
            }
        }
        repaint();

    }
    
    //male player harife
    void OPClickedSquare(int row, int col) {
        gameFrame.repaint();
        for (int i = 0; i < legalMoves.length; i++){ 
            if (legalMoves[i].fromRow == row && legalMoves[i].fromCol == col) {
                selectedRow = row; 
                selectedCol = col;
                repaint();
                return;
            }
        }

        if (selectedRow < 0) { 
            return;
        }

        for (int i = 0; i < legalMoves.length; i++){ 
            if (legalMoves[i].fromRow == selectedRow && legalMoves[i].fromCol == selectedCol 
                && legalMoves[i].toRow == row && legalMoves[i].toCol == col) { 
                OPMakeMove(legalMoves[i]); 
                return;
            }
        }
    }
    
    //move fqt baraye player harife in
    void OPMakeMove(GameMove move) {

        boardData.makeMove(move); 

        if (move.jumpCheck()) { 
            legalMoves = boardData.getLegalJumpsFrom(turn, move.toRow, move.toCol);
            if (legalMoves != null) { 
                selectedRow = move.toRow; 
                selectedCol = move.toCol; 
                repaint(); 
                return;
            }
        }

        if (turn == GameMap.player1) { 
            turn = GameMap.player2; 
            legalMoves = boardData.getLegalMoves(turn); 
            if (legalMoves == null) 
                gameOver(player1Msg + " wins!");
        } else { 
            turn = GameMap.player1; 
            legalMoves = boardData.getLegalMoves(turn);
            if (legalMoves == null) 
                gameOver(player2Msg + " wins!");
        }

        selectedRow = -1; 

        if (legalMoves != null) { 
            boolean sameFromSquare = true; 
            for (int i = 1; i < legalMoves.length; i++) 
                if (legalMoves[i].fromRow != legalMoves[0].fromRow 
                        || legalMoves[i].fromCol != legalMoves[0].fromCol) {
                    sameFromSquare = false; 
                    break;
                }
            if (sameFromSquare) { 
                selectedRow = legalMoves[0].fromRow;
                selectedCol = legalMoves[0].fromCol;
            }
        }

        repaint(); 
    }
    
    
    //handle kardan payame daryafti va check kardan inke male hamin game board ya na manade private chat
    public void notifyNewRecived(String message){
        if(message != null && message.contains("||")){
            //check kardan inke male hamin game board bodan
            //form koli &&ferstande##girande rowcol rowcol
            //&&ali##amir 23 34
            if(message.substring(2,message.indexOf("//")).contains(Player2) && message.substring(message.indexOf("//")+2,message.indexOf(" ")).contains(Player1)){
                //agar peyghame close hast yani ontaraft disconnect shode ma ham mibandim panjere ro
                if(message.contains("Close")){
                    gameFrame.dispose();
                    return;
                }
                //parsing harkat anjam shode va emal on be bazi
                int firstSpace = message.indexOf(" ");
                int lastSpace = message.lastIndexOf(" ");
                String from = message.substring(firstSpace+1,lastSpace);
                String to = message.substring(lastSpace+1);
                OPClickedSquare(Integer.parseInt(from.charAt(0)+""), Integer.parseInt(from.charAt(1)+""));
                OPClickedSquare(Integer.parseInt(to.charAt(0)+""), Integer.parseInt(to.charAt(1)+""));
            }
        }
    }
    


    public void paintComponent(Graphics g) { 
        //border dore baord bazi
        g.setColor(new Color(139,119,101));
        g.fillRect(0, 0, 405, 405);
        
        //paint mape bazi
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                //paints morabaha(zamin bazi)
                if ( row % 2 == col % 2 )
                    g.setColor(new Color(139,119,101));
                else
                    g.setColor(new Color(238,203,173));
                g.fillRect(2 + col*50, 2 + row*50, 50, 50);

                //paint morabahayi ke mohreyi rosh gharar gerfte
                switch (boardData.getPieceAt(row,col).toInt()) {
                    case GameMap.player1:
                        g.setColor(Color.lightGray);
                        g.fillOval(7 + col*50, 7 + row*50, 40, 40);
                        break;
                    case GameMap.player2:
                        g.setColor(Color.darkGray);
                        g.fillOval(7 + col*50, 7 + row*50, 40, 40);
                        break;
                    case GameMap.king1:
                        g.setColor(Color.lightGray);
                        g.fillOval(7 + col*50, 7 + row*50, 40, 40);
                        g.setColor(Color.yellow);
                        g.fillOval(12 + col*50, 12 + row*50, 30, 30);
                        break;
                    case GameMap.king2:
                        g.setColor(Color.darkGray);
                        g.fillOval(7 + col*50, 7 + row*50, 40, 40);
                        g.setColor(Color.red);
                        g.fillOval(12 + col*50, 12 + row*50, 30, 30);
                        break;
                }
            }
        }

        if (gameInProgress && ((whichSide == 1 &&  GameMap.player1 == turn) || (whichSide == 2 &&  GameMap.player2 == turn))) {
            g.setColor(new Color(0, 255,0));
            for (int i = 0; i < legalMoves.length; i++) { //baresiye hameye move haye mojaz
                //highlight kardan hame khone hayi ke mishe tekon dad
                g.drawRect(2 + legalMoves[i].fromCol*50, 2 + legalMoves[i].fromRow*50, 50, 50);
            }

            if (selectedRow >= 0){ //agar khone select shode
                g.setColor(Color.white); //onro ba sefid highlight mikonim
                g.drawRect(2 + selectedCol*50, 2 + selectedRow*50, 50, 50);
                g.drawRect(3 + selectedCol*50, 3 + selectedRow*50, 48, 48);
                g.setColor(Color.green);
                //va hame khonehayi ke az onja mishe raft ro sabz mikonim doresh ro
                for (int i = 0; i < legalMoves.length; i++) { 
                    if (legalMoves[i].fromCol == selectedCol && legalMoves[i].fromRow == selectedRow)
                        g.drawRect(2 + legalMoves[i].toCol*50, 2 + legalMoves[i].toRow*50, 50, 50);
                }
            }
        }
    }

    public void mouseEntered(MouseEvent evt) { }
    public void mouseClicked(MouseEvent evt) { }
    public void mouseReleased(MouseEvent evt) { }
    public void mouseExited(MouseEvent evt) { }

}




