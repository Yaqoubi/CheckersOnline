package Chat;

import Game.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.*;
import java.io.*;
import java.sql.Connection;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class MainFrame extends Thread{
    //asami mani daran   
    final JFrame frame = new JFrame("Conection");
    
    final JTextPane globalChat = new JTextPane();
    final JScrollPane globalChatScroll;
    
    final JTextArea userListArea = new JTextArea();
    final JScrollPane userListAreaScroll;
    
    
    final JTextField chatInput = new JTextField();
    final JScrollPane chatInputScrollPane;
    
    final JButton sendBtn;
    final JLabel enterUsernameLbl;
    final JButton disconnectBtn;
    final JLabel profilePicture;
    final JLabel usernameLbl;
    final JTextField userNameField;
    final JTextField passwordField;
    final JButton connectBtn;
    
    private Thread read;
    private String serverAdress = "localhost";
    private int port = 12345;
    private String name = "Username";
    
    BufferedReader in;
    PrintWriter out;
    Socket server;
    
    String lastestRequest = "";
    boolean chatRequest = false;
    boolean gameRequest = false;
    
    List<PrivateChatFrame> privateChatList = new ArrayList<PrivateChatFrame>();
    List<GameFrame> gameFramesList = new ArrayList<GameFrame>();
    List<Component> connectionFrameComponents = new ArrayList<Component>();
    List<Component> globalChatFrameComponents = new ArrayList<Component>();
    Font font = new Font("Arial", Font.PLAIN, 15);
    
    public MainFrame() {
        //init main frame
        frame.getContentPane().setLayout(null);
        //155 150
        frame.setSize(155, 200);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //init pane global chat
        globalChat.setBounds(185, 25, 490, 320);
        globalChat.setFont(font);
        globalChat.setMargin(new Insets(6, 6, 6, 6));
        globalChat.setEditable(false);
        globalChatScroll = new JScrollPane(globalChat);
        globalChatScroll.setBounds(185, 85, 490, 320);
        globalChat.setContentType("text/html");
        globalChat.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
           
        //init list userha
        userListArea.setBounds(520, 25, 156, 320);
        userListArea.setEditable(true);
        userListArea.setFont(font);
        userListArea.setMargin(new Insets(6, 6, 6, 6));
        userListArea.setEditable(false);
        userListAreaScroll = new JScrollPane(userListArea);
        userListAreaScroll.setBounds(25, 85, 156, 320);
        userListArea.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
        
        //init area type kardan karbar
        chatInput.setBounds(0, 410, 400, 50);
        chatInput.setFont(font);
        chatInput.setMargin(new Insets(6, 6, 6, 6));
        chatInputScrollPane = new JScrollPane(chatInput);
        chatInputScrollPane.setBounds(25, 410, 550, 50);
        
        //button send
        sendBtn = new JButton("Send");
        sendBtn.setFont(font);
        sendBtn.setBounds(575, 407, 103, 56);
        
        BufferedImage myPicture = null;
        try {
            myPicture = ImageIO.read(new File("boy.png"));
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        profilePicture = new JLabel(new ImageIcon(myPicture));
        profilePicture.setBounds(25,11,64,64);
        
        Font fontLbl = new Font("Courier", Font.BOLD,22);
        profilePicture.setFont(fontLbl);
        usernameLbl = new JLabel(name);
        usernameLbl.setFont(fontLbl);
        usernameLbl.setBounds(85, 40, 200, 50);
        
        //button Disconnect
        disconnectBtn = new JButton("");
        disconnectBtn.setFont(font);
        disconnectBtn.setBounds(610, 15, 60, 60);
        
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File("exit.png"));
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        disconnectBtn.setIcon(new ImageIcon(img));
        
        //action listener baraye inke agar enter dar text area zad payam ersal beshe ya up ya down zad payam ghabli ke neveshte ro neshon bede
        chatInput.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });
        
        //action listener click ro send baraye ersal payam be server
        sendBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                sendMessage();
            }
        });
        
         //handle kardan click bar roye esm yekarbar baraye ersal darkhast game ya chat be user
        userListArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) {
                    return;
                }
                int offset = userListArea.viewToModel(e.getPoint());
                try {
                    int rowStart = Utilities.getRowStart(userListArea, offset);
                    int rowEnd = Utilities.getRowEnd(userListArea, offset);
                    //peyda kardan username
                    String selectedLine = userListArea.getText().substring(rowStart, rowEnd);
                    //check kardan inke ro esme khod ya jaye khali click nakarde bashe user
                    if(selectedLine.equals("-"+name) || selectedLine.equals("")){
                        return;
                    }
                    selectedLine = "@"+selectedLine.substring(1);
                    
                    String[] choices = { "Game", "Chat" };
                    String result = (String) JOptionPane.showInputDialog(null, "Choose Action",
                            "What To Do!", JOptionPane.QUESTION_MESSAGE, null, // Use
                            // default
                            // icon
                            choices, // Array of choices
                            choices[0]); // Initial choice
                    
                    //hamontori ke zekr shod dar server ersal darkhast bazi ya chat be shekl @username !game ya @userrname !chat
                    if("Game" == result){
                        out.println(selectedLine +" "+"!Game");
                    }else if ("Chat" == result){
                        out.println(selectedLine +" "+"!Chat");
                    }else{
                        
                    }
                } catch (BadLocationException e1) {
                    e1.printStackTrace();
                }
                
            }
        });
        
        //Connection Frame
        userNameField = new JTextField(this.name);
        passwordField = new JTextField("Password");
        connectBtn = new JButton("Connect");
        enterUsernameLbl = new JLabel("Enter Login:");
        
        // position bandi dar frame
        connectBtn.setFont(font);
        userNameField.setBounds(10, 40, 135, 40);
        passwordField.setBounds(10, 85, 135, 40);
        connectBtn.setBounds(25, 130, 100, 40);
        enterUsernameLbl.setBounds(15, 7, 130, 40);
        
        //tayin rane past zamine
        globalChat.setBackground(Color.LIGHT_GRAY);
        userListArea.setBackground(Color.LIGHT_GRAY);
        
        frame.add(connectBtn);
        frame.add(userNameField);
        frame.add(enterUsernameLbl);
        frame.add(passwordField);
        frame.setVisible(true);
        
        connectionFrameComponents.add(connectBtn);
        connectionFrameComponents.add(userNameField);
        connectionFrameComponents.add(enterUsernameLbl);
        connectionFrameComponents.add(passwordField);
        globalChatFrameComponents.add(globalChatScroll);
        globalChatFrameComponents.add(userListAreaScroll);
        globalChatFrameComponents.add(sendBtn);
        globalChatFrameComponents.add(usernameLbl);
        globalChatFrameComponents.add(chatInputScrollPane);
        globalChatFrameComponents.add(disconnectBtn);
        globalChatFrameComponents.add(profilePicture);
        
        //action listener baraye click ro connect btn
        connectBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    //gerftan etelate field ha
                    name = userNameField.getText();
                    serverAdress = "localhost";
                    port = Integer.parseInt("12345");
                    
                    //ijad socket va jaryan vorodi va khoroji
                    server = new Socket(serverAdress, port);
                    in = new BufferedReader(new InputStreamReader(server.getInputStream()));
                    out = new PrintWriter(server.getOutputStream(), true);
                    
                    //ersal username be server baraye inke server check kone ke aya in username hast to server age hast error bargardone
                    //va age nist ghabolesh kone va be hame userha bege ke in user online shode ta list hashon ro update konan
                    out.println(name+" "+passwordField.getText());
                    
                    //ijad threadi baraye daryafte payam haye server
                    read = new ServerInputHandler();
                    read.start();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame,
                            "Could not connect to Server!",
                            "Error!",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        //acction listeneri baraye inke karbar btn disconnet ro zad
        disconnectBtn.addActionListener(new ActionListener()  {
            public void actionPerformed(ActionEvent ae) {
                frame.setTitle("Conection");
                frame.setSize(155, 200);
                updateComponent(FrameState.Connection);
                frame.revalidate();
                frame.repaint();
                read.interrupt();
                userListArea.setText(null);
                globalChat.setBackground(Color.LIGHT_GRAY);
                userListArea.setBackground(Color.LIGHT_GRAY);
                out.close();
            }
        });
    }
    
    void updateComponent(FrameState state){
        if(state == FrameState.Connection){
            for(Component comp:connectionFrameComponents){
                frame.add(comp);
            }
            for(Component comp:globalChatFrameComponents){
                frame.remove(comp);
            }
        }else{
            for(Component comp:connectionFrameComponents){
                frame.remove(comp);
            }
            for(Component comp:globalChatFrameComponents){
                frame.add(comp);
            }
        }
    }
    
    //agar server be ma okey bede ke in username ghanlan gerfte nashode ma taghirati to frame ijad mikoni va mohit chat o inaro namayesh midim
    public void connectionAccepted(){
        updateComponent(FrameState.Chat);
        frame.setTitle("Global Chat");
        frame.setSize(700,500);
        usernameLbl.setText(name);
        frame.revalidate();
        frame.repaint();
        globalChat.setBackground(Color.WHITE);
        userListArea.setBackground(Color.WHITE);
        addToPane(globalChat, "<span><b>(Log) </b>Successfully Connected To The Server"
                +"</span>");
    }
    
    //ersal message be server
    public void sendMessage() {
        try {
            String message = chatInput.getText().trim();
            if (message.equals("")) {
                return;
            }
            if(!lastestRequest.equals("") && (gameRequest || chatRequest) &&(message.equalsIgnoreCase("!Yes") || message.equalsIgnoreCase("!No"))){
                    String username = lastestRequest.substring(8);
                    if(message.equalsIgnoreCase("!Yes")){
                        out.println(lastestRequest);
                        if(gameRequest){
                            gameFramesList.add(new GameFrame(name,username,2,out));
                        }else{
                            privateChatList.add(new PrivateChatFrame(username,serverAdress,port,name,in,out,server));
                        }
                        addToPane(globalChat, "<b>(Request)</b> Request Accepted!");
                    }else{
                        addToPane(globalChat, "<b>(Request)</b> Request Declined!");
                        out.println("!No "+username);
                    }
                    gameRequest = false;
                    chatRequest = false;
                    lastestRequest = "";
                    chatInput.requestFocus();
                    chatInput.setText(null);
            }else{
                //neveshtan ro jaryan khoroji va clean kardan text input
                out.println(message);
                chatInput.requestFocus();
                chatInput.setText(null);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            System.exit(0);
        }
    }
    
    //main method
    public static void main(String[] args) throws Exception {
        new MainFrame();
    }
    
    //threadi baraye daryaft payam haye server va handle kardan on ha
    class ServerInputHandler extends Thread {
        public void run() {
            String message;
            
            //in boolean baraye check avalin payame server hast ke bebinim aya username entekhabi eshghal shode ya na
            boolean checkHandle = true;
            while(!Thread.currentThread().isInterrupted()){
                try {
                    message = in.readLine();
                    if(message != null){
                        //baresiye avalin payam
                        if(checkHandle){
                            checkHandle = false;
                            
                            //check kardan inke aya server bema error dade ast ke username eshghal ast ya na
                            //agar eshghl bod ke error neshon bede va az loop biad biron
                            //vagrna on taghirat lazem ro be seda zadan  connectChanges() anjam bede
                            if(message.contains("!User Exist!")){
                                JOptionPane.showMessageDialog(frame,
                                        "This Nickname Is Already Taken!",
                                        "Error!",
                                        JOptionPane.ERROR_MESSAGE);
                                break;
                            }
                            //error inke user az ghabl to database bode
                            if(message.contains("^^User Already Exist In Database!^^")){
                                JOptionPane.showMessageDialog(frame,
                                "User Already Exist In Database!",
                                "Error!",
                                JOptionPane.ERROR_MESSAGE);
                                break;
                            }
                            //error inke username peyda nashod baraye login
                            if(message.contains("^^User Not Found!^^")){
                                JOptionPane.showMessageDialog(frame,
                                "User Not Found!",
                                "Error!",
                                JOptionPane.ERROR_MESSAGE);
                                break;
                            }
                            //password eshtebah
                            if(message.contains("^^Wrong Password!^^")){
                                JOptionPane.showMessageDialog(frame,
                                "Password Is Wrong!",
                                "Error!",
                                JOptionPane.ERROR_MESSAGE);
                                break;
                            }
                            connectionAccepted();
                        }
                        
                        //Ersal Payam Daryaft Shode Be Hameye Frame Ke Baresi Konan Aya In Payam Male On Hast Ya Na
                        for(PrivateChatFrame frame: privateChatList){
                            frame.notifyNewRecived(message);
                        }
                        for(GameFrame game: gameFramesList){
                            game.getBoard().notifyNewRecived(message);
                        }
                        
                        //Agar Paym az noa khososi ya game bod ignore beshe
                        if(message.contains("))") || message.contains("//")){
                            continue;
                        }
                        //update list user ha [username, username,...]
                        if (message.charAt(0) == '[') {
                            message = message.substring(1, message.length()-1);
                            ArrayList<String> ListUser = new ArrayList<String>(
                                    Arrays.asList(message.split(", "))
                            );
                            userListArea.setText(null);
                            for (String user : ListUser) {
                                userListArea.append("-" + user +"\n");
                            }
                        }else{
                            //Agar Darkhaste chat bod dialogi baraye tayid karbar namayesh bede
                            if(message.contains("!Request Chat")){
                                String userName = message.substring(14);
                                addToPane(globalChat, "<b>(Request) </b>"+"Do You Want To Start Private Chat With "+userName+"?");
                                chatRequest = true;
                                gameRequest = false;
                                lastestRequest = "?ChatYes"+userName;
                                //Agar Darkhaste game bod dialogi baraye tayid karbar namayesh bede
                            }else if(message.contains("!Request Game")){
                                String userName = message.substring(14);
                                addToPane(globalChat, "<b>(Request) </b>"+"Do You Want To Start Game With "+userName+"?");
                                chatRequest = false;
                                gameRequest = true;
                                lastestRequest = "?GameYes"+userName;
                            }
                            //agar karbar ghabol kard invar ham frame ro baz kon hala agar chat bod ya game
                            else if(message.contains("?Chat?")){
                                addToPane(globalChat, "<b>(Request)</b> Request Accepted!");
                                String userName = message.substring(7);
                                privateChatList.add(new PrivateChatFrame(userName,serverAdress,port,name,in,out,server));
                            }
                            else if(message.contains("?Game?")){
                                addToPane(globalChat, "<b>(Request)</b> Request Accepted!");
                                String userName = message.substring(7);
                                gameFramesList.add(new GameFrame(name,userName,1,out));
                            }else if(message.contains("?No")){
                                addToPane(globalChat, "<b>(Request)</b> Request Declined!");
                            //dar gheyre in halat haye bala payam az noa global bode va namayeshesh bede
                            }else{
                                addToPane(globalChat, message);
                            }
                        }
                    }
                }
                catch (IOException ex) {
                }
            }
        }
    }
    
    //namayesh string haye html dar pane
    //dalile estefade az pane baraye bold kardan va rangi kardan ba estefade az html ast
    private void addToPane(JTextPane pane, String messageToAdd){
        HTMLDocument doc = (HTMLDocument)pane.getDocument();
        HTMLEditorKit editorKit = (HTMLEditorKit)pane.getEditorKit();
        try {
            editorKit.insertHTML(doc, doc.getLength(), messageToAdd, 0, 0, null);
            pane.setCaretPosition(doc.getLength());
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
