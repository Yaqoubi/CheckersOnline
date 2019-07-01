package Chat;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.*;
import java.io.*;
import javax.swing.*;


public class PrivateChatFrame extends Thread{
    
    
    //Chat Frame baraye chat haye private hast va besyar shabihe client gui ast    
    final JTextArea chatTextArea = new JTextArea();
    final JTextField chatTextInput = new JTextField();
    final JFrame chatFrame;
    final JLabel title;
    private String oldMsg = "";
    private Thread read;
    private String serverName;
    private int PORT;
    private String name;
    private String otherSideName;
    BufferedReader input;
    PrintWriter output;
    Socket server;
    
    //init frame
    public PrivateChatFrame(String frameName,String serverName,int PORT,String name,BufferedReader input,PrintWriter output,Socket server) {
        chatFrame = new JFrame("Private Chat");
        otherSideName = frameName;
        this.serverName = "localhost";
        this.PORT = 12345;
        this.name = name;
        String fontfamily = "Arial, sans-serif";
        Font font = new Font(fontfamily, Font.PLAIN, 15);
        chatFrame.getContentPane().setLayout(null);
        chatFrame.setSize(540, 480);
        chatFrame.setResizable(false);
        chatFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chatTextArea.setBounds(25, 25, 490, 320);
        chatTextArea.setFont(font);
        chatTextArea.setMargin(new Insets(6, 6, 6, 6));
        chatTextArea.setEditable(false);
        JScrollPane jtextFilDiscuSP = new JScrollPane(chatTextArea);
        jtextFilDiscuSP.setBounds(25, 55, 490, 320);
        chatTextArea.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
        chatTextInput.setBounds(0, 350, 240, 50);
        chatTextInput.setFont(font);
        chatTextInput.setMargin(new Insets(6, 6, 6, 6));
        final JScrollPane jtextInputChatSP = new JScrollPane(chatTextInput);
        jtextInputChatSP.setBounds(25, 380, 390, 50);
        final JButton jsbtn = new JButton("Send");
        jsbtn.setFont(font);
        jsbtn.setBounds(420, 377, 98, 55);
        chatTextInput.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    String currentMessage = chatTextInput.getText().trim();
                    chatTextInput.setText(oldMsg);
                    oldMsg = currentMessage;
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    String currentMessage = chatTextInput.getText().trim();
                    chatTextInput.setText(oldMsg);
                    oldMsg = currentMessage;
                }
            }
        });
        jsbtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                sendMessage();
            }
        });

        chatTextArea.setBackground(Color.LIGHT_GRAY);
        chatFrame.add(jtextFilDiscuSP);
        chatFrame.setVisible(true);
        title = new JLabel(name+" with "+frameName);
        Font fontLbl = new Font("Courier", Font.BOLD,18);
        title.setFont(fontLbl);
        title.setBounds(30,5,300,50);
        try {
            this.server = server;
            this.input = new BufferedReader(new InputStreamReader(server.getInputStream()));
            this.output = new PrintWriter(server.getOutputStream(), true);
            chatFrame.add(jsbtn);
            chatFrame.add(jtextInputChatSP);
            chatFrame.add(title);
            chatFrame.revalidate();
            chatFrame.repaint();
            chatTextArea.setBackground(Color.WHITE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(chatFrame, ex.getMessage());
        }
        chatFrame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                sendDisconnectCommand();
            }
        });
        
    }
    
    //ersal payame inke karbar frame ro baste ke be tarafe moghabel ham server payam bede frame ro bebande
    void sendDisconnectCommand(){
        String message = "(("+this.name+"))"+otherSideName+" "+"C10s3";
        output.println(message);
        chatFrame.dispose();
    }
    
    public void notifyNewRecived(String message){
        //check kardan inke payam male private chat ha bashe
        if(message != null && message.contains("))")){
            //ba check kardan ferstande va girande motmaen mishim ke make hamin frame payam
            if(message.substring(2,message.indexOf("))")).contains(otherSideName) && message.substring(message.indexOf("))")+2,message.indexOf(" ")).contains(this.name)){
                //agar payam in bod ke karbar moghable frame ro baste ma ham invar frame ro mibandim
                if(message.contains("C10s3")){
                    chatFrame.dispose();
                    return;
                }
                //dar gheyre in sorat payam ro namayesh midahim
                chatTextArea.append(message.substring(2,message.indexOf("))"))+":"+message.substring(message.indexOf(" ")+1)+"\n");
            }
        }
    }
    
    //ersal payam be server be hamon noa private message
    //((ferstande))girande "matne payam"
    public void sendMessage() {
        try {
            String message = chatTextInput.getText().trim();
            if (message.equals("")) {
                return;
            }
            this.oldMsg = message;
            message = "(("+this.name+"))"+otherSideName+" "+message;
            output.println(message);
            chatTextArea.append(name+":"+message.substring(message.indexOf(" ")+1)+"\n");
            chatTextInput.requestFocus();
            chatTextInput.setText(null);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            System.exit(0);
        }
    }
    
}