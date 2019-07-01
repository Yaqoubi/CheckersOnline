package Game;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import javax.swing.*;
import sun.applet.Main;

public class GameFrame extends JFrame { 
    
    //Board Bazi
    private GameBoard board;
    
    public GameFrame(String name,String opName,int whichSide,PrintWriter output){
        
        //Setting haye frame asli
        setTitle(name+" with "+opName);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane();
        pack();
        //setSize(324,346);
        int width = 404;
        int heigh = 426;
        setSize(width,heigh);
        setResizable(false);
        setLayout(null);
        setBackground(new Color(225, 225, 225));

        //ijad yek board bazi va ezafe kardan on be frame
        board = new GameBoard(name,opName,whichSide,output,this);
        add(board);
        
        //gharar dadan har componnet sare jaye sahihesh
        board.setBounds(0,0,width,heigh);
        //listener baraye inke karbar panjerero bast be server etela bedim ma disconect shodim ta
        //be tarafe moghabel ham peygham bastan panjere ersal besje
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                board.sendDisconnectCommand();
            }
        });
        setVisible(true);
    }
    
    public GameBoard getBoard(){
       return board;
    }
    
}
