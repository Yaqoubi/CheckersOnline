package Chat;

import User.*;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
  //Port Connection
  private int port;
  //List users ha
  private List<User> users;
  private List<User> AllUsers;
  //Socket Server
  private ServerSocket server;
  
  //Constructor Baraye Gerftan Port Va New Kardan List
  public Server(int port) {
    this.port = port;
    this.users = new ArrayList<User>();
    AllUsers = new ArrayList<User>();
    //Load Kardan Tamame User Ha Dar Yel List
    BufferedReader reader = null;
      try {
          reader = new BufferedReader(new FileReader (new File("users.txt")));
          String line = null;
          while((line = reader.readLine()) != null) {
              String[] userPass = line.split(":::");
              if(userPass.length == 2){
                AllUsers.add(new User(null, userPass[0], userPass[1]));
              }
          }
      } catch (FileNotFoundException ex) {
          Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IOException ex) {
          Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
      } finally {
          try {
              reader.close();
          } catch (IOException ex) {
              Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
          }
      }
  }
  
  //Run Kardan Server Ro Port 12345
  public static void main(String[] args) throws IOException {
    new Server(12345).run();
  }
  
  //Start Server Va Shora Daryaft Client
  public void run() throws IOException {
    //Sakhte Socket Server
    server = new ServerSocket(port);
    System.out.println("Server Is Online!");
    Outer: while(true) {
      String nickname="";
      String password="";
      //Paziresh Client Ha
      Socket userSocket = server.accept();
      String message = (new Scanner ( userSocket.getInputStream() )).nextLine();
      boolean find = false;
      nickname = message.substring(0,message.lastIndexOf(" "));
      password = message.substring(message.lastIndexOf(" ")+1);
      User newUser = new User(userSocket, nickname,password);
          for (User login : AllUsers) {
            if (login.getName().equals(nickname)) {
                find = true;
                if(!login.getPassword().equals(password)){
                    newUser.getOutStream().println("^^Wrong Password!^^");
                    userSocket.close();
                    continue Outer;
                }
            }
          }
          //agar user ro peyda konim dar list yani ghablan register shode peygham error ro ersal mikonim be karbar
          //va socket ro mibandim
          if(!find){
               try {
              Files.write(Paths.get("users.txt"), ("\n"+nickname+":::"+password).getBytes(), StandardOpenOption.APPEND);
                    AllUsers.add(newUser);
                }catch (IOException e) {
                //exception handling left as an exercise for the reader
                }    
     
          }
      if(findUser(nickname) != null){
        //Agar nickname peyda shod peyghame error ra be client ersal mikonim 
        sendError(newUser, "!User Exist!");
        userSocket.close();
        continue;
      }
      //ezafe kardan be list
      this.users.add(newUser);
      //sakht thread jadid baraye handle kardan payam haye user sakhte shode
      new Thread(new UserMessagesHandler(this, newUser)).start();
    }
  }
  
  void sendError(User user,String error){
      user.getOutStream().println(error);
  }
  
  //Peyda Kardan Yek User Dar List
  User findUser(String username){
      for (User nameCheck : this.users) {
        if (nameCheck.getName().equals(username)) {
            return nameCheck;
        }
      }
      return null;
  }

  //pak kardan user az list userha
  public void removeUser(User user){
    this.users.remove(user);
  }

  //ersal message be hamye karbarha
  public void sendToAllHTMLFormat(String message, User sender) {
    for (User client : this.users) {
      client.getOutStream().println(sender.toString() + "<span>: " +message+"</span>");
    }
  }
  //ersal message be hamye karbarha darhalate adi va na html
  public void sendToAll(String msg) {
    for (User client : this.users) {
      client.getOutStream().println(msg);
    }
  }
  
  //upfate kardan list karbarha ba bordcasr an
  public void updateUserList(){
    for (User client : this.users) {
      client.getOutStream().println(this.users);
    }
  }
 
  //ersal payam va request be yek user khas
  public void sendToUser(String message, String user){
    User findUser = findUser(user);
    if(findUser != null){
        findUser.getOutStream().println(message);
    }else{
        System.err.println("Cannot Find User!");
    }
  }
}



